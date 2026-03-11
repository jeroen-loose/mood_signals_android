package com.loosethread.moodsignals

class Signal (
    var id: Int?,
    var description: String?,
    var scores: MutableList<SignalScore>
) {
    constructor(id: Int) : this(id, null, mutableListOf<SignalScore>()) {
        val tmp = Db.getSignal(id)
        description = tmp.description
        scores = tmp.scores
    }
}

