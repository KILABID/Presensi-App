package com.murad.presensi.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import java.util.*

class UtilsTIme {

    fun getDateAndTimeInIndonesia(timeZone: String): Pair<String, String> {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZone))

        val dateFormatter = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID"))
        val timeFormatter = SimpleDateFormat("HH:mm", Locale.getDefault())

        dateFormatter.timeZone = calendar.timeZone
        timeFormatter.timeZone = calendar.timeZone

        val date = dateFormatter.format(calendar.time)
        val time = timeFormatter.format(calendar.time)

        return Pair(date, time)
    }

    fun parseDate(dateString: String): Date? {
        return try {
            val format = SimpleDateFormat("EEEE, d MMMM yyyy", Locale("id", "ID")) // Format sesuai dengan data
            format.parse(dateString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun compareDates(dateString1: String, dateString2: String): Int {
        val date1 = parseDate(dateString1)
        val date2 = parseDate(dateString2)

        return when {
            date1 == null || date2 == null -> {
                // Handle error: one of the dates is invalid
                0 // or throw an exception
            }
            date1.before(date2) -> -1 // date1 is before date2
            date1.after(date2) -> 1 // date1 is after date2
            else -> 0 // dates are equal
        }
    }
}