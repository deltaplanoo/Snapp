package com.campsnap.importer

import com.campsnap.importer.data.CampSnapRenamer
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CampSnapRenamingTest {

    private val batchDate: Date = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).parse("20261012_113000")!!

    @Test
    fun testFallbackRenamingWhenNoExif() {
        val filename = CampSnapRenamer.generateTargetName(
            inputStream = null,
            batchTimestamp = batchDate,
            index = 1
        )
        assertEquals("CS_20261012_113000_001.jpg", filename)
    }

    @Test
    fun testFallbackRenamingWithPaddedIndex() {
        val filename42 = CampSnapRenamer.generateTargetName(
            inputStream = null,
            batchTimestamp = batchDate,
            index = 42
        )
        assertEquals("CS_20261012_113000_042.jpg", filename42)

        val filename123 = CampSnapRenamer.generateTargetName(
            inputStream = null,
            batchTimestamp = batchDate,
            index = 123
        )
        assertEquals("CS_20261012_113000_123.jpg", filename123)
    }

    @Test
    fun testEpoch1970Validation() {
        val cal = Calendar.getInstance().apply {
            set(1970, Calendar.JANUARY, 1, 0, 0, 0)
        }
        assertFalse("Year 1970 must be considered invalid epoch", CampSnapRenamer.isValidExifDate(cal.time))
    }

    @Test
    fun testEpoch2020Validation() {
        val cal = Calendar.getInstance().apply {
            set(2020, Calendar.JANUARY, 1, 0, 0, 0)
        }
        assertFalse("Year 2020 must be considered invalid firmware default", CampSnapRenamer.isValidExifDate(cal.time))
    }

    @Test
    fun testValidRecentDateValidation() {
        val cal = Calendar.getInstance().apply {
            set(2024, Calendar.JUNE, 15, 14, 20, 10)
        }
        assertTrue("Year 2024 should be valid", CampSnapRenamer.isValidExifDate(cal.time))
    }
}
