package com.loosethread.moodsignals.database

import android.provider.BaseColumns

object DbC {
    object Signal : BaseColumns {
        const val TBL = "signals"
        const val COL_DESCRIPTION = "description"
        const val COL_ARCHIVED = "archived"
        const val COL_ACTIVE_CHOICE = "active_choice"
        const val COL_NOTIFICATION_TIME_ID = "notification_time_id"
        const val COL_CATEGORY_ID = "category_id"
    }

    object SignalCategory : BaseColumns {
        const val TBL = "signal_categories"
        const val COL_DESCRIPTION = "description"
    }

    object SignalValue : BaseColumns {
        const val TBL = "signal_values"
        const val COL_SIGNAL_ID = "signal_id"
        const val COL_SCORE = "score"
        const val COL_DESCRIPTION = "description"
    }

    object Day : BaseColumns {
        const val TBL = "days"
        const val COL_DATE = "date"
    }

    object DaySignalValue : BaseColumns {
        const val TBL = "day_signal_values"
        const val COL_DAY_ID = "day_id"
        const val COL_SIGNAL_ID = "signal_id"
        const val COL_SIGNAL_SCORE = "score"
    }

    object DayComment : BaseColumns {
        const val TBL = "day_comments"
        const val COL_DAY_ID = "day_id"
        const val COL_COMMENT = "comment"
    }

    object NotificationTime : BaseColumns {
        const val TBL = "notification_times"
        const val COL_TITLE = "title"
        const val COL_QUESTION = "question"
        const val COL_TIME = "time"
    }
}