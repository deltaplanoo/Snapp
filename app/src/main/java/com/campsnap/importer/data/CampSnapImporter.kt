package com.campsnap.importer.data

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.documentfile.provider.DocumentFile
import com.campsnap.importer.model.CameraPhoto
import com.campsnap.importer.model.ImportBatchResult
import com.campsnap.importer.model.ImportPhase
import com.campsnap.importer.model.ImportProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.Date
import java.util.Locale

/**
 * Interface defining the contract for importing photos from Camp Snap cameras.
 */
interface CampSnapImporterEngine {
    suspend fun scanCameraPhotos(treeUri: Uri): List<CameraPhoto>

    suspend fun importAndClearPhotos(
        treeUri: Uri,
        destinationAlbum: String,
        clearCameraAfter: Boolean = true,
        onProgress: suspend (ImportProgress) -> Unit
    ): ImportBatchResult
}

/**
 * Two-phase import engine ("Copy-Verify-Prune") designed specifically for Camp Snap cameras.
 *
 * Implements:
 * 1. SAF directory traversal (Root and DCIM subdirectories).
 * 2. Collision-proof EXIF renaming.
 * 3. MediaStore streaming with IS_PENDING = 1.
 * 4. Byte-length verification.
 * 5. Safe pruning of original files from camera only after complete verification.
 */
