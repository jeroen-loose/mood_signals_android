package com.loosethread.moodsignals

class Day (
    var id: Int,
    var date: String,
    var comment: String?,
    var scores: MutableList<DaySignalValue>,
)