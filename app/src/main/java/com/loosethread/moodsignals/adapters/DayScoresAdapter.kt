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
    private val manager: FragmentManager,
    lifecycle: Lifecycle,
    private val dayScoresPerWeek: MutableMap<Int, MutableMap<Int, LogDay>> = DaysLogByWeek.getWeeks()
): FragmentStateAdapter(manager, lifecycle) {
    // handles selection of new day by scrolling to individual day details
    var onWeekSelected: ((dayIndex: Int, weekIndex: Int) -> Unit)? = null
    // handles selection of new day by tapping one in the chart
    var onDaySelected: ((dayId: Int) -> Unit)? = null

    private val weekKeys: List<Int> get() = dayScoresPerWeek.keys.sortedDescending()

    private var dayIndex: Int = 0
    private var weekKey: Int = 0

    override fun getItemCount(): Int {
        return dayScoresPerWeek.size
    }

    override fun createFragment(position: Int): Fragment {
        val weekKey = weekKeys[position]
        val arguments = bundleOf(
            "index" to weekKey
        )
        val fragment = FragmentWeekChart()
        fragment.arguments = arguments
        fragment.onDaySelected = { dayId ->
           onDaySelected?.invoke(dayId)
        }
        return fragment
    }

    fun setDayIndex(viewPagerPosition: Int, dayIndex: Int) {
        val tag = "f" + getItemId(viewPagerPosition)
        val fragment = manager.findFragmentByTag(tag) as? FragmentWeekChart

        fragment?.selectDay(dayIndex)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun containsItem(itemId: Long): Boolean =
        dayScoresPerWeek[itemId.toInt()]?.isNotEmpty() ?: false

    fun selectWeek(dayId: Int) {
        for (weekIndex in dayScoresPerWeek.keys) {
            val week = dayScoresPerWeek[weekIndex]
            for (dayIndex in week!!.keys) {
               if ((week[dayIndex] as LogDay).dayId == dayId) {
                   for (index in weekKeys.indices) {
                      if (weekKeys[index] == weekIndex) {
                          if(this.dayIndex > 0 && this.weekKey > 0) {
                              val vpWeekIndex = manager.findFragmentByTag("f" + getItemId(this.weekKey)) as? FragmentWeekChart
                              vpWeekIndex?.selectDay(-1)
                          }
                          onWeekSelected?.invoke(dayIndex, index)
                          this.dayIndex = dayIndex
                          this.weekKey = index
                          return
                      }
                   }
               }
            }
        }
    }
}