class CampSnapImporter(
    private val context: Context
) : CampSnapImporterEngine {

    companion object {
        private const val BUFFER_SIZE = 64 * 1024 // 64 KB buffer
        private val IGNORED_DIRECTORY_NAMES = setOf(
            ".trashes",
            ".trash",
            ".fseventsd",
            ".spotlight-v100",
            "system volume information",
            "android",
            "lost.dir",
            "__macosx"
        )
    }

    /**
     * Scans the given SAF tree for photos.
     */
    override suspend fun scanCameraPhotos(treeUri: Uri): List<CameraPhoto> = withContext(Dispatchers.IO) {
        val rootDir = DocumentFile.fromTreeUri(context, treeUri)
            ?: return@withContext emptyList()

        if (!rootDir.canRead()) {
            return@withContext emptyList()
        }

        val photos = mutableListOf<CameraPhoto>()
        traverseDirectory(rootDir, photos, currentDepth = 0, maxDepth = 4)

        // Sort chronologically by last modified date, then by filename
        photos.sortedWith(compareBy({ it.lastModified }, { it.originalName }))
    }

    /**
     * Executes the safe two-phase transfer:
     * Phase 1 (Copy & Verify): Stream into MediaStore, check byte length, publish with IS_PENDING = 0.
     * Phase 2 (Pruning): Safely delete verified files from camera.
     */
    override suspend fun importAndClearPhotos(
        treeUri: Uri,
        destinationAlbum: String,
        clearCameraAfter: Boolean,
        onProgress: suspend (ImportProgress) -> Unit
    ): ImportBatchResult = withContext(Dispatchers.IO) {
        val photos = scanCameraPhotos(treeUri)
        val albumName = destinationAlbum.trim().ifEmpty { "Camp Snap" }
        val relativePath = "Pictures/$albumName"

        if (photos.isEmpty()) {
            return@withContext ImportBatchResult(
                totalFound = 0,
                successfullyImported = 0,
                successfullyDeleted = 0,
                destinationPath = relativePath
            )
        }

        val batchTimestamp = Date()
        val totalCount = photos.size
        val verifiedCopiedFiles = mutableListOf<DocumentFile>()
        val failedFiles = mutableListOf<String>()

        // ==========================================
        // Phase 1: Copy & Verify
        // ==========================================
        for ((indexZero, photo) in photos.withIndex()) {
            val oneBasedIndex = indexZero + 1

            // 1. Generate unique collision-proof target filename
            val targetFilename = generateCollisionProofFilename(
                photo = photo,
                batchTimestamp = batchTimestamp,
                index = oneBasedIndex,
                relativePath = relativePath
            )

            onProgress(
                ImportProgress(
                    currentIndex = oneBasedIndex,
                    totalCount = totalCount,
                    currentFileName = targetFilename,
                    phase = ImportPhase.COPY_AND_VERIFY
                )
            )

            var mediaUri: Uri? = null
            var copySuccess = false

            try {
                // 2. Prepare MediaStore ContentValues with IS_PENDING = 1
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, targetFilename)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, relativePath)
                    put(MediaStore.Images.Media.IS_PENDING, 1)

                    if (photo.lastModified > 0) {
                        put(MediaStore.Images.Media.DATE_TAKEN, photo.lastModified)
                        put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
                    }
                }

                mediaUri = context.contentResolver.insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    values
                ) ?: throw IOException("Failed to create MediaStore entry for $targetFilename")

                // 3. Stream bytes with verification
                val bytesCopied = streamAndVerify(photo.documentFile.uri, mediaUri)
                val expectedBytes = photo.sizeBytes

                // Verification check:
                if (expectedBytes > 0 && bytesCopied != expectedBytes) {
                    throw IOException(
                        "Byte length mismatch for $targetFilename: copied $bytesCopied bytes, expected $expectedBytes bytes"
                    )
                }

                if (bytesCopied == 0L) {
                    throw IOException("File copied with 0 bytes: $targetFilename")
                }

                // 4. Mark IS_PENDING = 0 to publish photo to Android gallery
                val publishValues = ContentValues().apply {
                    put(MediaStore.Images.Media.IS_PENDING, 0)
                }
                context.contentResolver.update(mediaUri, publishValues, null, null)

                verifiedCopiedFiles.add(photo.documentFile)
                copySuccess = true

            } catch (e: Exception) {
                // Incomplete or failed copy: delete orphan MediaStore entry
                mediaUri?.let { uri ->
                    try {
                        context.contentResolver.delete(uri, null, null)
                    } catch (_: Exception) {
                    }
                }
                failedFiles.add(photo.originalName)
            }

            // If an individual file fails, do not proceed to prune failed files
        }

        // ==========================================
        // Phase 2: Cleanup & Safe Pruning
        // ==========================================
        var deletedCount = 0

        // Only prune if Phase 1 completed with zero fatal errors and clearCameraAfter is true
        if (clearCameraAfter && failedFiles.isEmpty() && verifiedCopiedFiles.isNotEmpty()) {
            onProgress(
                ImportProgress(
                    currentIndex = totalCount,
                    totalCount = totalCount,
                    currentFileName = "Clearing camera files...",
                    phase = ImportPhase.PRUNING_CAMERA
                )
            )

            for (docFile in verifiedCopiedFiles) {
                try {
                    if (docFile.delete()) {
                        deletedCount++
                    }
                } catch (_: Exception) {
                    // Safe catch - photo is already verified in user gallery
                }
            }
        }

        ImportBatchResult(
            totalFound = totalCount,
            successfullyImported = verifiedCopiedFiles.size,
            successfullyDeleted = deletedCount,
            destinationPath = relativePath,
            failedFiles = failedFiles,
            errorMessage = if (failedFiles.isNotEmpty()) {
                "${failedFiles.size} photos failed to copy. Originals were preserved on the camera."
            } else null
        )
    }

    /**
     * Streams input SAF file into MediaStore output stream and counts total bytes written.
     */
    private fun streamAndVerify(sourceUri: Uri, destinationUri: Uri): Long {
        val resolver: ContentResolver = context.contentResolver
        var inputStream: InputStream? = null
        var outputStream: OutputStream? = null
        var totalBytesWritten = 0L

        try {
            inputStream = resolver.openInputStream(sourceUri)
                ?: throw IOException("Cannot open input stream: $sourceUri")
            outputStream = resolver.openOutputStream(destinationUri, "w")
                ?: throw IOException("Cannot open output stream: $destinationUri")

            val buffer = ByteArray(BUFFER_SIZE)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                totalBytesWritten += bytesRead
            }
            outputStream.flush()
        } finally {
            try {
                inputStream?.close()
            } catch (_: Exception) {}
            try {
                outputStream?.close()
            } catch (_: Exception) {}
        }

        return totalBytesWritten
    }

    /**
     * Traverses the directory structure to collect valid JPEG files.
     */
    private fun traverseDirectory(
        directory: DocumentFile,
        output: MutableList<CameraPhoto>,
        currentDepth: Int,
        maxDepth: Int
    ) {
        if (currentDepth > maxDepth) return

        val files = try {
            directory.listFiles()
        } catch (e: Exception) {
            emptyArray()
        }

        for (file in files) {
            val name = file.name ?: continue
            val lowerName = name.lowercase(Locale.ROOT)

            if (file.isDirectory) {
                if (lowerName !in IGNORED_DIRECTORY_NAMES && !name.startsWith(".")) {
                    traverseDirectory(file, output, currentDepth + 1, maxDepth)
                }
            } else if (file.isFile) {
                if (isCampSnapPhoto(name)) {
                    output.add(
                        CameraPhoto(
                            documentFile = file,
                            originalName = name,
                            sizeBytes = file.length(),
                            lastModified = file.lastModified()
                        )
                    )
                }
            }
        }
    }

    private fun isCampSnapPhoto(name: String): Boolean {
        val lower = name.lowercase(Locale.ROOT)
        if (name.startsWith(".") || name.startsWith("._")) return false
        return lower.endsWith(".jpg") || lower.endsWith(".jpeg")
    }

    /**
     * Resolves target filename with EXIF analysis and MediaStore collision check.
     */
    private fun generateCollisionProofFilename(
        photo: CameraPhoto,
        batchTimestamp: Date,
        index: Int,
        relativePath: String
    ): String {
        val inputStream = try {
            context.contentResolver.openInputStream(photo.documentFile.uri)
        } catch (e: Exception) {
            null
        }

        val baseTargetName = CampSnapRenamer.generateTargetName(
            inputStream = inputStream,
            batchTimestamp = batchTimestamp,
            index = index
        )

        // Ensure no collision in MediaStore under relativePath
        return resolveMediaStoreCollision(baseTargetName, relativePath)
    }

    /**
     * Checks if a filename already exists in MediaStore under the given relative path.
     * If so, appends an incrementing counter before the extension.
     */
    private fun resolveMediaStoreCollision(baseName: String, relativePath: String): String {
        var candidate = baseName
        var counter = 1

        val nameWithoutExt = if (baseName.contains(".")) {
            baseName.substringBeforeLast(".")
        } else {
            baseName
        }
        val ext = if (baseName.contains(".")) {
            ".${baseName.substringAfterLast(".")}"
        } else {
            ".jpg"
        }

        while (isFilenameTakenInMediaStore(candidate, relativePath)) {
            candidate = "${nameWithoutExt}_${counter}${ext}"
            counter++
        }

        return candidate
    }

    private fun isFilenameTakenInMediaStore(displayName: String, relativePath: String): Boolean {
        val projection = arrayOf(MediaStore.Images.Media._ID)
        val selection = "${MediaStore.Images.Media.DISPLAY_NAME} = ? AND ${MediaStore.Images.Media.RELATIVE_PATH} = ?"
        val pathWithSlash = if (relativePath.endsWith("/")) relativePath else "$relativePath/"
        val selectionArgs = arrayOf(displayName, pathWithSlash)

        return try {
            context.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                null
            )?.use { cursor ->
                cursor.count > 0
            } ?: false
        } catch (e: Exception) {
            false
        }
    }
}
