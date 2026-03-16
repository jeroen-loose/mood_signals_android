package com.loosethread.moodsignals

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns

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
                "${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_NOTIFICATION_TIME_ID} as notification_time_id, " +
                "${DbContract.SignalValue.TABLE_NAME}.${DbContract.SignalValue.COLUMN_NAME_DESCRIPTION} as signal_value_description, " +
                "${DbContract.SignalValue.TABLE_NAME}.${DbContract.SignalValue.COLUMN_NAME_SCORE} as score FROM" +
                " ${DbContract.Signal.TABLE_NAME} INNER JOIN ${DbContract.SignalValue.TABLE_NAME} " +
                "ON ${DbContract.Signal.TABLE_NAME}.${BaseColumns._ID} = ${DbContract.SignalValue.TABLE_NAME}.${DbContract.SignalValue.COLUMN_NAME_SIGNAL_ID} " +
                "WHERE ${DbContract.Signal.TABLE_NAME}.${BaseColumns._ID} = ?" +
                "ORDER BY ${DbContract.SignalValue.COLUMN_NAME_SCORE} ASC"

        val params = arrayOf(id.toString())

        val cursor = db.rawQuery(query, params)

        var signal = Signal(id, null, mutableListOf<SignalScore>(), null, null)

        with(cursor) {
            while (moveToNext()) {
                signal.description = getString(getColumnIndexOrThrow("signal_description"))
                signal.activeChoice = getInt(getColumnIndexOrThrow("active_choice")) == 1
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

    fun getSignals(): MutableList<Signal> {
        val db = helper.readableDatabase
        val query = "SELECT ${DbContract.Signal.TABLE_NAME}.${BaseColumns._ID} AS id, " +
                "${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_DESCRIPTION} AS signal_description, " +
                "${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_ACTIVE_CHOICE} AS active_choice, " +
                "${DbContract.Signal.TABLE_NAME}.${DbContract.Signal.COLUMN_NAME_NOTIFICATION_TIME_ID} AS notification_time_id, " +
                "${DbContract.SignalValue.TABLE_NAME}.${DbContract.SignalValue.COLUMN_NAME_DESCRIPTION} AS signal_value_description, " +
                "${DbContract.SignalValue.COLUMN_NAME_SCORE} AS score FROM " +
                " ${DbContract.Signal.TABLE_NAME} INNER JOIN ${DbContract.SignalValue.TABLE_NAME} " +
                "ON ${DbContract.Signal.TABLE_NAME}.${BaseColumns._ID} = ${DbContract.SignalValue.TABLE_NAME}.${DbContract.SignalValue.COLUMN_NAME_SIGNAL_ID} " +
                "ORDER BY ${DbContract.Signal.TABLE_NAME}.${BaseColumns._ID} ASC, ${DbContract.SignalValue.COLUMN_NAME_SCORE} ASC"

        val cursor = db.rawQuery(query, null)
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
                "${DbContract.Signal.COLUMN_NAME_NOTIFICATION_TIME_ID} = ? " +
                "WHERE ${BaseColumns._ID} = ?"
        val params = arrayOf(
            signal.description.toString(),
            if (signal.activeChoice == true) "1" else "0",
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

            val notificationTime = NotificationTime(
                id,
                getString(getColumnIndexOrThrow("title")),
                getString(getColumnIndexOrThrow("question")),
                getString(getColumnIndexOrThrow("time"))
            )

            close()

            return notificationTime
        }
    }

    fun getNotificationTimes(): MutableList<NotificationTime> {
        val db = helper.readableDatabase
        val query = "SELECT * FROM ${DbContract.NotificationTime.TABLE_NAME}"

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

    fun deleteNotificationTime(id: Int) {
        val db = helper.writableDatabase
        val query = "DELETE FROM ${DbContract.NotificationTime.TABLE_NAME} WHERE ${BaseColumns._ID} = ?"
        val params = arrayOf(id.toString())
        val c = db.rawQuery(query, params)
        c.moveToFirst()
        c.close()
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
}