package com.loosethread.moodsignals

import android.provider.BaseColumns

object DbContract {
    object Signal : BaseColumns {
        const val TABLE_NAME = "signals"
        const val COLUMN_NAME_DESCRIPTION = "description"
        const val COLUMN_NAME_ARCHIVED = "archived"
        const val COLUMN_NAME_ACTIVE_CHOICE = "active_choice"
        const val COLUMN_NAME_NOTIFICATION_TIME_ID = "notification_time_id"
    }

    object SignalValue : BaseColumns {
        const val TABLE_NAME = "signal_values"
        const val COLUMN_NAME_SIGNAL_ID = "signal_id"
        const val COLUMN_NAME_SCORE = "score"
        const val COLUMN_NAME_DESCRIPTION = "description"
    }

    object Day : BaseColumns {
        const val TABLE_NAME = "days"
        const val COLUMN_NAME_DATE = "date"
    }

    object DaySignalValue : BaseColumns {
        const val TABLE_NAME = "day_signal_values"
        const val COLUMN_NAME_DAY_ID = "day_id"
        const val COLUMN_NAME_SIGNAL_ID = "signal_id" // only required to make unique index of day_id and signal_id
        const val COLUMN_NAME_SIGNAL_SCORE = "score"
    }

    object DayComment : BaseColumns {
        const val TABLE_NAME = "day_comments"
        const val COLUMN_NAME_DAY_ID = "day_id"
        const val COLUMN_NAME_COMMENT = "comment"
    }

    object NotificationTime : BaseColumns {
        const val TABLE_NAME = "notification_times"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_QUESTION = "question"
        const val COLUMN_NAME_TIME = "time"
    }
}