package com.loosethread.moodsignals.helpers

import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.datatypes.Day
import com.loosethread.moodsignals.datatypes.LogDay
import java.time.LocalDate
import java.time.temporal.IsoFields

class DaysLogByWeek {
    companion object {
        private lateinit var day: Day
        private val result = mutableMapOf<Int, MutableMap<Int, LogDay>>()

        fun groupDayScoresByWeek(dayScores: MutableList<LogDay>) {

            for (dayScore in dayScores) {
                day = Db.getDay(dayScore.dayId)
                val yearInt = day.date.substring(0, 4).toInt()
                val monthInt = day.date.substring(5, 7).toInt()
                val dayInt = day.date.substring(8, 10).toInt()

                val localDate = LocalDate.of(yearInt, monthInt, dayInt)

                val week = localDate.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR)
                val weekYear = localDate.get(IsoFields.WEEK_BASED_YEAR)
                val weekday = localDate.dayOfWeek.value
                val weekIndex = String.format("%04d%02d", weekYear, week).toInt()

                if (result[weekIndex] == null)
                    result[weekIndex] = mutableMapOf<Int, LogDay>()

                result[weekIndex]?.set(weekday, dayScore)
            }
        }

        fun getWeek(week: Int): MutableMap<Int, LogDay>? {
            return result[week]
        }

        fun getWeeks(): MutableMap<Int, MutableMap<Int, LogDay>> {
            return result
        }

        fun getSize() : Int {
            return result.size
        }
    }

}