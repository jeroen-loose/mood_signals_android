package com.loosethread.moodsignals.adapters

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.loosethread.moodsignals.fragments.FragmentSingleDay
import com.loosethread.moodsignals.datatypes.Day
import com.loosethread.moodsignals.fragments.FragmentWeekChart

class DaysLogPagerAdapter(
    private val manager: FragmentManager,
    lifecycle: Lifecycle,
    private val days: MutableList<Day>
): FragmentStateAdapter(manager, lifecycle) {
    var onDaySelected: ((dayId: Int) -> Unit) ?= null
    var onCommentSearchToggle: ((searchVisible: Boolean) -> Unit) ?= null

    override fun getItemCount(): Int = days.size

    override fun createFragment(position: Int): Fragment {
        val dayId = getItemId(position).toInt()
        val arguments = bundleOf("dayId" to dayId)
        val result = FragmentSingleDay()
        result.arguments = arguments

        result.onDaySelected = { id: Int ->
            onDaySelected?.invoke(id)
        }
        result.onCommentSearchToggle = { searchVisible: Boolean ->
            onCommentSearchToggle?.invoke(searchVisible)
        }

        return result
    }

    override fun getItemId(position: Int): Long {
       return days[position].id.toLong()
    }

    override fun containsItem(itemId: Long): Boolean = days.any { it.id.toLong() == itemId }

    fun getPosition(dayId: Int) : Int {
        return days.indexOfFirst { it.id == dayId }
    }

    fun showEditCommentDialog(position: Int) {

    }

    fun disableCommentSearch(viewPagerPosition: Int) {
        val tag = "f" + getItemId(viewPagerPosition)
        val fragment = manager.findFragmentByTag(tag) as? FragmentSingleDay

        fragment?.disableCommentSearch()
    }
}