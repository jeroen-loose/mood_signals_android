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
import com.loosethread.moodsignals.helpers.DaysLogByWeek
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

        DaysLogByWeek.groupDayScoresByWeek(Db.getDayScores())

        adapter = DayScoresAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        viewPager = binding.vpDays

        viewPager.setOffscreenPageLimit(3)
        viewPager.setLayoutDirection(View.LAYOUT_DIRECTION_RTL)

        viewPager.adapter = adapter
    }
}