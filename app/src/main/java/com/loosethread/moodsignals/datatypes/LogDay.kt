package com.loosethread.moodsignals.datatypes

class LogDay (
    var dayId: Int,
    var description: String,
    var scoreCount: Map<Int, Int> = mapOf()
){
    override fun toString(): String {
        return description
    }
}