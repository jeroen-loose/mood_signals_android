package com.loosethread.moodsignals.views

import android.graphics.drawable.GradientDrawable

class Chart : GradientDrawable
{
    val colorArray = intArrayOf(
        0xff00ff00.toInt(),
        0xffffff00.toInt(),
        0xffffff00.toInt(),
        0xffff0000.toInt()
    )

    constructor(values: IntArray)  {
        setOrientation(Orientation.LEFT_RIGHT)
        setPercentages(values)
    }

    fun setPercentages(values : IntArray) {
        val percentages = floatArrayOf(
            0.toFloat(),
            values[0].toFloat() / values.sum().toFloat(),
            (values[0] + values[1]).toFloat() / values.sum().toFloat(),
            1.toFloat(),
        )
        setColors(
            colorArray,
            percentages
        )
    }
}