package com.loosethread.moodsignals.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private val SQL_CREATE_ENTRIES = arrayOf(
        "CREATE TABLE ${DbContract.Signal.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${DbContract.Signal.COLUMN_NAME_DESCRIPTION} TEXT NOT NULL," +
                "${DbContract.Signal.COLUMN_NAME_ARCHIVED} BOOLEAN NOT NULL DEFAULT FALSE," +
                "${DbContract.Signal.COLUMN_NAME_ACTIVE_CHOICE} BOOLEAN NOT NULL DEFAULT FALSE," +
                "${DbContract.Signal.COLUMN_NAME_NOTIFICATION_TIME_ID} INTEGER" +
                "${DbContract.Signal.COLUMN_NAME_CATEGORY_ID} INTEGER" +
                ")",

        "CREATE TABLE ${DbContract.SignalCategory.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${DbContract.SignalCategory.COLUMN_NAME_DESCRIPTION} TEXT NOT NULL" +
                ")",

        "CREATE TABLE ${DbContract.SignalValue.TABLE_NAME} (" +
                "${DbContract.SignalValue.COLUMN_NAME_SIGNAL_ID} INTEGER NOT NULL, " +
                "${DbContract.SignalValue.COLUMN_NAME_SCORE} INTEGER NOT NULL, " +
                "${DbContract.SignalValue.COLUMN_NAME_DESCRIPTION} TEXT NOT NULL, " +
                "PRIMARY KEY (${DbContract.SignalValue.COLUMN_NAME_SIGNAL_ID}, " +
                "${DbContract.SignalValue.COLUMN_NAME_SCORE}" +
                ")" +
                ")",

        "CREATE TABLE ${DbContract.Day.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${DbContract.Day.COLUMN_NAME_DATE} TEXT NOT NULL" +
                ")",

        "CREATE TABLE ${DbContract.DaySignalValue.TABLE_NAME} (" +
                "${DbContract.DaySignalValue.COLUMN_NAME_DAY_ID} INTEGER NOT NULL, " +
                "${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_ID} INTEGER NOT NULL, " +
                "${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_SCORE} INTEGER NOT NULL, " +
                "PRIMARY KEY (${DbContract.DaySignalValue.COLUMN_NAME_DAY_ID}, " +
                "${DbContract.DaySignalValue.COLUMN_NAME_SIGNAL_ID}" +
                ")" +
                ")",

        "CREATE TABLE ${DbContract.DayComment.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${DbContract.DayComment.COLUMN_NAME_DAY_ID} INTEGER NOT NULL," +
                "${DbContract.DayComment.COLUMN_NAME_COMMENT} TEXT NOT NULL" +
                ")",

        "CREATE TABLE ${DbContract.NotificationTime.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${DbContract.NotificationTime.COLUMN_NAME_TITLE} TEXT NOT NULL," +
                "${DbContract.NotificationTime.COLUMN_NAME_QUESTION} TEXT NOT NULL," +
                "${DbContract.NotificationTime.COLUMN_NAME_TIME} TEXT NOT NULL" +
                ")",

        "INSERT INTO ${DbContract.NotificationTime.TABLE_NAME} (" +
                "${DbContract.NotificationTime.COLUMN_NAME_TITLE}," +
               "${DbContract.NotificationTime.COLUMN_NAME_QUESTION}, " +
                "${DbContract.NotificationTime.COLUMN_NAME_TIME}" +
                ") VALUES " +
                "('Morning', 'How did you sleep?', '07:30'), " +
                "('Evening', 'How was your day?', '21:00')",

        "INSERT INTO ${DbContract.SignalCategory.TABLE_NAME} (" +
                "${DbContract.SignalCategory.COLUMN_NAME_DESCRIPTION}" +
                ") VALUES " +
                "('Social'), " +
                "('Tasks'), " +
                "('Sleep')," +
                "('Self-Care')," +
                "('Work')," +
                "('Stress')"

    )

    private val SQL_ENABLE_CATEGORIES_ENTRIES = arrayOf(
        "CREATE TABLE IF NOT EXISTS ${DbContract.SignalCategory.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                "${DbContract.SignalCategory.COLUMN_NAME_DESCRIPTION} TEXT NOT NULL" +
                ")",

        "ALTER TABLE ${DbContract.Signal.TABLE_NAME} ADD ${DbContract.Signal.COLUMN_NAME_CATEGORY_ID} INTEGER",

        "INSERT INTO ${DbContract.SignalCategory.TABLE_NAME} (" +
                "${DbContract.SignalCategory.COLUMN_NAME_DESCRIPTION}" +
                ") VALUES " +
                "('Social'), " +
                "('Tasks'), " +
                "('Sleep')," +
                "('Self-Care')," +
                "('Work')," +
                "('Stress')"
    )

    private val SQL_DELETE_ENTRIES = arrayOf(
            "DROP TABLE IF EXISTS ${DbContract.Signal.TABLE_NAME}",
            "DROP TABLE IF EXISTS ${DbContract.SignalValue.TABLE_NAME}",
            "DROP TABLE IF EXISTS ${DbContract.Day.TABLE_NAME}",
            "DROP TABLE IF EXISTS ${DbContract.DaySignalValue.TABLE_NAME}",
            "DROP TABLE IF EXISTS ${DbContract.DayComment.TABLE_NAME}",
            "DROP TABLE IF EXISTS ${DbContract.NotificationTime.TABLE_NAME}"
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