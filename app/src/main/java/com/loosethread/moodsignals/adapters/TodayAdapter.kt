package com.loosethread.moodsignals.adapters

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.datatypes.Signal
import com.loosethread.moodsignals.fragments.FragmentSingleItemToday


class TodayAdapter(
    manager: FragmentManager,
    lifecycle: Lifecycle,
    private val signals: MutableList<Signal>,
    private var selectedDate: String
): FragmentStateAdapter(manager, lifecycle) {

    var dayId = Db.getDayId(selectedDate)
    var categoryId: Int = -1
    var onScoreSelected: ((signalId: Int, score: Int, isLastItem: Boolean) -> Unit)? = null
    var onCategoryId: ((categoryId: Int) -> Unit)? = null

    override fun getItemCount(): Int = signals.size

    override fun createFragment(position: Int): Fragment {
        var isLastItem: Boolean
        if (position < signals.size - 1) {
            isLastItem = false
        } else {
            isLastItem = true
        }

        val arguments = bundleOf(
            "signalId" to getItemId(position).toInt(),
            "dayId" to Db.getDayId(selectedDate)
        )
        val result = FragmentSingleItemToday()
        result.onScoreSelected = { signalId, score ->
            onScoreSelected?.invoke(signalId, score, isLastItem)
        }
        result.onCategoryId = { categoryId ->
            if (this.categoryId != categoryId) {
                this.categoryId = categoryId
                onCategoryId?.invoke(categoryId)
            }
        }
        result.arguments = arguments

        return result
    }

    override fun getItemId(position: Int): Long {
        return signals[position].id!!.toLong()
    }

    override fun containsItem(itemId: Long): Boolean = signals.any { it.id!!.toLong() == itemId }

}