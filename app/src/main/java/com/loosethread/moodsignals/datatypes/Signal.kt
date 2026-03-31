package com.loosethread.moodsignals.datatypes

import com.loosethread.moodsignals.datatypes.SignalScore
import com.loosethread.moodsignals.database.Db

class Signal (
    var id: Int?,
    var description: String?,
    var scores: MutableList<SignalScore>,
    var activeChoice: Boolean?,
    var categoryId: Int?,
    var notificationTimeId: Int?,
    var archived: Boolean = false
) {
    constructor(id: Int) : this(id, null, mutableListOf<SignalScore>(), null, null, null) {
        val tmp = Db.getSignal(id)
        description = tmp.description
        scores = tmp.scores
        activeChoice = tmp.activeChoice
        categoryId = tmp.categoryId
        notificationTimeId = tmp.notificationTimeId
        archived = tmp.archived
    }
}