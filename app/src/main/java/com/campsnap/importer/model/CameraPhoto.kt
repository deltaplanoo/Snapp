package com.campsnap.importer.model

import androidx.documentfile.provider.DocumentFile

/**
 * Represents a photo file detected on the Camp Snap camera.
 */
data class CameraPhoto(
    val documentFile: DocumentFile,
    val originalName: String,
    val sizeBytes: Long,
    val lastModified: Long
)

/**
 * Progress details emitted during the transfer engine execution.
 */
data class ImportProgress(
    val currentIndex: Int,
    val totalCount: Int,
    val currentFileName: String,
    val phase: ImportPhase = ImportPhase.COPY_AND_VERIFY
) {
    val progressFraction: Float
        get() = if (totalCount > 0) currentIndex.toFloat() / totalCount else 0f

    val percentage: Int
        get() = (progressFraction * 100).toInt()
}

enum class ImportPhase {
    SCANNING,
    COPY_AND_VERIFY,
    PRUNING_CAMERA,
    COMPLETE
}

/**
 * Result of the two-phase import process.
 */
data class ImportBatchResult(
    val totalFound: Int,
    val successfullyImported: Int,
    val successfullyDeleted: Int,
    val destinationPath: String,
    val failedFiles: List<String> = emptyList(),
    val errorMessage: String? = null
)
