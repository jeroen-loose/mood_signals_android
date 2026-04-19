package com.loosethread.moodsignals.helpers

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateHelper {
    private lateinit var sqlFormat : SimpleDateFormat
    private val humanReadableFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault())

    fun init(pattern: String) {
        this.sqlFormat = SimpleDateFormat(pattern)
    }

    fun formatForDisplay(date: String) : String {
        return humanReadableFormat.format(sqlFormat.parse(date))
    }

    fun formatForSql(date: Date) : String {
        return sqlFormat.format(date)
    }
}