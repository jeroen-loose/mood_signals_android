package com.loosethread.moodsignals.views

import android.content.Context
import android.graphics.drawable.GradientDrawable
import androidx.core.content.ContextCompat
import com.loosethread.moodsignals.R
import com.loosethread.moodsignals.database.Db

class Chart : GradientDrawable
{
    val colorArray = mutableListOf<Int>()
    val percentages = mutableListOf<Float>()

    val gradientSize = .2f
    var green: Int = 0
    var orange: Int = 0
    var red: Int = 0
    var gray: Int = 0

    companion object {
        val ROUNDED_TOP = 0
        val ROUNDED_ALL = 1
    }
    constructor(context: Context, values: IntArray, style: Int? = null)  {
        green = ContextCompat.getColor(context,R.color.green)
        orange = ContextCompat.getColor(context,R.color.orange)
        red = ContextCompat.getColor(context,R.color.red)
        gray = ContextCompat.getColor(context, R.color.background_gray)

        setOrientation(Orientation.LEFT_RIGHT)
        setPercentages(values)
        if (style != null) {
            setStyle(style)
        }
    }

    fun setStyle(style: Int) {
        when (style) {
            ROUNDED_TOP -> setCornerRadii(floatArrayOf(25f, 25f, 25f, 25f, 0f, 0f, 0f, 0f))
            ROUNDED_ALL -> setCornerRadii(floatArrayOf(25f, 25f, 25f, 25f, 25f, 25f, 25f, 25f))
            else -> setCornerRadii(floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f))
        }
    }

    fun setPercentages(values : IntArray) {
        if (values.sum() == 0) {
            colorArray.add(gray)
            colorArray.add(gray)
            percentages.add(0f)
            percentages.add(100f)
        }
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