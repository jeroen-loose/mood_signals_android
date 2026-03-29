package com.loosethread.moodsignals.adapters

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.loosethread.moodsignals.fragments.FragmentSingleDay
import com.loosethread.moodsignals.datatypes.Day

class DaysLogPagerAdapter(
    manager: FragmentManager,
    lifecycle: Lifecycle,
    private val days: MutableList<Day>
): FragmentStateAdapter(manager, lifecycle) {

    override fun getItemCount(): Int = days.size

    override fun createFragment(position: Int): Fragment {
        val arguments = bundleOf("dayId" to getItemId(position).toInt())
        val result = FragmentSingleDay()
        result.arguments = arguments
        return result
    }

    override fun getItemId(position: Int): Long {
       return days[position].id.toLong()
    }

    override fun containsItem(itemId: Long): Boolean = days.any { it.id.toLong() == itemId }
}