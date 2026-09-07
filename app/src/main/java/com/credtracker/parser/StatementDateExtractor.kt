package com.credtracker.parser

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object StatementDateExtractor {

    private val DATE_FORMATS = listOf(
        "dd-MMM-yyyy",
        "dd-MMM-yy",
        "dd/MM/yyyy",
        "dd/MM/yy",
        "dd-MM-yyyy",
        "dd-MM-yy",
        "dd MMM yyyy",
        "dd MMMM yyyy",
        "MMM dd, yyyy",
        "MMMM dd, yyyy",
        "yyyy-MM-dd"
    )

    fun parseDate(rawDateStr: String): Long? {
        val cleaned = rawDateStr.trim()
            .replace(Regex("""(?<=\d)(st|nd|rd|th)"""), "") // Remove ordinal suffixes 1st, 2nd, etc.
            .replace(",", " ")
            .replace(Regex("""\s+"""), " ")
            .trim()

        for (format in DATE_FORMATS) {
            try {
                val sdf = SimpleDateFormat(format, Locale.ENGLISH)
                sdf.isLenient = false
                val date = sdf.parse(cleaned)
                if (date != null) {
                    return date.time
                }
            } catch (_: Exception) {
                // Try next format
            }
        }

        // Try short format without year (e.g. "15-Sep" or "15 Sep")
        val shortFormats = listOf("dd-MMM", "dd MMM", "MMM dd", "dd/MM")
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        for (format in shortFormats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.ENGLISH)
                sdf.isLenient = false
                val date = sdf.parse(cleaned)
                if (date != null) {
                    val cal = Calendar.getInstance()
                    val targetCal = Calendar.getInstance().apply { time = date }
                    cal.set(Calendar.MONTH, targetCal.get(Calendar.MONTH))
                    cal.set(Calendar.DAY_OF_MONTH, targetCal.get(Calendar.DAY_OF_MONTH))
                    // If target date in current year is already more than 60 days in past, assume next year
                    if (cal.timeInMillis < System.currentTimeMillis() - 60L * 24 * 60 * 60 * 1000) {
                        cal.add(Calendar.YEAR, 1)
                    }
                    return cal.timeInMillis
                }
            } catch (_: Exception) {
                // Try next
            }
        }

        return null
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        return sdf.format(Date(timestamp))
    }

    fun getDaysRemaining(dueTimestamp: Long): Long {
        val diff = dueTimestamp - System.currentTimeMillis()
        return (diff / (1000 * 60 * 60 * 24))
    }
}
