package com.loosethread.moodsignals.adapters

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.loosethread.moodsignals.datatypes.LogDay
import com.loosethread.moodsignals.fragments.FragmentWeekChart
import com.loosethread.moodsignals.helpers.DaysLogByWeek

class DayScoresAdapter(
    manager: FragmentManager,
    lifecycle: Lifecycle,
    private val dayScoresPerWeek: MutableMap<Int, MutableMap<Int, LogDay>> = DaysLogByWeek.getWeeks()
): FragmentStateAdapter(manager, lifecycle) {

    private val weekKeys: List<Int> get() = dayScoresPerWeek.keys.sortedDescending()

    override fun getItemCount(): Int {
        return dayScoresPerWeek.size
    }

    override fun createFragment(position: Int): Fragment {
        val weekKey = weekKeys[position]
        val arguments = bundleOf(
            "index" to weekKey
        )
        val result = FragmentWeekChart()
        result.arguments = arguments
        return result
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun containsItem(itemId: Long): Boolean =
        dayScoresPerWeek[itemId.toInt()]?.isNotEmpty() ?: false
}