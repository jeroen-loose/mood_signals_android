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

class FragmentHomeDaysLog : Fragment() {
    var onDayChanged: ((dayId: Int) -> Unit)? = null
    var onCommentSearchToggle: ((searchVisible: Boolean) -> Unit) ?= null
    private var _binding: FragmentDaysLogBinding? = null

    private val binding get() = _binding!!
    private lateinit var viewPager: ViewPager2
    val days = Db.getDays()
    private var lastPosition: Int = 0

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
        adapter.onDaySelected = { id: Int ->
            selectDay(id)
            onDayChanged?.invoke(id)
        }

        adapter.onCommentSearchToggle = { searchVisible: Boolean ->
            onCommentSearchToggle?.invoke(searchVisible)
        }

        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val previousPosition = lastPosition

                val dayId = days[position].id
                (viewPager.adapter as DaysLogPagerAdapter).disableCommentSearch(previousPosition)
                onDayChanged?.invoke(dayId)
                lastPosition = position
            }
        })
        lastPosition = viewPager.currentItem

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

    fun selectDay(dayId: Int) {
        val dayIndex = (viewPager.adapter as DaysLogPagerAdapter).getPosition(dayId)
        viewPager.setCurrentItem(dayIndex, true)
    }

    fun showEditCommentDialog() {
        val currentDayId = days[viewPager.currentItem].id
        val fragment = (childFragmentManager.findFragmentByTag("f${currentDayId}") as? FragmentSingleDay)
        fragment?.showEditCommentDialog()
    }

    fun getDayId(): Int {
        return days[viewPager.currentItem].id
    }
}