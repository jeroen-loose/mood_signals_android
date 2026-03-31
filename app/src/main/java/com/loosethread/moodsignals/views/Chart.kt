package com.loosethread.moodsignals.views

import android.graphics.drawable.GradientDrawable

class Chart : GradientDrawable
{
    val colorArray = mutableListOf<Int>()
    val percentages = mutableListOf<Float>()

    val gradientSize = .2f

    constructor(values: IntArray)  {
        setOrientation(Orientation.LEFT_RIGHT)
        setPercentages(values)
    }

    fun setPercentages(values : IntArray) {
        if (values[0] > 0) {
            colorArray.add(0xff00ff00.toInt())
            colorArray.add(0xff00ff00.toInt())
            percentages.add(0f)
            percentages.add((values[0].toFloat() - gradientSize) / values.sum().toFloat())
        }

        if (values[1] > 0) {
            colorArray.add(0xffffff00.toInt())
            colorArray.add(0xffffff00.toInt())
            percentages.add((values[0].toFloat() + gradientSize) / values.sum().toFloat())
            percentages.add((values[0].toFloat() + values[1].toFloat() - gradientSize) / values.sum().toFloat())
        }

        if (values[2] > 0) {
            colorArray.add(0xffff0000.toInt())
            colorArray.add(0xffff0000.toInt())
            percentages.add((values[0].toFloat() + values[1].toFloat() + gradientSize) / values.sum().toFloat())
            percentages.add(1f)
        }

        setColors(
            colorArray.toIntArray(),
            percentages.toFloatArray()
        )
    }
}