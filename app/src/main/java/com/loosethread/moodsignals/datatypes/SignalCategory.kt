package com.loosethread.moodsignals.datatypes

class SignalCategory (
    var id: Int?,
    var description: String?
) {
    override fun toString(): String {
        return description.toString()
    }
}