package com.loosethread.moodsignals

class Signal (
    var id: Int?,
    var description: String?,
    var scores: MutableList<SignalScore>,
    var activeChoice: Boolean?,
    var notificationTimeId: Int?,
    var archived: Boolean = false
) {
    constructor(id: Int) : this(id, null, mutableListOf<SignalScore>(), null, null) {
        val tmp = Db.getSignal(id)
        description = tmp.description
        scores = tmp.scores
        activeChoice = tmp.activeChoice
        notificationTimeId = tmp.notificationTimeId
        archived = tmp.archived
    }
}

