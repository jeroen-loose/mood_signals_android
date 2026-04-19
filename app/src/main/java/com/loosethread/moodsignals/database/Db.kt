package com.loosethread.moodsignals.database

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import com.loosethread.moodsignals.datatypes.Day
import com.loosethread.moodsignals.datatypes.DaySignalValue
import com.loosethread.moodsignals.datatypes.LogCategory
import com.loosethread.moodsignals.datatypes.LogDay
import com.loosethread.moodsignals.datatypes.NotificationTime
import com.loosethread.moodsignals.datatypes.Signal
import com.loosethread.moodsignals.datatypes.SignalCategory
import com.loosethread.moodsignals.datatypes.SignalScore

object Db {
    private lateinit var appContext: Context
    private lateinit var helper: DbHelper

    fun init(context: Context) {
        appContext = context.applicationContext
        helper = DbHelper(appContext)
    }

    fun close() {
        helper.close()
    }

    fun getSignal(id: Int) : Signal {
        val db = helper.readableDatabase
        val query = "SELECT ${DbC.Signal.TBL}.${DbC.Signal.COL_DESCRIPTION} as signal_description, " +
                "${DbC.Signal.TBL}.${DbC.Signal.COL_ACTIVE_CHOICE} as active_choice, " +
                "${DbC.Signal.TBL}.${DbC.Signal.COL_CATEGORY_ID} as category_id, " +
                "${DbC.Signal.TBL}.${DbC.Signal.COL_NOTIFICATION_TIME_ID} as notification_time_id, " +
                "${DbC.SignalValue.TBL}.${DbC.SignalValue.COL_DESCRIPTION} as signal_value_description, " +
                "${DbC.SignalValue.TBL}.${DbC.SignalValue.COL_SCORE} as score FROM" +
                " ${DbC.Signal.TBL} INNER JOIN ${DbC.SignalValue.TBL} " +
                "ON ${DbC.Signal.TBL}.${BaseColumns._ID} = ${DbC.SignalValue.TBL}.${DbC.SignalValue.COL_SIGNAL_ID} " +
                "WHERE ${DbC.Signal.TBL}.${BaseColumns._ID} = ?" +
                "ORDER BY ${DbC.SignalValue.COL_SCORE} ASC"

        val params = arrayOf(id.toString())

        val cursor = db.rawQuery(query, params)

        var signal = Signal(id, null, mutableListOf<SignalScore>(), null, null, null)

        with(cursor) {
            while (moveToNext()) {
                signal.description = getString(getColumnIndexOrThrow("signal_description"))
                signal.activeChoice = getInt(getColumnIndexOrThrow("active_choice")) == 1
                signal.categoryId = getInt(getColumnIndexOrThrow("category_id"))
                signal.notificationTimeId = getInt(getColumnIndexOrThrow("notification_time_id"))

                val signalScore = SignalScore(
                    getInt(getColumnIndexOrThrow("score")),
                    getString(getColumnIndexOrThrow("signal_value_description"))
                )
                signal.scores.add(signalScore)
            }

            close()
        }

        return signal
    }
    fun getSignalsByCategory(categoryId: Int?): MutableList<Signal> {
        val db = helper.readableDatabase
        var query = "SELECT ${DbC.Signal.TBL}.${BaseColumns._ID} AS id, " +
                "${DbC.Signal.TBL}.${DbC.Signal.COL_DESCRIPTION} AS signal_description, " +
                "${DbC.Signal.TBL}.${DbC.Signal.COL_ACTIVE_CHOICE} AS active_choice, " +
                "${DbC.Signal.TBL}.${DbC.Signal.COL_NOTIFICATION_TIME_ID} AS notification_time_id, " +
                "${DbC.SignalValue.TBL}.${DbC.SignalValue.COL_DESCRIPTION} AS signal_value_description, " +
                "${DbC.SignalValue.COL_SCORE} AS score FROM " +
                " ${DbC.Signal.TBL} INNER JOIN ${DbC.SignalValue.TBL} " +
                "ON ${DbC.Signal.TBL}.${BaseColumns._ID} = ${DbC.SignalValue.TBL}.${DbC.SignalValue.COL_SIGNAL_ID} " +
                "WHERE ${DbC.Signal.TBL}.${DbC.Signal.COL_CATEGORY_ID} = ? " +
                "ORDER BY ${DbC.Signal.TBL}.${BaseColumns._ID} ASC, ${DbC.SignalValue.COL_SCORE} ASC"

        var params: Array<String>
        if (categoryId != null) {
            params = arrayOf(categoryId.toString())
        } else {
            params = arrayOf("NULL")
        }

        val cursor = db.rawQuery(query, params)
        var signals = mutableListOf<Signal>()

        with(cursor) {
            var signal: Signal
            var id: Int? = null
            var resultIndex: Int = signals.lastIndex

            while (moveToNext()) {
                val newId = getInt(getColumnIndexOrThrow("id"))
                if(id != newId) {
                    signal = Signal(
                        newId,
                        getString(getColumnIndexOrThrow("signal_description")),
                        mutableListOf<SignalScore>(),
                        getInt(getColumnIndexOrThrow("active_choice")) == 1,
                        categoryId,
                        getInt(getColumnIndexOrThrow("notification_time_id"))
                    )
                    signals.add(signal)
                    resultIndex = signals.lastIndex
                    id = newId
                }

                val signalScore = SignalScore(
                    getInt(getColumnIndexOrThrow("score")),
                    getString(getColumnIndexOrThrow("signal_value_description"))
                )

                signals[resultIndex].scores.add(signalScore)
            }

            close()
        }

        return signals
    }

