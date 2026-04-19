package com.loosethread.moodsignals.datatypes

class Signal (
    var id: Int?,
    var description: String?,
    var scores: MutableList<SignalScore>,
    var activeChoice: Boolean?,
    var categoryId: Int?,
    var notificationTimeId: Int?,
    var archived: Boolean = false
)