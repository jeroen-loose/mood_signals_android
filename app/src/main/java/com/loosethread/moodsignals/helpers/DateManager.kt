package com.loosethread.moodsignals.helpers

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateManager {
    private lateinit var sqlFormat : SimpleDateFormat
    private var selectedDate = Date()
    private val humanReadableFormat = DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault())
    private var calendar = Calendar.getInstance()

    fun init(pattern: String) {
        this.sqlFormat = SimpleDateFormat(pattern)
    }

    /*
    fun setDate(date: String) {
       //date = sqlFormat.parse(date).toString()
    }

    }

    */
    fun setDate(y: Int, m: Int, d: Int) {
        calendar.apply{
            set(Calendar.YEAR, y)
            set(Calendar.MONTH, m)
            set(Calendar.DAY_OF_MONTH, d)
        }
        selectedDate = calendar.time
    }

    /*

    fun setDate(date: Date) {
        this.date = date
    }
    */

    fun formatForDisplay(date: String? = null) : String {
        if (date == null) {
            return humanReadableFormat.format(selectedDate)
        } else {
            return humanReadableFormat.format(sqlFormat.parse(date))
        }
    }

    fun formatStringForDisplay(dateString: String) : String {
        return humanReadableFormat.format(sqlFormat.parse(dateString))
    }

    fun formatForSql() : String {
        return sqlFormat.format(selectedDate)
    }

    fun getYear(): Int {
        return calendar.get(Calendar.YEAR)
    }

    fun getMonth(): Int {
        return calendar.get(Calendar.MONTH)
    }

    fun getDay(): Int {
        return calendar.get(Calendar.DAY_OF_MONTH)
    }
}