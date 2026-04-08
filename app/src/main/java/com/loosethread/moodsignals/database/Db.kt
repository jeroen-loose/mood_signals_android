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
        val query = "SELECT ${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_DESCRIPTION} as signal_description, " +
                "${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_ACTIVE_CHOICE} as active_choice, " +
                "${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_CATEGORY_ID} as category_id, " +
                "${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_NOTIFICATION_TIME_ID} as notification_time_id, " +
                "${DbContract.SignalValue.TABLE_NAME}.${DbContract.SignalValue.COLUMN_NAME_DESCRIPTION} as signal_value_description, " +
                "${DbContract.SignalValue.TABLE_NAME}.${DbContract.SignalValue.COLUMN_NAME_SCORE} as score FROM" +
                " ${DbContract.Signal.TABLE_NAME} INNER JOIN ${DbContract.SignalValue.TABLE_NAME} " +
                "ON ${DbContract.Signal.TABLE_NAME}.${BaseColumns._ID} = ${DbContract.SignalValue.TABLE_NAME}.${DbContract.SignalValue.COLUMN_NAME_SIGNAL_ID} " +
                "WHERE ${DbContract.Signal.TABLE_NAME}.${BaseColumns._ID} = ?" +
                "ORDER BY ${DbContract.SignalValue.COLUMN_NAME_SCORE} ASC"

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
        var query = "SELECT ${DbContract.Signal.TABLE_NAME}.${BaseColumns._ID} AS id, " +
                "${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_DESCRIPTION} AS signal_description, " +
                "${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_ACTIVE_CHOICE} AS active_choice, " +
                "${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_NOTIFICATION_TIME_ID} AS notification_time_id, " +
                "${DbContract.SignalValue.TABLE_NAME}.${DbContract.SignalValue.COLUMN_NAME_DESCRIPTION} AS signal_value_description, " +
                "${DbContract.SignalValue.COLUMN_NAME_SCORE} AS score FROM " +
                " ${DbContract.Signal.TABLE_NAME} INNER JOIN ${DbContract.SignalValue.TABLE_NAME} " +
                "ON ${DbContract.Signal.TABLE_NAME}.${BaseColumns._ID} = ${DbContract.SignalValue.TABLE_NAME}.${DbContract.SignalValue.COLUMN_NAME_SIGNAL_ID} " +
                "WHERE ${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_CATEGORY_ID} = ? " +
                "ORDER BY ${DbContract.Signal.TABLE_NAME}.${BaseColumns._ID} ASC, ${DbContract.SignalValue.COLUMN_NAME_SCORE} ASC"

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

    fun signalHasEntries(id: Int) : Boolean {
        val db = helper.readableDatabase

        val query =
            "SELECT * FROM ${DbContract.SignalValue.TABLE_NAME} WHERE ${DbContract.SignalValue.COLUMN_NAME_SIGNAL_ID} = ? LIMIT 0,1"
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
            query = "UPDATE ${DbContract.Signal.TABLE_NAME} " +
                    "SET ${DbContract.Signal.COLUMN_NAME_ARCHIVED} = 1 " +
                    "WHERE ${BaseColumns._ID} = ?"
        } else {
            query = "DELETE FROM ${DbContract.Signal.TABLE_NAME} WHERE ${BaseColumns._ID} = ?"
        }
        val params = arrayOf(id.toString())
        val c = db.rawQuery(query, params)
        c.moveToFirst()
        c.close()
    }


    fun getCategories(idToExclude: Int? = null): MutableList<SignalCategory> {
        val db = helper.readableDatabase
        val result = mutableListOf<SignalCategory>()
        var query = "SELECT * FROM ${DbContract.SignalCategory.TABLE_NAME}"
        if (idToExclude != null) {
            query = query.plus(" WHERE ${BaseColumns._ID} != $idToExclude")
        }
        val c = db.rawQuery(query, null)
        with (c) {
            while (moveToNext()) {
                result.add(
                    SignalCategory(
                        getInt(getColumnIndexOrThrow(BaseColumns._ID)),
                        getString(getColumnIndexOrThrow(DbContract.SignalCategory.COLUMN_NAME_DESCRIPTION))
                    )
                )
            }
            c.close()
        }
        return result
    }

    fun getCategory(id: Int) : SignalCategory {
        val db = helper.readableDatabase
        val query = "SELECT * FROM ${DbContract.SignalCategory.TABLE_NAME} WHERE ${BaseColumns._ID} = ?"

        val params = arrayOf(id.toString())

        val cursor = db.rawQuery(query, params)

        var category = SignalCategory(id, null)
        with(cursor) {
            while (moveToNext()) {
                category = SignalCategory(
                    getInt(getColumnIndexOrThrow(BaseColumns._ID)),
                    getString(getColumnIndexOrThrow(DbContract.SignalCategory.COLUMN_NAME_DESCRIPTION))
                )
            }

            close()
        }

        return category
    }

    fun categoryHasSignals(id: Int) : Boolean {
        val db = helper.readableDatabase

        val query = "SELECT * FROM ${DbContract.Signal.TABLE_NAME} WHERE ${DbContract.Signal.COLUMN_NAME_CATEGORY_ID} = ?"
        val params = arrayOf(id.toString())
        val c = db.rawQuery(query, params)
        val result = c.count > 0

        c.close()
        return result
    }

    fun addCategory(description: String) : Int {
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put(DbContract.SignalCategory.COLUMN_NAME_DESCRIPTION, description)
        }

        val newCategoryId = db.insert(DbContract.SignalCategory.TABLE_NAME, null, values)
        return newCategoryId.toInt()
    }

    fun updateCategory(id: Int, description: String) {
        val db = helper.writableDatabase
        val query = "UPDATE ${DbContract.SignalCategory.TABLE_NAME} " +
                "SET ${DbContract.SignalCategory.COLUMN_NAME_DESCRIPTION} = ? " +
                "WHERE ${BaseColumns._ID} = ?"
        val params = arrayOf(description, id.toString())
        val c = db.rawQuery(query, params)
        c.moveToFirst()
        c.close()
    }

    fun deleteCategory(id: Int, replacement: Int) {
        val db = helper.writableDatabase
        if (replacement > 0) {
            val query = "UPDATE ${DbContract.Signal.TABLE_NAME} " +
                    "SET category_id = ? " +
                    "WHERE category_id = ?"
            val params = arrayOf(replacement.toString(), id.toString())
            val c = db.rawQuery(query, params)
            c.moveToFirst()
            c.close()
        }
        val query = "DELETE FROM ${DbContract.SignalCategory.TABLE_NAME} WHERE ${BaseColumns._ID} = ?"
        val params = arrayOf(id.toString())
        val c = db.rawQuery(query, params)
        c.moveToFirst()
        c.close()
    }

    fun updateSignalCategoryIds(from: Int, to: Int) {
        val db = helper.writableDatabase
        val query = "UPDATE ${DbContract.Signal.TABLE_NAME} " +
                "SET ${DbContract.Signal.COLUMN_NAME_CATEGORY_ID} = ? " +
                "WHERE ${DbContract.Signal.COLUMN_NAME_CATEGORY_ID} = ?"
        val params = arrayOf(to.toString(), from.toString())
        val c = db.rawQuery(query, params)
        c.moveToFirst()
        c.close()
    }

    fun getDayCategories(dayId: Int): MutableList<LogCategory> {
        val db = helper.readableDatabase

        val query = "SELECT ${DbContract.DaySignalValue.TABLE_NAME}.${DbContract.DaySignalValue.COLUMN_NAME_DAY_ID} AS day_id, " +
                "${DbContract.DaySignalValue.TABLE_NAME}.${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_SCORE} AS signal_score, " +
                "COUNT(${DbContract.DaySignalValue.TABLE_NAME}.${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_SCORE}) AS score_count, " +
                "${DbContract.SignalCategory.TABLE_NAME}.${BaseColumns._ID} AS category_id, " +
                "${DbContract.SignalCategory.TABLE_NAME}.${DbContract.SignalCategory.COLUMN_NAME_DESCRIPTION} AS category_description " +
                "FROM ${DbContract.DaySignalValue.TABLE_NAME} " +
                "LEFT JOIN ${DbContract.Signal.TABLE_NAME} " +
                "ON ${DbContract.DaySignalValue.TABLE_NAME}.${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_ID} = ${DbContract.Signal.TABLE_NAME}.${BaseColumns._ID} " +
                "LEFT JOIN ${DbContract.SignalCategory.TABLE_NAME} " +
                "ON ${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_CATEGORY_ID} = ${DbContract.SignalCategory.TABLE_NAME}.${BaseColumns._ID} " +
                "WHERE ${DbContract.DaySignalValue.TABLE_NAME}.${DbContract.DaySignalValue.COLUMN_NAME_DAY_ID} = ? " +
                "GROUP BY " +
                "${DbContract.SignalCategory.TABLE_NAME}.${BaseColumns._ID}, " +
                "${DbContract.DaySignalValue.TABLE_NAME}.${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_SCORE} " +
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
        var query = "SELECT ${DbContract.Signal.TABLE_NAME}.${BaseColumns._ID} AS id, " +
                "${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_DESCRIPTION} AS signal_description, " +
                "${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_ACTIVE_CHOICE} AS active_choice, " +
                "${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_CATEGORY_ID} AS category_id, " +
                "${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_NOTIFICATION_TIME_ID} AS notification_time_id, " +
                "${DbContract.SignalValue.TABLE_NAME}.${DbContract.SignalValue.COLUMN_NAME_DESCRIPTION} AS signal_value_description, " +
                "${DbContract.SignalValue.COLUMN_NAME_SCORE} AS score FROM " +
                " ${DbContract.Signal.TABLE_NAME} INNER JOIN ${DbContract.SignalValue.TABLE_NAME} " +
                "ON ${DbContract.Signal.TABLE_NAME}.${BaseColumns._ID} = ${DbContract.SignalValue.TABLE_NAME}.${DbContract.SignalValue.COLUMN_NAME_SIGNAL_ID} " +
                "WHERE ${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_ARCHIVED} = 0 "
        if (notificationTimeId != null) {
            query += "AND ${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_NOTIFICATION_TIME_ID} = ? "
        }
        query += "ORDER BY ${DbContract.Signal.TABLE_NAME}.${BaseColumns._ID} ASC, ${DbContract.SignalValue.COLUMN_NAME_SCORE} ASC"

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
            put(DbContract.Signal.COLUMN_NAME_DESCRIPTION, signal.description)
            put(DbContract.Signal.COLUMN_NAME_ACTIVE_CHOICE, signal.activeChoice)
            put(DbContract.Signal.COLUMN_NAME_CATEGORY_ID, signal.categoryId)
            put(DbContract.Signal.COLUMN_NAME_NOTIFICATION_TIME_ID, signal.notificationTimeId)
            put(DbContract.Signal.COLUMN_NAME_ARCHIVED, 0)
        }

        val newSignalId = db.insert(DbContract.Signal.TABLE_NAME, null, values)

        signal.scores.forEach { score ->
            val values = ContentValues().apply {
                put(DbContract.SignalValue.COLUMN_NAME_SIGNAL_ID, newSignalId)
                put(DbContract.SignalValue.COLUMN_NAME_SCORE, score.score)
                put(DbContract.SignalValue.COLUMN_NAME_DESCRIPTION, score.description)
            }

            db.insert(DbContract.SignalValue.TABLE_NAME, null, values)
        }
    }

    fun updateSignal(signal: Signal) {
        val db = helper.writableDatabase

        val query = "UPDATE ${DbContract.Signal.TABLE_NAME} " +
                "SET ${DbContract.Signal.COLUMN_NAME_DESCRIPTION} = ?, " +
                "${DbContract.Signal.COLUMN_NAME_ACTIVE_CHOICE} = ?, " +
                "${DbContract.Signal.COLUMN_NAME_CATEGORY_ID} = ?, " +
                "${DbContract.Signal.COLUMN_NAME_NOTIFICATION_TIME_ID} = ? " +
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
            val query = "UPDATE ${DbContract.SignalValue.TABLE_NAME} " +
                    "SET ${DbContract.SignalValue.COLUMN_NAME_DESCRIPTION} = ? " +
                    "WHERE ${DbContract.SignalValue.COLUMN_NAME_SIGNAL_ID} = ? " +
                    "AND ${DbContract.SignalValue.COLUMN_NAME_SCORE} = ?"
            val params = arrayOf(signalScore.description.toString(), signal.id.toString(), signalScore.score.toString())
            c = db.rawQuery(query, params)
            c.moveToFirst()
        }

        c.close()

    }

    fun getNotificationTime(id: Int) : NotificationTime {
        val db = helper.readableDatabase
        val query = "SELECT " +
                "${DbContract.NotificationTime.COLUMN_NAME_TITLE} as title, " +
                "${DbContract.NotificationTime.COLUMN_NAME_QUESTION} as question, " +
                "${DbContract.NotificationTime.COLUMN_NAME_TIME} as time " +
                "FROM " +
                "${DbContract.NotificationTime.TABLE_NAME} " +
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
        var query = "SELECT * FROM ${DbContract.NotificationTime.TABLE_NAME}"
        if (idToExclude != null) {
            query = query.plus(" WHERE ${BaseColumns._ID} != $idToExclude")
        }

        val cursor = db.rawQuery(query, null)
        val result = mutableListOf<NotificationTime>()

        with(cursor) {
            while(moveToNext()) {
                val notificationTime = NotificationTime(
                    getInt(getColumnIndexOrThrow(BaseColumns._ID)),
                    getString(getColumnIndexOrThrow(DbContract.NotificationTime.COLUMN_NAME_TITLE)),
                    getString(getColumnIndexOrThrow(DbContract.NotificationTime.COLUMN_NAME_QUESTION)),
                    getString(getColumnIndexOrThrow(DbContract.NotificationTime.COLUMN_NAME_TIME))
                )

                result.add(notificationTime)
            }

            close()

            return result
        }
    }

    fun addNotificationTime(notificationTime: NotificationTime) : Int {
        val db = helper.writableDatabase
        val query = "INSERT INTO ${DbContract.NotificationTime.TABLE_NAME}" +
                "(" +
                "${DbContract.NotificationTime.COLUMN_NAME_TITLE}," +
                "${DbContract.NotificationTime.COLUMN_NAME_QUESTION}," +
                "${DbContract.NotificationTime.COLUMN_NAME_TIME}" +
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
        val query = "UPDATE ${DbContract.NotificationTime.TABLE_NAME} " +
                "SET " +
                "${DbContract.NotificationTime.COLUMN_NAME_TITLE} = '${notificationTime.title}', " +
                "${DbContract.NotificationTime.COLUMN_NAME_QUESTION} = '${notificationTime.question}', " +
                "${DbContract.NotificationTime.COLUMN_NAME_TIME} = '${notificationTime.time}' " +
                "WHERE ${BaseColumns._ID} = ?"
        val params = arrayOf(notificationTime.id.toString())
        val c = db.rawQuery(query, params)
        c.moveToFirst()
        c.close()
    }

    fun notificationTimeHasSignals(id: Int) : Boolean {
        val db = helper.readableDatabase

        val query = "SELECT * FROM ${DbContract.Signal.TABLE_NAME} WHERE ${DbContract.Signal.COLUMN_NAME_NOTIFICATION_TIME_ID} = ?"
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
                val query = "UPDATE ${DbContract.Signal.TABLE_NAME} " +
                        "SET notification_time_id = ? " +
                        "WHERE notification_time_id = ?"
                val params = arrayOf(replacement.toString(), id.toString())
                val c = db.rawQuery(query, params)
                c.moveToFirst()
                c.close()
            }
        }
        val query = "DELETE FROM ${DbContract.NotificationTime.TABLE_NAME} WHERE ${BaseColumns._ID} = ?"
        val params = arrayOf(id.toString())
        val c = db.rawQuery(query, params)
        c.moveToFirst()
        c.close()
    }

    fun insertDaySignalValue(dayId: Int, signalId: Int, scoreId: Int) {
        val db = helper.writableDatabase
        val query = "INSERT OR REPLACE INTO ${DbContract.DaySignalValue.TABLE_NAME} " +
                "(" +
                "${DbContract.DaySignalValue.COLUMN_NAME_DAY_ID}, " +
                "${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_ID}, " +
                "${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_SCORE} " +
                ") VALUES (" +
                "$dayId, " +
                "$signalId, " +
                "$scoreId" +
                ")"
        val c = db.rawQuery(query, null)
        c.moveToFirst()
        c.close()
    }

    fun getDaySignalValues(dayId: Int) : MutableList<DaySignalValue> {
        val db = helper.readableDatabase
        val query = "SELECT ${DbContract.DaySignalValue.TABLE_NAME}.${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_ID} as signal_id, " +
                "${DbContract.DaySignalValue.TABLE_NAME}.${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_SCORE} as score, " +
                "${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_DESCRIPTION} as signal_description, " +
                "${DbContract.SignalValue.TABLE_NAME}.${DbContract.SignalValue.COLUMN_NAME_DESCRIPTION} as score_description " +
                "FROM ${DbContract.DaySignalValue.TABLE_NAME} " +
                "JOIN ${DbContract.Signal.TABLE_NAME} " +
                "ON ${DbContract.DaySignalValue.TABLE_NAME}.${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_ID} = ${DbContract.Signal.TABLE_NAME}.${BaseColumns._ID} " +
                "JOIN ${DbContract.SignalValue.TABLE_NAME} " +
                "ON ${DbContract.DaySignalValue.TABLE_NAME}.${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_ID} = ${DbContract.SignalValue.TABLE_NAME}.${DbContract.SignalValue.COLUMN_NAME_SIGNAL_ID} " +
                "AND ${DbContract.DaySignalValue.TABLE_NAME}.${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_SCORE} = ${DbContract.SignalValue.TABLE_NAME}.${DbContract.SignalValue.COLUMN_NAME_SCORE} " +
                "WHERE ${DbContract.DaySignalValue.COLUMN_NAME_DAY_ID} = ? " +
                "ORDER BY score DESC, signal_id ASC"
        val params = arrayOf(dayId.toString())
        val c = db.rawQuery(query, params)
        var result = mutableListOf<DaySignalValue>()
        with(c) {
            while(moveToNext()) {
               result.add(
                   DaySignalValue(
                       //getInt(getColumnIndexOrThrow(DbContract.DaySignalValue.COLUMN_NAME_DAY_ID)),
                       getInt(getColumnIndexOrThrow(DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_ID)),
                       getInt(getColumnIndexOrThrow(DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_SCORE)),
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
        var query = "SELECT ${DbContract.DaySignalValue.TABLE_NAME}.${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_ID} as signal_id, " +
                "${DbContract.DaySignalValue.TABLE_NAME}.${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_SCORE} as score, " +
                "${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_DESCRIPTION} as signal_description, " +
                "${DbContract.SignalValue.TABLE_NAME}.${DbContract.SignalValue.COLUMN_NAME_DESCRIPTION} as score_description " +
                "FROM ${DbContract.DaySignalValue.TABLE_NAME} " +
                "JOIN ${DbContract.Signal.TABLE_NAME} " +
                "ON ${DbContract.DaySignalValue.TABLE_NAME}.${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_ID} = ${DbContract.Signal.TABLE_NAME}.${BaseColumns._ID} " +
                "JOIN ${DbContract.SignalValue.TABLE_NAME} " +
                "ON ${DbContract.DaySignalValue.TABLE_NAME}.${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_ID} = ${DbContract.SignalValue.TABLE_NAME}.${DbContract.SignalValue.COLUMN_NAME_SIGNAL_ID} " +
                "AND ${DbContract.DaySignalValue.TABLE_NAME}.${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_SCORE} = ${DbContract.SignalValue.TABLE_NAME}.${DbContract.SignalValue.COLUMN_NAME_SCORE} " +
                "WHERE ${DbContract.DaySignalValue.COLUMN_NAME_DAY_ID} = ? " +
                "AND ${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_CATEGORY_ID} "

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
                        getInt(getColumnIndexOrThrow(DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_ID)),
                        getInt(getColumnIndexOrThrow(DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_SCORE)),
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
        val query = "SELECT * FROM ${DbContract.DaySignalValue.TABLE_NAME} " +
                "WHERE ${DbContract.DaySignalValue.COLUMN_NAME_DAY_ID} = ? " +
                "AND ${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_ID} = ?"
        val params = arrayOf(dayId.toString(), signalId.toString())
        val c = db.rawQuery(query, params)
        c.moveToFirst()
        c.close()
    }

    fun getDayId(date: String) : Int {
        val db = helper.readableDatabase
        val query = "SELECT * FROM ${DbContract.Day.TABLE_NAME} " +
                "WHERE ${DbContract.Day.COLUMN_NAME_DATE} = ? " +
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
       val query = "SELECT ${DbContract.Day.TABLE_NAME}.${BaseColumns._ID} as day_id, " +
               "${DbContract.Day.TABLE_NAME}.${DbContract.Day.COLUMN_NAME_DATE} as date, " +
               "${DbContract.DayComment.TABLE_NAME}.${DbContract.DayComment.COLUMN_NAME_COMMENT} as comment, " +
               "${DbContract.DaySignalValue.TABLE_NAME}.${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_SCORE} as signal_score, " +
               "COUNT(*) as count " +
               "FROM ${DbContract.Day.TABLE_NAME} " +
               "LEFT JOIN ${DbContract.DayComment.TABLE_NAME} " +
               "ON ${DbContract.Day.TABLE_NAME}.${BaseColumns._ID} = ${DbContract.DayComment.TABLE_NAME}.${DbContract.DayComment.COLUMN_NAME_DAY_ID} " +
               "LEFT JOIN ${DbContract.DaySignalValue.TABLE_NAME} " +
               "ON ${DbContract.Day.TABLE_NAME}.${BaseColumns._ID} = ${DbContract.DaySignalValue.TABLE_NAME}.${DbContract.DaySignalValue.COLUMN_NAME_DAY_ID} " +
               "GROUP BY ${DbContract.DaySignalValue.TABLE_NAME}.${DbContract.DaySignalValue.COLUMN_NAME_DAY_ID}, ${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_SCORE} " +
               "ORDER BY " +
               "${DbContract.DaySignalValue.COLUMN_NAME_DAY_ID} ASC, " +
               "${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_SCORE} ASC"
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
        val query = "INSERT INTO ${DbContract.Day.TABLE_NAME} " +
                "(" +
                "${DbContract.Day.COLUMN_NAME_DATE}" +
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
        val query = "SELECT * FROM ${DbContract.DaySignalValue.TABLE_NAME} " +
                "WHERE ${DbContract.DaySignalValue.COLUMN_NAME_DAY_ID} = ?"
        val params = arrayOf(dayId.toString())
        val c = db.rawQuery(query, params)
        val result = c.count == 0
        c.close()
        return result
    }

    fun removeDay(dayId: Int) {
        val db = helper.writableDatabase
        val query = "DELETE FROM ${DbContract.Day.TABLE_NAME} WHERE ${BaseColumns._ID} = ?"
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
            query = "DELETE FROM ${DbContract.DayComment.TABLE_NAME} " +
                    "WHERE ${DbContract.DayComment.COLUMN_NAME_DAY_ID} = ?"
            params = arrayOf(dayId.toString())
        } else {
            val existingComment = getComment(dayId)
            if (existingComment.isNullOrEmpty()) {
                query = "INSERT INTO ${DbContract.DayComment.TABLE_NAME} " +
                        "(" +
                        "${DbContract.DayComment.COLUMN_NAME_DAY_ID}, " +
                        "${DbContract.DayComment.COLUMN_NAME_COMMENT}" +
                        ") VALUES (?, ?)"
                params = arrayOf(dayId.toString(), comment)
            } else {
                query = "UPDATE ${DbContract.DayComment.TABLE_NAME} " +
                        "SET ${DbContract.DayComment.COLUMN_NAME_COMMENT} = ? " +
                        "WHERE ${DbContract.DayComment.COLUMN_NAME_DAY_ID} = ?"
                params = arrayOf(comment, dayId.toString())
            }
        }
        val c = db.rawQuery(query, params)
        c.moveToFirst()
        c.close()
    }

    fun getComment(dayId: Int) : String {
        val db = helper.readableDatabase
        val query = "SELECT ${DbContract.DayComment.COLUMN_NAME_COMMENT} FROM ${DbContract.DayComment.TABLE_NAME} " +
                "WHERE ${DbContract.DayComment.COLUMN_NAME_DAY_ID} = ?"
        val params = arrayOf(dayId.toString())
        val c = db.rawQuery(query, params)
        var result = ""
        with(c) {
            if(moveToFirst()) {
                result = getString(c.getColumnIndexOrThrow(DbContract.DayComment.COLUMN_NAME_COMMENT))
            }
            close()
        }

        return result
    }

    fun getDays() : MutableList<Day> {
        val db = helper.readableDatabase
        val query = "SELECT ${DbContract.Day.TABLE_NAME}.${BaseColumns._ID} as day_id, " +
                "${DbContract.Day.TABLE_NAME}.${DbContract.Day.COLUMN_NAME_DATE} as date, " +
                "${DbContract.DayComment.TABLE_NAME}.${DbContract.DayComment.COLUMN_NAME_COMMENT} as comment " +
                "FROM ${DbContract.Day.TABLE_NAME} " +
                "LEFT JOIN ${DbContract.DayComment.TABLE_NAME} " +
                "ON ${DbContract.Day.TABLE_NAME}.${BaseColumns._ID} = ${DbContract.DayComment.TABLE_NAME}.${DbContract.DayComment.COLUMN_NAME_DAY_ID} " +
                "ORDER BY ${DbContract.Day.COLUMN_NAME_DATE} DESC"
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
            "SELECT ${DbContract.Day.TABLE_NAME}.${BaseColumns._ID} as day_id, " +
                    "${DbContract.Day.TABLE_NAME}.${DbContract.Day.COLUMN_NAME_DATE} as date, " +
                    "${DbContract.DayComment.TABLE_NAME}.${DbContract.DayComment.COLUMN_NAME_COMMENT} as comment " +
                    "FROM ${DbContract.Day.TABLE_NAME} " +
                    "LEFT JOIN ${DbContract.DayComment.TABLE_NAME} " +
                    "ON ${DbContract.Day.TABLE_NAME}.${BaseColumns._ID} = ${DbContract.DayComment.TABLE_NAME}.${DbContract.DayComment.COLUMN_NAME_DAY_ID} " +
                    "WHERE ${DbContract.Day.TABLE_NAME}.${BaseColumns._ID} = ?"
        "ORDER BY ${DbContract.Day.COLUMN_NAME_DATE} DESC"
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

    fun reset() {
        helper.reset()
    }

    fun addCategories() {
        helper.addCategories()
    }
}