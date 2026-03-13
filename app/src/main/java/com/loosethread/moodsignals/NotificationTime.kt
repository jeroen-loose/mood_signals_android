package com.loosethread.moodsignals

class NotificationTime (
    var id: Int?,
    var question: String?,
    var time: String?
) {
    constructor(id: Int) : this(id, null, null) {
        val tmp = Db.getNotificationTime(id)
        question = tmp.question
        time = tmp.time
    }

    override fun toString(): String {
        return time.toString()
    }
}

