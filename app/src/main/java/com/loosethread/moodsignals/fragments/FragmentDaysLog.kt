package com.loosethread.moodsignals.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.loosethread.moodsignals.adapters.DaysLogPagerAdapter
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.databinding.FragmentDaysLogBinding

class FragmentDaysLog : Fragment() {
    var onDayChanged: ((dayId: Int) -> Unit)? = null
    private var _binding: FragmentDaysLogBinding? = null

    private val binding get() = _binding!!
    private lateinit var viewPager: ViewPager2
    val days = Db.getDays()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDaysLogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = binding.vpDays
        val adapter = DaysLogPagerAdapter(childFragmentManager, viewLifecycleOwner.lifecycle, days)
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val dayId = days[position].id
                onDayChanged?.invoke(dayId)
            }
        })

        val child = viewPager.getChildAt(0) as? androidx.recyclerview.widget.RecyclerView
        child?.let {
            it.clipToPadding = false
            it.clipChildren = false
        }
        viewPager.setOffscreenPageLimit(3)
        viewPager.setLayoutDirection(View.LAYOUT_DIRECTION_RTL)

    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}