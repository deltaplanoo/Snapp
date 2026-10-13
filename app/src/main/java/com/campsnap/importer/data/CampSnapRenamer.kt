package com.campsnap.importer.data

import androidx.exifinterface.media.ExifInterface
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Handles collision-proof renaming for Camp Snap camera photos.
 *
 * Camp Snap cameras reset file sequences to IMG_0001.JPG when formatted.
 * This class inspects EXIF metadata for TAG_DATETIME_ORIGINAL:
 * - If valid (> 2020 and <= now + 1 year): CS_YYYYMMDD_HHMMSS_XXX.jpg
 * - If missing or default epoch (1970/2020): CS_<ImportTimestamp>_<Index>.jpg
 */
object CampSnapRenamer {

    private const val EXIF_DATE_PATTERN = "yyyy:MM:dd HH:mm:ss"
    private const val FILENAME_DATE_PATTERN = "yyyyMMdd_HHmmss"

    /**
     * Extracts date and generates the collision-proof target filename.
     *
     * @param inputStream Stream to the photo bytes for ExifInterface parsing.
     * @param batchTimestamp Timestamp representing when this import session began.
     * @param index 1-based index in the current batch.
     */
    fun generateTargetName(
        inputStream: InputStream?,
        batchTimestamp: Date,
        index: Int
    ): String {
        val exifDate = inputStream?.use { stream ->
            parseExifDate(stream)
        }

        val indexSuffix = String.format(Locale.US, "%03d", index)

        return if (exifDate != null && isValidExifDate(exifDate)) {
            val dateStr = SimpleDateFormat(FILENAME_DATE_PATTERN, Locale.US).format(exifDate)
            "CS_${dateStr}_${indexSuffix}.jpg"
        } else {
            val batchStr = SimpleDateFormat(FILENAME_DATE_PATTERN, Locale.US).format(batchTimestamp)
            "CS_${batchStr}_${indexSuffix}.jpg"
        }
    }

    /**
     * Parses EXIF TAG_DATETIME_ORIGINAL (or TAG_DATETIME as fallback).
     */
    fun parseExifDate(inputStream: InputStream): Date? {
        return try {
            val exif = ExifInterface(inputStream)
            val dateString = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)
                ?: exif.getAttribute(ExifInterface.TAG_DATETIME)

            if (!dateString.isNullOrBlank()) {
                val parser = SimpleDateFormat(EXIF_DATE_PATTERN, Locale.US)
                parser.isLenient = false
                parser.parse(dateString.trim())
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Validates whether an EXIF date is genuine or a camera clock reset default (1970 or 2020).
     */
    fun isValidExifDate(date: Date): Boolean {
        val cal = Calendar.getInstance()
        cal.time = date
        val year = cal.get(Calendar.YEAR)

        // Camp Snap cameras default to 1970 (Unix Epoch) or 2020 (Firmware factory default)
        if (year <= 2020) {
            return false
        }

        // Avoid future corrupt dates
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        if (year > currentYear + 2) {
            return false
        }

        return true
    }
}
