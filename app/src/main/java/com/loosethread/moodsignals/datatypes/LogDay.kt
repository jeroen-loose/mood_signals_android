package com.loosethread.moodsignals.datatypes

import com.loosethread.moodsignals.datatypes.SignalScore
import com.loosethread.moodsignals.database.Db

class LogDay (
    var dayId: Int,
    var description: String,
    var score_count: Map<Int, Int> = mapOf()
) {
    override fun toString(): String {
        return description
    }
}