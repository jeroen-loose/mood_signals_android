package com.loosethread.moodsignals.datatypes

class NotificationTime (
    var id: Int?,
    var title: String?,
    var question: String?,
    var time: String?
) {
    override fun toString(): String {
        return title.toString()
    }
}