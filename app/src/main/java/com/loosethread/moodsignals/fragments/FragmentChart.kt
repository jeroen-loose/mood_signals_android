package com.loosethread.moodsignals.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.loosethread.moodsignals.adapters.DayScoresAdapter
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.databinding.FragmentChartBinding
import com.loosethread.moodsignals.helpers.DaysLogByWeek

class FragmentChart : Fragment() {
    lateinit var adapter: DayScoresAdapter
    private lateinit var viewPager: ViewPager2

    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!

    var onDaySelected: ((dayIndex: Int) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChartBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DaysLogByWeek.groupDayScoresByWeek(Db.getDayScores())

        adapter = DayScoresAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        viewPager = binding.vpDays

        viewPager.setOffscreenPageLimit(3)
        viewPager.setLayoutDirection(View.LAYOUT_DIRECTION_RTL)

        viewPager.adapter = adapter
        adapter.onWeekSelected = { dayIndex: Int, weekIndex: Int ->
            //unset currently selected day
            (viewPager.adapter as DayScoresAdapter).setDayIndex(
                viewPager.currentItem,
                -1
            )
            //select week
            viewPager.setCurrentItem(weekIndex, true)
            //select day
            (viewPager.adapter as DayScoresAdapter).setDayIndex(
                viewPager.currentItem,
                dayIndex
            )
        }
        adapter.onDaySelected = { dayIndex ->
            onDaySelected?.invoke(dayIndex)
        }
    }

    fun selectDay(dayId: Int) {
        (viewPager.adapter as DayScoresAdapter).selectWeek(dayId)
    }
}