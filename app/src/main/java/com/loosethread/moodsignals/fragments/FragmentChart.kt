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
import com.loosethread.moodsignals.datatypes.Day
import com.loosethread.moodsignals.datatypes.LogDay
import java.time.LocalDate
import java.util.Calendar

class FragmentChart : Fragment() {
    lateinit var adapter: DayScoresAdapter
    private lateinit var viewPager: ViewPager2

    private var _binding: FragmentChartBinding? = null
    private val binding get() = _binding!!

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

        val scores = groupDayScoresByWeek(Db.getDayScores())

        adapter = DayScoresAdapter(childFragmentManager, viewLifecycleOwner.lifecycle, scores)
        viewPager = binding.vpDays

        viewPager.setOffscreenPageLimit(3)
        viewPager.setLayoutDirection(View.LAYOUT_DIRECTION_LTR)

        viewPager.adapter = adapter
        viewPager.setCurrentItem(viewPager.childCount, false)
    }

    companion object {
        private val calendar = Calendar.getInstance()
        private lateinit var day: Day

        fun groupDayScoresByWeek(dayScores: MutableList<LogDay>): MutableList<MutableMap<Int, LogDay>> {
            val result = mutableListOf<MutableMap<Int, LogDay>>()
            var previousWeek = -1
            var resultIndex = 0
            var week = -1
            var weekday: Int
            calendar.setFirstDayOfWeek(Calendar.MONDAY)

            for (dayScore in dayScores) {
                day = Db.getDay(dayScore.dayId)
                val yearInt = day.date.substring(0, 4).toInt()
                val monthInt = day.date.substring(5, 7).toInt()
                val dayInt = day.date.substring(8, 10).toInt()

                //calendar.set(yearInt, monthInt, dayInt)
                calendar.set(Calendar.YEAR, yearInt)
                calendar.set(Calendar.MONTH, monthInt)
                calendar.set(Calendar.DAY_OF_MONTH, dayInt)
                week = calendar.get(Calendar.WEEK_OF_YEAR)
                weekday = LocalDate.of(yearInt, monthInt, dayInt).getDayOfWeek().getValue()
                // TODO clean solution that doesn't break when changing years
                if (weekday == 7) week -= 1

                if (week != previousWeek) {
                    result.add(mutableMapOf<Int, LogDay>())
                    resultIndex = result.size - 1
                    previousWeek = week
                }
                result[resultIndex].put(weekday, dayScore)
            }
            return result
        }
    }
}