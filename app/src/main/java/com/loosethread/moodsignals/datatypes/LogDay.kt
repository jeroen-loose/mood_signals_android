package com.loosethread.moodsignals.datatypes

class LogDay (
    var dayId: Int,
    var description: String,
    var score_count: Map<Int, Int> = mapOf()
){
    override fun toString(): String {
        return description
    }
}