    fun getSignalCount() : Int {
        val db = helper.readableDatabase

        // TODO: handle archived signals
        val query = "SELECT * FROM ${DbC.Signal.TBL}"
        val c = db.rawQuery(query, null)
        val result = c.count
        c.close()
        return result
    }

    fun signalHasEntries(id: Int) : Boolean {
        val db = helper.readableDatabase

        val query =
            "SELECT * FROM ${DbC.SignalValue.TBL} WHERE ${DbC.SignalValue.COL_SIGNAL_ID} = ? LIMIT 0,1"
        val params = arrayOf(id.toString())
        val c = db.rawQuery(query, params)
        val result = c.count > 0

        c.close()
        return result
    }

    fun deleteSignal(id: Int, archive: Boolean) {
        val db = helper.writableDatabase
        var query: String
        if (archive) {
            query = "UPDATE ${DbC.Signal.TBL} " +
                    "SET ${DbC.Signal.COL_ARCHIVED} = 1 " +
                    "WHERE ${BaseColumns._ID} = ?"
        } else {
            query = "DELETE FROM ${DbC.Signal.TBL} WHERE ${BaseColumns._ID} = ?"
        }
        val params = arrayOf(id.toString())
        val c = db.rawQuery(query, params)
        c.moveToFirst()
        c.close()
    }


    fun getCategories(idToExclude: Int? = null): MutableList<SignalCategory> {
        val db = helper.readableDatabase
        val result = mutableListOf<SignalCategory>()
        var query = "SELECT * FROM ${DbC.SignalCategory.TBL}"
        if (idToExclude != null) {
            query = query.plus(" WHERE ${BaseColumns._ID} != $idToExclude")
        }
        val c = db.rawQuery(query, null)
        with (c) {
            while (moveToNext()) {
                result.add(
                    SignalCategory(
                        getInt(getColumnIndexOrThrow(BaseColumns._ID)),
                        getString(getColumnIndexOrThrow(DbC.SignalCategory.COL_DESCRIPTION))
                    )
                )
            }
            c.close()
        }
        return result
    }

    fun getCategory(id: Int) : SignalCategory {
        val db = helper.readableDatabase
        val query = "SELECT * FROM ${DbC.SignalCategory.TBL} WHERE ${BaseColumns._ID} = ?"

        val params = arrayOf(id.toString())

        val cursor = db.rawQuery(query, params)

        var category = SignalCategory(id, null)
        with(cursor) {
            while (moveToNext()) {
                category = SignalCategory(
                    getInt(getColumnIndexOrThrow(BaseColumns._ID)),
                    getString(getColumnIndexOrThrow(DbC.SignalCategory.COL_DESCRIPTION))
                )
            }

            close()
        }

        return category
    }

    fun categoryHasSignals(id: Int) : Boolean {
        val db = helper.readableDatabase

        val query = "SELECT * FROM ${DbC.Signal.TBL} WHERE ${DbC.Signal.COL_CATEGORY_ID} = ?"
        val params = arrayOf(id.toString())
        val c = db.rawQuery(query, params)
        val result = c.count > 0

        c.close()
        return result
    }

    fun addCategory(description: String) : Int {
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put(DbC.SignalCategory.COL_DESCRIPTION, description)
        }

