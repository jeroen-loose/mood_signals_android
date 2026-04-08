package com.loosethread.moodsignals.adapters

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.loosethread.moodsignals.datatypes.LogDay
import com.loosethread.moodsignals.fragments.FragmentWeekChart

class DayScoresAdapter(
    manager: FragmentManager,
    lifecycle: Lifecycle,
    private val dayScoresPerWeek: MutableList<MutableMap<Int, LogDay>>
): FragmentStateAdapter(manager, lifecycle) {

    override fun getItemCount(): Int {
        return dayScoresPerWeek.size
    }

    override fun createFragment(position: Int): Fragment {
        val arguments = bundleOf(
            "index" to position,
            "scores" to dayScoresPerWeek[position]
        )
        val result = FragmentWeekChart()
        result.arguments = arguments
        return result
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun containsItem(itemId: Long): Boolean = dayScoresPerWeek[itemId.toInt()].isNotEmpty()
}