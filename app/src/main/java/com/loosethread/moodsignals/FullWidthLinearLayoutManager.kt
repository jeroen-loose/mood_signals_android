package com.loosethread.moodsignals

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FullWidthLinearLayoutManager : LinearLayoutManager {
    constructor (c: Context) : super (c)
    constructor (c: Context, orientation: Int, reverseLayout: Boolean) : super (c, orientation, reverseLayout)
    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return spanLayoutDimensions(super.generateDefaultLayoutParams())
    }

    private fun spanLayoutDimensions (params: RecyclerView.LayoutParams) : RecyclerView.LayoutParams {
        params.width = getHorizontalSpace()
        return params
    }

    private fun getHorizontalSpace(): Int {
        return getWidth() - getPaddingLeft() - getPaddingRight()
    }
}