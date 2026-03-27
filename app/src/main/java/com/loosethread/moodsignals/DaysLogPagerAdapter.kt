package com.loosethread.moodsignals

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class DaysLogPagerAdapter(activity: FragmentActivity, val days: MutableList<Day>): FragmentStateAdapter(activity) {

    override fun getItemCount(): Int = days.size

    override fun createFragment(position: Int): Fragment {
        val arguments = bundleOf("dayId" to days[position].id)
        val result = FragmentSingleDay()
        result.arguments = arguments
        return result
    }

    override fun getItemId(position: Int): Long {
       return days[position].id.toLong()
    }
}