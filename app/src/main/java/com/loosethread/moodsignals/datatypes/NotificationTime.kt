package com.loosethread.moodsignals.datatypes

import com.loosethread.moodsignals.database.Db

class NotificationTime (
    var id: Int?,
    var title: String?,
    var question: String?,
    var time: String?
) {
    constructor(id: Int) : this(id, null, null, null) {
        val tmp = Db.getNotificationTime(id)
        title = tmp.title
        question = tmp.question
        time = tmp.time
    }

    override fun toString(): String {
        return title.toString()
    }
}