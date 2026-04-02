package com.loosethread.moodsignals.views

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import com.loosethread.moodsignals.R

class Chart : GradientDrawable
{
    val colorArray = mutableListOf<Int>()
    val percentages = mutableListOf<Float>()

    val gradientSize = .2f
    var green: Int = 0
    var orange: Int = 0
    var red: Int = 0

    constructor(context: Context, values: IntArray)  {
        green = ContextCompat.getColor(context,R.color.green)
        orange = ContextCompat.getColor(context,R.color.orange)
        red = ContextCompat.getColor(context,R.color.red)

        setOrientation(Orientation.LEFT_RIGHT)
        setPercentages(values)
        cornerRadius = 25f
    }

    fun setPercentages(values : IntArray) {
        if (values[0] > 0) {
            colorArray.add(green)
            colorArray.add(green)
            percentages.add(0f)
            percentages.add((values[0].toFloat() - gradientSize) / values.sum().toFloat())
        }

        if (values[1] > 0) {
            colorArray.add(orange)
            colorArray.add(orange)
            percentages.add((values[0].toFloat() + gradientSize) / values.sum().toFloat())
            percentages.add((values[0].toFloat() + values[1].toFloat() - gradientSize) / values.sum().toFloat())
        }

        if (values[2] > 0) {
            colorArray.add(red)
            colorArray.add(red)
            percentages.add((values[0].toFloat() + values[1].toFloat() + gradientSize) / values.sum().toFloat())
            percentages.add(1f)
        }

        setColors(
            colorArray.toIntArray(),
            percentages.toFloatArray()
        )
    }
}