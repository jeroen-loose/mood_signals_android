package com.loosethread.moodsignals.helpers

import com.loosethread.moodsignals.R

object ColorPicker {
    fun red() = R.color.red
    fun orange() = R.color.orange
    fun green() = R.color.green

    operator fun get(score: Int): Int {
        return when (score) {
            1 -> green()
            2 -> orange()
            3 -> red()
            else -> R.color.black
        }
    }
}