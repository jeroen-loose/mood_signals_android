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
                "${DbContract.SignalValue.TABLE_NAME}.${DbContract.SignalValue.COLUMN_NAME_DESCRIPTION} as signal_value_description, " +
                "${DbContract.SignalValue.TABLE_NAME}.${DbContract.SignalValue.COLUMN_NAME_SCORE} as score FROM" +
                " ${DbContract.Signal.TABLE_NAME} INNER JOIN ${DbContract.SignalValue.TABLE_NAME} " +
                "ON ${DbContract.Signal.TABLE_NAME}.${BaseColumns._ID} = ${DbContract.SignalValue.TABLE_NAME}.${DbContract.SignalValue.COLUMN_NAME_SIGNAL_ID} " +
                "WHERE ${DbContract.Signal.TABLE_NAME}.${BaseColumns._ID} = ?" +
                "ORDER BY ${DbContract.SignalValue.COLUMN_NAME_SCORE} ASC"

        val params = arrayOf(id.toString())

        val cursor = db.rawQuery(query, params)

        var signal = Signal(id, null, mutableListOf<SignalScore>())

        with(cursor) {
            while (moveToNext()) {
                if(signal.description.isNullOrEmpty() ) {
                    signal.description = getString(getColumnIndexOrThrow("signal_description"))
                }

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
                        mutableListOf<SignalScore>()
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

        val query = "UPDATE ${DbContract.Signal.TABLE_NAME} SET ${DbContract.Signal.COLUMN_NAME_DESCRIPTION} = ? WHERE ${BaseColumns._ID} = ?"
        val params = arrayOf(signal.description.toString(), signal.id.toString())
        var c = db.rawQuery(query, params)
        c.moveToFirst()

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
}