package com.loosethread.moodsignals

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FullWidthLinearLayoutManager : LinearLayoutManager {
    constructor (c: Context) : super (c)
    constructor (c: Context, orientation: Int, reverseLayout: Boolean) : super (c, orientation, reverseLayout)

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return spanLayoutWidth(super.generateDefaultLayoutParams())
    }

    private fun spanLayoutWidth (params: RecyclerView.LayoutParams) : RecyclerView.LayoutParams {
        params.width = getHorizontalSpace()
        return params

    }

    private fun getHorizontalSpace(): Int {
        return getWidth() - getPaddingLeft() - getPaddingRight()
    }

}