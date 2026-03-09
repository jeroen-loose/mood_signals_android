package com.loosethread.moodsignals

data class Signal (
    val description: String,
    val scores: List<SignalScore>
)