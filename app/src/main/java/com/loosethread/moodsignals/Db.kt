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


    fun getSignals(): MutableList<Signal> {
        val db = helper.readableDatabase
        val projection = arrayOf(BaseColumns._ID, DbContract.Signal.COLUMN_NAME_DESCRIPTION)
        val cursor = db.query(
            DbContract.Signal.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null
        )
        val signals = mutableListOf<Signal>()
        with(cursor) {
            while (moveToNext()) {
                val description =
                    getString(getColumnIndexOrThrow(DbContract.Signal.COLUMN_NAME_DESCRIPTION))
                signals.add(Signal(description, listOf()))
            }
        }
        cursor.close()
        return signals
    }

    fun addSignal(signal: Signal) {
        val db = helper.writableDatabase
        val values = ContentValues().apply {
            put(DbContract.Signal.COLUMN_NAME_DESCRIPTION, signal.description)
        }

        val newSignalId = db.insert(DbContract.Signal.TABLE_NAME, null, values)

        for(score in signal.scores) {
            val values = ContentValues().apply {
                put(DbContract.SignalValue.COLUMN_NAME_SIGNAL_ID, newSignalId)
                put(DbContract.SignalValue.COLUMN_NAME_SCORE, score.score)
                put(DbContract.SignalValue.COLUMN_NAME_DESCRIPTION, score.description)
            }

            db.insert(DbContract.SignalValue.TABLE_NAME, null, values)
        }
    }
}