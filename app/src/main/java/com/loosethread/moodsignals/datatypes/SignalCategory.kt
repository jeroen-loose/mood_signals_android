package com.loosethread.moodsignals.datatypes

import com.loosethread.moodsignals.datatypes.SignalScore
import com.loosethread.moodsignals.database.Db

class SignalCategory (
    var id: Int?,
    var description: String?
) {
    override fun toString(): String {
        return description.toString()
    }
}