        val newCategoryId = db.insert(DbC.SignalCategory.TBL, null, values)
        return newCategoryId.toInt()
    }

    fun updateCategory(id: Int, description: String) {
        val db = helper.writableDatabase
        val query = "UPDATE ${DbC.SignalCategory.TBL} " +
                "SET ${DbC.SignalCategory.COL_DESCRIPTION} = ? " +
                "WHERE ${BaseColumns._ID} = ?"
        val params = arrayOf(description, id.toString())
        val c = db.rawQuery(query, params)
        c.moveToFirst()
        c.close()
    }

    fun deleteCategory(id: Int, replacement: Int) {
        val db = helper.writableDatabase
        if (replacement > 0) {
            val query = "UPDATE ${DbC.Signal.TBL} " +
                    "SET category_id = ? " +
                    "WHERE category_id = ?"
            val params = arrayOf(replacement.toString(), id.toString())
            val c = db.rawQuery(query, params)
            c.moveToFirst()
            c.close()
        }
        val query = "DELETE FROM ${DbC.SignalCategory.TBL} WHERE ${BaseColumns._ID} = ?"
        val params = arrayOf(id.toString())
        val c = db.rawQuery(query, params)
        c.moveToFirst()
        c.close()
    }

    fun updateSignalCategoryIds(from: Int, to: Int) {
        val db = helper.writableDatabase
        val query = "UPDATE ${DbC.Signal.TBL} " +
                "SET ${DbC.Signal.COL_CATEGORY_ID} = ? " +
                "WHERE ${DbC.Signal.COL_CATEGORY_ID} = ?"
        val params = arrayOf(to.toString(), from.toString())
        val c = db.rawQuery(query, params)
        c.moveToFirst()
        c.close()
    }

    fun getDayCategories(dayId: Int): MutableList<LogCategory> {
        val db = helper.readableDatabase

        val query = "SELECT ${DbC.DaySignalValue.TBL}.${DbC.DaySignalValue.COL_DAY_ID} AS day_id, " +
                "${DbC.DaySignalValue.TBL}.${DbC.DaySignalValue.COL_SIGNAL_SCORE} AS signal_score, " +
                "COUNT(${DbC.DaySignalValue.TBL}.${DbC.DaySignalValue.COL_SIGNAL_SCORE}) AS score_count, " +
                "${DbC.SignalCategory.TBL}.${BaseColumns._ID} AS category_id, " +
                "${DbC.SignalCategory.TBL}.${DbC.SignalCategory.COL_DESCRIPTION} AS category_description " +
                "FROM ${DbC.DaySignalValue.TBL} " +
                "LEFT JOIN ${DbC.Signal.TBL} " +
                "ON ${DbC.DaySignalValue.TBL}.${DbC.DaySignalValue.COL_SIGNAL_ID} = ${DbC.Signal.TBL}.${BaseColumns._ID} " +
                "LEFT JOIN ${DbC.SignalCategory.TBL} " +
                "ON ${DbC.Signal.TBL}.${DbC.Signal.COL_CATEGORY_ID} = ${DbC.SignalCategory.TBL}.${BaseColumns._ID} " +
                "WHERE ${DbC.DaySignalValue.TBL}.${DbC.DaySignalValue.COL_DAY_ID} = ? " +
                "GROUP BY " +
                "${DbC.SignalCategory.TBL}.${BaseColumns._ID}, " +
                "${DbC.DaySignalValue.TBL}.${DbC.DaySignalValue.COL_SIGNAL_SCORE} " +
                "ORDER BY category_id ASC, signal_score ASC"
        val params = arrayOf(dayId.toString())
        val c = db.rawQuery(query, params)
        var logCategory = LogCategory(-1, -1, "")
        var result:  MutableList<LogCategory> = mutableListOf()
        var resultIndex = -1

        with (c) {
            while (moveToNext()) {
                var categoryId = getInt(getColumnIndexOrThrow("category_id"))

                if(logCategory.categoryId != categoryId) {
                    var description = getString(getColumnIndexOrThrow("category_description"))
                    if(description.isNullOrEmpty()) { description = "Uncategorized" }

                    logCategory = LogCategory(
                        getInt(getColumnIndexOrThrow("day_id")),
                        getInt(getColumnIndexOrThrow("category_id")),
                        description,
                        mapOf()
                    )
                    result.add(logCategory)
                    resultIndex = result.lastIndex
                }
                val scoreCount = mapOf(
                    getInt(getColumnIndexOrThrow("signal_score")) to getInt(getColumnIndexOrThrow("score_count"))
                )

                result[resultIndex].score_count = result[resultIndex].score_count.plus(scoreCount)

            }

            close()
        }
        return result
    }

    fun getSignalsByNotificationTime(notificationTimeId: Int? = null): MutableList<Signal> {
        val db = helper.readableDatabase
        var query = "SELECT ${DbC.Signal.TBL}.${BaseColumns._ID} AS id, " +
                "${DbC.Signal.TBL}.${DbC.Signal.COL_DESCRIPTION} AS signal_description, " +
                "${DbC.Signal.TBL}.${DbC.Signal.COL_ACTIVE_CHOICE} AS active_choice, " +
                "${DbC.Signal.TBL}.${DbC.Signal.COL_CATEGORY_ID} AS category_id, " +
                "${DbC.Signal.TBL}.${DbC.Signal.COL_NOTIFICATION_TIME_ID} AS notification_time_id, " +
                "${DbC.SignalValue.TBL}.${DbC.SignalValue.COL_DESCRIPTION} AS signal_value_description, " +
                "${DbC.SignalValue.COL_SCORE} AS score FROM " +
                " ${DbC.Signal.TBL} INNER JOIN ${DbC.SignalValue.TBL} " +
                "ON ${DbC.Signal.TBL}.${BaseColumns._ID} = ${DbC.SignalValue.TBL}.${DbC.SignalValue.COL_SIGNAL_ID} " +
                "WHERE ${DbC.Signal.TBL}.${DbC.Signal.COL_ARCHIVED} = 0 "
        if (notificationTimeId != null) {
            query += "AND ${DbC.Signal.TBL}.${DbC.Signal.COL_NOTIFICATION_TIME_ID} = ? "
        }
        query += "ORDER BY category_id ASC, ${DbC.Signal.TBL}.${BaseColumns._ID} ASC, ${DbC.SignalValue.COL_SCORE} ASC"

        var params: Array<String>? = null
        if (notificationTimeId != null) {
            params = arrayOf(notificationTimeId.toString())
        }

        val cursor = db.rawQuery(query, params)
        var signals = mutableListOf<Signal>()

        with(cursor) {
            var signal: Signal
            var id: Int? = null
            var resultIndex: Int = signals.lastIndex

            while (moveToNext()) {
                val newId = getInt(getColumnIndexOrThrow("id"))
                if(id != newId) {
                    signal = Signal(
                        newId,
                        getString(getColumnIndexOrThrow("signal_description")),
                        mutableListOf<SignalScore>(),
                        getInt(getColumnIndexOrThrow("active_choice")) == 1,
                        getInt(getColumnIndexOrThrow("category_id")),
                        getInt(getColumnIndexOrThrow("notification_time_id"))
                    )
                    signals.add(signal)
                    resultIndex = signals.lastIndex
                    id = newId
                }

                val signalScore = SignalScore(
                    getInt(getColumnIndexOrThrow("score")),
                    getString(getColumnIndexOrThrow("signal_value_description"))
                )

                signals[resultIndex].scores.add(signalScore)
            }

            close()
        }

        return signals
    }

    fun addSignal(signal: Signal) {
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put(DbC.Signal.COL_DESCRIPTION, signal.description)
            put(DbC.Signal.COL_ACTIVE_CHOICE, signal.activeChoice)
            put(DbC.Signal.COL_CATEGORY_ID, signal.categoryId)
            put(DbC.Signal.COL_NOTIFICATION_TIME_ID, signal.notificationTimeId)
            put(DbC.Signal.COL_ARCHIVED, 0)
        }

        val newSignalId = db.insert(DbC.Signal.TBL, null, values)

        signal.scores.forEach { score ->
            val values = ContentValues().apply {
                put(DbC.SignalValue.COL_SIGNAL_ID, newSignalId)
                put(DbC.SignalValue.COL_SCORE, score.score)
                put(DbC.SignalValue.COL_DESCRIPTION, score.description)
            }

            db.insert(DbC.SignalValue.TBL, null, values)
        }
    }

    fun updateSignal(signal: Signal) {
        val db = helper.writableDatabase

        val query = "UPDATE ${DbC.Signal.TBL} " +
                "SET ${DbC.Signal.COL_DESCRIPTION} = ?, " +
                "${DbC.Signal.COL_ACTIVE_CHOICE} = ?, " +
                "${DbC.Signal.COL_CATEGORY_ID} = ?, " +
                "${DbC.Signal.COL_NOTIFICATION_TIME_ID} = ? " +
                "WHERE ${BaseColumns._ID} = ?"
        val params = arrayOf(
            signal.description.toString(),
            if (signal.activeChoice == true) "1" else "0",
            signal.categoryId.toString(),
            signal.notificationTimeId.toString(),
            signal.id.toString()
        )
        var c = db.rawQuery(query, params)
        c.moveToFirst()
        c.close()

        signal.scores.forEach { signalScore ->
            val query = "UPDATE ${DbC.SignalValue.TBL} " +
                    "SET ${DbC.SignalValue.COL_DESCRIPTION} = ? " +
                    "WHERE ${DbC.SignalValue.COL_SIGNAL_ID} = ? " +
                    "AND ${DbC.SignalValue.COL_SCORE} = ?"
            val params = arrayOf(signalScore.description.toString(), signal.id.toString(), signalScore.score.toString())
            c = db.rawQuery(query, params)
            c.moveToFirst()
        }

        c.close()

    }

    fun getNotificationTime(id: Int) : NotificationTime {
        val db = helper.readableDatabase
        val query = "SELECT " +
                "${DbC.NotificationTime.COL_TITLE} as title, " +
                "${DbC.NotificationTime.COL_QUESTION} as question, " +
                "${DbC.NotificationTime.COL_TIME} as time " +
                "FROM " +
                "${DbC.NotificationTime.TBL} " +
                "WHERE ${BaseColumns._ID} = ?"

        val params = arrayOf(id.toString())

        val cursor = db.rawQuery(query, params)

        with(cursor) {
            moveToNext()

            var time = getString(getColumnIndexOrThrow("time"))
            if (time!!.substring(1, 2) == ":") {
                time = "0" + time
            }

            val notificationTime = NotificationTime(
                id,
                getString(getColumnIndexOrThrow("title")),
                getString(getColumnIndexOrThrow("question")),
                time
            )

            close()

            return notificationTime
        }
    }

    fun getNotificationTimes(idToExclude: Int? = null): MutableList<NotificationTime> {
        val db = helper.readableDatabase
        var query = "SELECT * FROM ${DbC.NotificationTime.TBL}"
        if (idToExclude != null) {
            query = query.plus(" WHERE ${BaseColumns._ID} != $idToExclude")
        }

        val cursor = db.rawQuery(query, null)
        val result = mutableListOf<NotificationTime>()

        with(cursor) {
            while(moveToNext()) {
                val notificationTime = NotificationTime(
                    getInt(getColumnIndexOrThrow(BaseColumns._ID)),
                    getString(getColumnIndexOrThrow(DbC.NotificationTime.COL_TITLE)),
                    getString(getColumnIndexOrThrow(DbC.NotificationTime.COL_QUESTION)),
                    getString(getColumnIndexOrThrow(DbC.NotificationTime.COL_TIME))
                )

                result.add(notificationTime)
            }

            close()

            return result
        }
    }

    fun addNotificationTime(notificationTime: NotificationTime) : Int {
        val db = helper.writableDatabase
        val query = "INSERT INTO ${DbC.NotificationTime.TBL}" +
                "(" +
                "${DbC.NotificationTime.COL_TITLE}," +
                "${DbC.NotificationTime.COL_QUESTION}," +
                "${DbC.NotificationTime.COL_TIME}" +
                ") VALUES (" +
                "'${notificationTime.title}'," +
                "'${notificationTime.question}'," +
                "'${notificationTime.time}'" +
                ")"
        val c = db.rawQuery(query, null)
        c.moveToFirst()
        c.close()

        return getInsertId()
    }

    fun updateNotificationTime(notificationTime: NotificationTime) {
        val db = helper.writableDatabase
        val query = "UPDATE ${DbC.NotificationTime.TBL} " +
                "SET " +
                "${DbC.NotificationTime.COL_TITLE} = '${notificationTime.title}', " +
                "${DbC.NotificationTime.COL_QUESTION} = '${notificationTime.question}', " +
                "${DbC.NotificationTime.COL_TIME} = '${notificationTime.time}' " +
                "WHERE ${BaseColumns._ID} = ?"
        val params = arrayOf(notificationTime.id.toString())
        val c = db.rawQuery(query, params)
        c.moveToFirst()
        c.close()
    }

    fun notificationTimeHasSignals(id: Int) : Boolean {
        val db = helper.readableDatabase

        val query = "SELECT * FROM ${DbC.Signal.TBL} WHERE ${DbC.Signal.COL_NOTIFICATION_TIME_ID} = ?"
        val params = arrayOf(id.toString())
        val c = db.rawQuery(query, params)
        val result = c.count > 0

        c.close()
        return result
    }

    fun deleteNotificationTime(id: Int, replacement: Int? = null) {
        val db = helper.writableDatabase
        replacement?.let {
            if (it > 0) {
                val query = "UPDATE ${DbC.Signal.TBL} " +
                        "SET notification_time_id = ? " +
                        "WHERE notification_time_id = ?"
                val params = arrayOf(replacement.toString(), id.toString())
                val c = db.rawQuery(query, params)
                c.moveToFirst()
                c.close()
            }
        }
        val query = "DELETE FROM ${DbC.NotificationTime.TBL} WHERE ${BaseColumns._ID} = ?"
        val params = arrayOf(id.toString())
        val c = db.rawQuery(query, params)
        c.moveToFirst()
        c.close()
    }

    fun insertDaySignalValue(dayId: Int, signalId: Int, scoreId: Int) {
        val db = helper.writableDatabase
        val query = "INSERT OR REPLACE INTO ${DbC.DaySignalValue.TBL} " +
                "(" +
                "${DbC.DaySignalValue.COL_DAY_ID}, " +
                "${DbC.DaySignalValue.COL_SIGNAL_ID}, " +
                "${DbC.DaySignalValue.COL_SIGNAL_SCORE} " +
                ") VALUES (" +
                "$dayId, " +
                "$signalId, " +
                "$scoreId" +
                ")"
        val c = db.rawQuery(query, null)
        c.moveToFirst()
        c.close()
    }

    fun removeDaySignalValue(dayId: Int, signalId: Int) {
        val db = helper.writableDatabase
        val query = "DELETE FROM ${DbC.DaySignalValue.TBL} " +
                "WHERE ${DbC.DaySignalValue.COL_DAY_ID} = ? " +
                "AND ${DbC.DaySignalValue.COL_SIGNAL_ID} = ?"
        val params = arrayOf(dayId.toString(), signalId.toString())
        val c = db.rawQuery(query, params)
        c.moveToFirst()
        c.close()
    }

    fun getDaySignalValues(dayId: Int) : MutableList<DaySignalValue> {
        val db = helper.readableDatabase
        val query = "SELECT ${DbC.DaySignalValue.TBL}.${DbC.DaySignalValue.COL_SIGNAL_ID} as signal_id, " +
                "${DbC.DaySignalValue.TBL}.${DbC.DaySignalValue.COL_SIGNAL_SCORE} as score, " +
                "${DbC.Signal.TBL}.${DbC.Signal.COL_DESCRIPTION} as signal_description, " +
                "${DbC.SignalValue.TBL}.${DbC.SignalValue.COL_DESCRIPTION} as score_description " +
                "FROM ${DbC.DaySignalValue.TBL} " +
                "JOIN ${DbC.Signal.TBL} " +
                "ON ${DbC.DaySignalValue.TBL}.${DbC.DaySignalValue.COL_SIGNAL_ID} = ${DbC.Signal.TBL}.${BaseColumns._ID} " +
                "JOIN ${DbC.SignalValue.TBL} " +
                "ON ${DbC.DaySignalValue.TBL}.${DbC.DaySignalValue.COL_SIGNAL_ID} = ${DbC.SignalValue.TBL}.${DbC.SignalValue.COL_SIGNAL_ID} " +
                "AND ${DbC.DaySignalValue.TBL}.${DbC.DaySignalValue.COL_SIGNAL_SCORE} = ${DbC.SignalValue.TBL}.${DbC.SignalValue.COL_SCORE} " +
                "WHERE ${DbC.DaySignalValue.COL_DAY_ID} = ? " +
                "ORDER BY score DESC, signal_id ASC"
        val params = arrayOf(dayId.toString())
        val c = db.rawQuery(query, params)
        var result = mutableListOf<DaySignalValue>()
        with(c) {
            while(moveToNext()) {
               result.add(
                   DaySignalValue(
                       //getInt(getColumnIndexOrThrow(DbContract.DaySignalValue.COLUMN_NAME_DAY_ID)),
                       getInt(getColumnIndexOrThrow(DbC.DaySignalValue.COL_SIGNAL_ID)),
                       getInt(getColumnIndexOrThrow(DbC.DaySignalValue.COL_SIGNAL_SCORE)),
                       getString(getColumnIndexOrThrow("signal_description")),
                       getString(getColumnIndexOrThrow("score_description"))
                   )
               )
            }
        }

        return result
    }

    fun getDaySignalValuesByCategory(dayId: Int, categoryId: Int?) : MutableList<DaySignalValue> {
        val db = helper.readableDatabase
        var query = "SELECT ${DbC.DaySignalValue.TBL}.${DbC.DaySignalValue.COL_SIGNAL_ID} as signal_id, " +
                "${DbC.DaySignalValue.TBL}.${DbC.DaySignalValue.COL_SIGNAL_SCORE} as score, " +
                "${DbC.Signal.TBL}.${DbC.Signal.COL_DESCRIPTION} as signal_description, " +
                "${DbC.SignalValue.TBL}.${DbC.SignalValue.COL_DESCRIPTION} as score_description " +
                "FROM ${DbC.DaySignalValue.TBL} " +
                "JOIN ${DbC.Signal.TBL} " +
                "ON ${DbC.DaySignalValue.TBL}.${DbC.DaySignalValue.COL_SIGNAL_ID} = ${DbC.Signal.TBL}.${BaseColumns._ID} " +
                "JOIN ${DbC.SignalValue.TBL} " +
                "ON ${DbC.DaySignalValue.TBL}.${DbC.DaySignalValue.COL_SIGNAL_ID} = ${DbC.SignalValue.TBL}.${DbC.SignalValue.COL_SIGNAL_ID} " +
                "AND ${DbC.DaySignalValue.TBL}.${DbC.DaySignalValue.COL_SIGNAL_SCORE} = ${DbC.SignalValue.TBL}.${DbC.SignalValue.COL_SCORE} " +
                "WHERE ${DbC.DaySignalValue.COL_DAY_ID} = ? " +
                "AND ${DbC.Signal.TBL}.${DbC.Signal.COL_CATEGORY_ID} "

                var params: Array<String>
                if (categoryId == 0) {
                    query += "IS NULL"
                    params = arrayOf(dayId.toString())
                } else {
                    query += "= ?"
                    params = arrayOf(dayId.toString(), categoryId.toString())
                }
                query += " ORDER BY score DESC, signal_id ASC"

        val c = db.rawQuery(query, params)
        var result = mutableListOf<DaySignalValue>()
        with(c) {
            while(moveToNext()) {
                result.add(
                    DaySignalValue(
                        //getInt(getColumnIndexOrThrow(DbContract.DaySignalValue.COLUMN_NAME_DAY_ID)),
                        getInt(getColumnIndexOrThrow(DbC.DaySignalValue.COL_SIGNAL_ID)),
                        getInt(getColumnIndexOrThrow(DbC.DaySignalValue.COL_SIGNAL_SCORE)),
                        getString(getColumnIndexOrThrow("signal_description")),
                        getString(getColumnIndexOrThrow("score_description"))
                    )
                )
            }
        }

        return result
    }

    fun getDaySignalValue(dayId: Int, signalId: Int) {
        val db = helper.readableDatabase
        val query = "SELECT * FROM ${DbC.DaySignalValue.TBL} " +
                "WHERE ${DbC.DaySignalValue.COL_DAY_ID} = ? " +
                "AND ${DbC.DaySignalValue.COL_SIGNAL_ID} = ?"
        val params = arrayOf(dayId.toString(), signalId.toString())
        val c = db.rawQuery(query, params)
        c.moveToFirst()
        c.close()
    }

    fun getDayId(date: String) : Int {
        val db = helper.readableDatabase
        val query = "SELECT * FROM ${DbC.Day.TBL} " +
                "WHERE ${DbC.Day.COL_DATE} = ? " +
                "ORDER BY ${BaseColumns._ID} DESC " +
                "LIMIT 1"
        val params = arrayOf(date)
        val c = db.rawQuery(query, params)
        var id : Int

        if(c.moveToFirst()){
            id = c.getInt(c.getColumnIndexOrThrow(BaseColumns._ID))
        } else {
            id = createDayId(date)
        }

        c.close()
        return id
    }

    fun getDayScores() : MutableList<LogDay> {
       val db = helper.readableDatabase
       val query = "SELECT ${DbC.Day.TBL}.${BaseColumns._ID} as day_id, " +
               "${DbC.Day.TBL}.${DbC.Day.COL_DATE} as date, " +
               "${DbC.DayComment.TBL}.${DbC.DayComment.COL_COMMENT} as comment, " +
               "${DbC.DaySignalValue.TBL}.${DbC.DaySignalValue.COL_SIGNAL_SCORE} as signal_score, " +
               "COUNT(*) as count " +
               "FROM ${DbC.Day.TBL} " +
               "LEFT JOIN ${DbC.DayComment.TBL} " +
               "ON ${DbC.Day.TBL}.${BaseColumns._ID} = ${DbC.DayComment.TBL}.${DbC.DayComment.COL_DAY_ID} " +
               "LEFT JOIN ${DbC.DaySignalValue.TBL} " +
               "ON ${DbC.Day.TBL}.${BaseColumns._ID} = ${DbC.DaySignalValue.TBL}.${DbC.DaySignalValue.COL_DAY_ID} " +
               "GROUP BY ${DbC.Day.TBL}.${BaseColumns._ID}, ${DbC.DaySignalValue.COL_SIGNAL_SCORE} " +
               "ORDER BY " +
               "${DbC.DaySignalValue.COL_DAY_ID} DESC, " +
               "${DbC.DaySignalValue.COL_SIGNAL_SCORE} ASC"
       val c = db.rawQuery(query, null)
       var result = mutableListOf<LogDay>()
        var day = LogDay(-1, "", mutableMapOf())
        var lastIndex : Int? = null

        with(c) {
            while(moveToNext()) {

                if (day.dayId != getInt(getColumnIndexOrThrow("day_id"))) {
                    day = LogDay(
                        getInt(getColumnIndexOrThrow("day_id")),
                        getString(getColumnIndexOrThrow("date")) + " - " + getString(getColumnIndexOrThrow("comment")),
                        mutableMapOf()
                    )
                    result.add(day)
                    lastIndex = result.lastIndex
                }

                val score_count = mapOf(
                    getInt(getColumnIndexOrThrow("signal_score")) to getInt(getColumnIndexOrThrow("count"))
                )

                result[lastIndex!!].score_count = result[lastIndex!!].score_count.plus(score_count)
            }
        }
       c.close()
       return result
    }

    fun createDayId(date: String) : Int {
        val db = helper.writableDatabase
        val query = "INSERT INTO ${DbC.Day.TBL} " +
                "(" +
                "${DbC.Day.COL_DATE}" +
                ") VALUES (" +
                "?" +
                ")"
        val params = arrayOf(date)
        val c = db.rawQuery(query, params)
        c.moveToFirst()
        c.close()
        return getInsertId()
    }

    fun dayIsEmpty(dayId: Int) : Boolean {
        val db = helper.readableDatabase
        val query = "SELECT * FROM ${DbC.DaySignalValue.TBL} " +
                "WHERE ${DbC.DaySignalValue.COL_DAY_ID} = ?"
        val params = arrayOf(dayId.toString())
        val c = db.rawQuery(query, params)
        val result = c.count == 0
        c.close()
        return result
    }

    fun removeDay(dayId: Int) {
        val db = helper.writableDatabase
        val query = "DELETE FROM ${DbC.Day.TBL} WHERE ${BaseColumns._ID} = ?"
        val params = arrayOf(dayId.toString())
        val c = db.rawQuery(query, params)
        c.moveToFirst()
        c.close()
    }

    fun updateComment(dayId: Int, comment: String?) {
        val db = helper.writableDatabase
        var query: String
        var params: Array<String>

        if (comment.isNullOrEmpty()) {
            query = "DELETE FROM ${DbC.DayComment.TBL} " +
                    "WHERE ${DbC.DayComment.COL_DAY_ID} = ?"
            params = arrayOf(dayId.toString())
        } else {
            val existingComment = getComment(dayId)
            if (existingComment.isNullOrEmpty()) {
                query = "INSERT INTO ${DbC.DayComment.TBL} " +
                        "(" +
                        "${DbC.DayComment.COL_DAY_ID}, " +
                        "${DbC.DayComment.COL_COMMENT}" +
                        ") VALUES (?, ?)"
                params = arrayOf(dayId.toString(), comment)
            } else {
                query = "UPDATE ${DbC.DayComment.TBL} " +
                        "SET ${DbC.DayComment.COL_COMMENT} = ? " +
                        "WHERE ${DbC.DayComment.COL_DAY_ID} = ?"
                params = arrayOf(comment, dayId.toString())
            }
        }
        val c = db.rawQuery(query, params)
        c.moveToFirst()
        c.close()
    }

    fun getComment(dayId: Int) : String {
        val db = helper.readableDatabase
        val query = "SELECT ${DbC.DayComment.COL_COMMENT} FROM ${DbC.DayComment.TBL} " +
                "WHERE ${DbC.DayComment.COL_DAY_ID} = ?"
        val params = arrayOf(dayId.toString())
        val c = db.rawQuery(query, params)
        var result = ""
        with(c) {
            if(moveToFirst()) {
                result = getString(c.getColumnIndexOrThrow(DbC.DayComment.COL_COMMENT))
            }
            close()
        }

        return result
    }

    fun getDays() : MutableList<Day> {
        val db = helper.readableDatabase
        val query = "SELECT ${DbC.Day.TBL}.${BaseColumns._ID} as day_id, " +
                "${DbC.Day.TBL}.${DbC.Day.COL_DATE} as date, " +
                "${DbC.DayComment.TBL}.${DbC.DayComment.COL_COMMENT} as comment " +
                "FROM ${DbC.Day.TBL} " +
                "LEFT JOIN ${DbC.DayComment.TBL} " +
                "ON ${DbC.Day.TBL}.${BaseColumns._ID} = ${DbC.DayComment.TBL}.${DbC.DayComment.COL_DAY_ID} " +
                "ORDER BY ${DbC.Day.COL_DATE} DESC"
        val c = db.rawQuery(query, null)
        var result = mutableListOf<Day>()

        with(c) {
            while(moveToNext()) {
                val newId = getInt(c.getColumnIndexOrThrow("day_id"))
                result.add(
                    Day(
                        newId,
                        getString(c.getColumnIndexOrThrow("date")),
                        getString(c.getColumnIndexOrThrow("comment"))
                    )
                )
            }
            close()
        }

        return result
    }

    fun getDay(id: Int) : Day {
        val db = helper.readableDatabase
        val query =
            "SELECT ${DbC.Day.TBL}.${BaseColumns._ID} as day_id, " +
                    "${DbC.Day.TBL}.${DbC.Day.COL_DATE} as date, " +
                    "${DbC.DayComment.TBL}.${DbC.DayComment.COL_COMMENT} as comment " +
                    "FROM ${DbC.Day.TBL} " +
                    "LEFT JOIN ${DbC.DayComment.TBL} " +
                    "ON ${DbC.Day.TBL}.${BaseColumns._ID} = ${DbC.DayComment.TBL}.${DbC.DayComment.COL_DAY_ID} " +
                    "WHERE ${DbC.Day.TBL}.${BaseColumns._ID} = ?"
        "ORDER BY ${DbC.Day.COL_DATE} DESC"
        val params = arrayOf(id.toString())
        val c = db.rawQuery(query, params)
        var result = Day(-1, "", "")

        with(c) {
            while(moveToNext()) {
                result = Day(
                    id,
                    getString(c.getColumnIndexOrThrow("date")),
                    getString(c.getColumnIndexOrThrow("comment"))
                )
            }
            close()
        }

        return result
    }

    fun getInsertId(): Int {
        val db = helper.readableDatabase
        val query = "SELECT last_insert_rowid() AS id"
        val c = db.rawQuery(query, null)
        var id = -1
        if(c.moveToFirst()){
            id = c.getInt(c.getColumnIndexOrThrow("id"))
        }
        c.close()
        return id
    }

    fun generateMissingDays() {
        val db = helper.readableDatabase

        val query = """
        WITH RECURSIVE dates(date) AS (
          SELECT MIN(${DbC.Day.COL_DATE}) FROM ${DbC.Day.TBL}
          UNION ALL
          SELECT date(date, '+1 day')
          FROM dates
          WHERE date < date('now')
        )
        SELECT dates.date as missingDate
        FROM dates
        LEFT JOIN ${DbC.Day.TBL} ON dates.date = ${DbC.Day.TBL}.${DbC.Day.COL_DATE}
        WHERE ${DbC.Day.TBL}.${DbC.Day.COL_DATE} IS NULL
    """.trimIndent()

        val c = db.rawQuery(query, null)
        with (c) {
            while (moveToNext()) {
                val missingDate = getString(getColumnIndexOrThrow("missingDate"))
                createDayId(missingDate)
            }
        }
    }

    fun reset() {
        helper.reset()
    }

    fun addCategories() {
        helper.addCategories()
    }
}