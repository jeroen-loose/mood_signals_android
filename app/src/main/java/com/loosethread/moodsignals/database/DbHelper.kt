package com.loosethread.moodsignals.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val SQL_CREATE_ENTRIES = arrayOf(
        "CREATE TABLE ${DbC.Signal.TBL} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${DbC.Signal.COL_DESCRIPTION} TEXT NOT NULL," +
                "${DbC.Signal.COL_ARCHIVED} BOOLEAN NOT NULL DEFAULT FALSE," +
                "${DbC.Signal.COL_ACTIVE_CHOICE} BOOLEAN NOT NULL DEFAULT FALSE," +
                "${DbC.Signal.COL_NOTIFICATION_TIME_ID} INTEGER" +
                "${DbC.Signal.COL_CATEGORY_ID} INTEGER" +
                ")",

        "CREATE TABLE ${DbC.SignalCategory.TBL} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${DbC.SignalCategory.COL_DESCRIPTION} TEXT NOT NULL" +
                ")",

        "CREATE TABLE ${DbC.SignalValue.TBL} (" +
                "${DbC.SignalValue.COL_SIGNAL_ID} INTEGER NOT NULL, " +
                "${DbC.SignalValue.COL_SCORE} INTEGER NOT NULL, " +
                "${DbC.SignalValue.COL_DESCRIPTION} TEXT NOT NULL, " +
                "PRIMARY KEY (${DbC.SignalValue.COL_SIGNAL_ID}, " +
                "${DbC.SignalValue.COL_SCORE}" +
                ")" +
                ")",

        "CREATE TABLE ${DbC.Day.TBL} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${DbC.Day.COL_DATE} TEXT NOT NULL" +
                ")",

        "CREATE TABLE ${DbC.DaySignalValue.TBL} (" +
                "${DbC.DaySignalValue.COL_DAY_ID} INTEGER NOT NULL, " +
                "${DbC.DaySignalValue.COL_SIGNAL_ID} INTEGER NOT NULL, " +
                "${DbC.DaySignalValue.COL_SIGNAL_SCORE} INTEGER NOT NULL, " +
                "PRIMARY KEY (${DbC.DaySignalValue.COL_DAY_ID}, " +
                "${DbC.DaySignalValue.COL_SIGNAL_ID}" +
                ")" +
                ")",

        "CREATE TABLE ${DbC.DayComment.TBL} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${DbC.DayComment.COL_DAY_ID} INTEGER NOT NULL," +
                "${DbC.DayComment.COL_COMMENT} TEXT NOT NULL" +
                ")",

        "CREATE TABLE ${DbC.NotificationTime.TBL} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${DbC.NotificationTime.COL_TITLE} TEXT NOT NULL," +
                "${DbC.NotificationTime.COL_QUESTION} TEXT NOT NULL," +
                "${DbC.NotificationTime.COL_TIME} TEXT NOT NULL" +
                ")",

        "INSERT INTO ${DbC.NotificationTime.TBL} (" +
                "${DbC.NotificationTime.COL_TITLE}," +
               "${DbC.NotificationTime.COL_QUESTION}, " +
                "${DbC.NotificationTime.COL_TIME}" +
                ") VALUES " +
                "('Morning', 'How did you sleep?', '07:30'), " +
                "('Evening', 'How was your day?', '21:00')",

        "INSERT INTO ${DbC.SignalCategory.TBL} (" +
                "${DbC.SignalCategory.COL_DESCRIPTION}" +
                ") VALUES " +
                "('Uncategorized'), " +
                "('Energy'), " +
                "('Mood'), " +
                "('Social'), " +
                "('Tasks'), " +
                "('Sleep')," +
                "('Self-Care')," +
                "('Work')," +
                "('Stress')"

    )

    private val SQL_ENABLE_CATEGORIES_ENTRIES = arrayOf(
        "CREATE TABLE IF NOT EXISTS ${DbC.SignalCategory.TBL} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${DbC.SignalCategory.COL_DESCRIPTION} TEXT NOT NULL" +
                ")",

        "ALTER TABLE ${DbC.Signal.TBL} ADD ${DbC.Signal.COL_CATEGORY_ID} INTEGER",

        "INSERT INTO ${DbC.SignalCategory.TBL} (" +
                "${DbC.SignalCategory.COL_DESCRIPTION}" +
                ") VALUES " +
                "('Social'), " +
                "('Tasks'), " +
                "('Sleep')," +
                "('Self-Care')," +
                "('Work')," +
                "('Stress')"
    )

    private val SQL_DELETE_ENTRIES = arrayOf(
            "DROP TABLE IF EXISTS ${DbC.Signal.TBL}",
            "DROP TABLE IF EXISTS ${DbC.SignalValue.TBL}",
            "DROP TABLE IF EXISTS ${DbC.Day.TBL}",
            "DROP TABLE IF EXISTS ${DbC.DaySignalValue.TBL}",
            "DROP TABLE IF EXISTS ${DbC.DayComment.TBL}",
            "DROP TABLE IF EXISTS ${DbC.NotificationTime.TBL}"
    )

    override fun onCreate(db: SQLiteDatabase) {
        for(sql in SQL_CREATE_ENTRIES) {
            db.execSQL(sql)
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        for(sql in SQL_DELETE_ENTRIES) {
            db.execSQL(sql)
        }
        onCreate(db)
    }
    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "MoodSignals.db"
    }

    fun reset() {
        onUpgrade(writableDatabase, 1, 1)
    }

    fun addCategories() {
        for(sql in SQL_ENABLE_CATEGORIES_ENTRIES) {
            writableDatabase.execSQL(sql)
        }
    }
}