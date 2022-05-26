package com.udacity.asteroidradar.utils

import com.udacity.asteroidradar.Constants
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {
    companion object {
        fun getStartAndEndDates(daysToInclude: Int): Pair<String, String> {
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
            val startDate = dateFormat.format(calendar.time)
            calendar.add(Calendar.DAY_OF_YEAR, daysToInclude)
            val endDate = dateFormat.format(calendar.time)
            return Pair(startDate, endDate)
        }
    }
}