package com.loosethread.moodsignals

import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat

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