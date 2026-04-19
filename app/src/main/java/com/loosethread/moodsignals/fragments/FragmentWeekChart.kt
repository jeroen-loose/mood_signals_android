package com.loosethread.moodsignals.fragments

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.loosethread.moodsignals.R
import com.loosethread.moodsignals.databinding.FragmentWeekChartBinding
import com.loosethread.moodsignals.helpers.DaysLogByWeek
import com.loosethread.moodsignals.views.Chart

class FragmentWeekChart : Fragment() {
    private var _binding: FragmentWeekChartBinding? = null
    private val binding get() = _binding!!
    private var dayIndex: Int = 0
    private var weekKey: Int = 0

    var onDaySelected: ((dayId: Int) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWeekChartBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            weekKey = requireArguments().get("index") as Int

            val scores = DaysLogByWeek.getWeek(weekKey)
        for (i in 1..7) {
            val score = scores?.get(i)
            if (score != null) {
                val tvDayOfWeek = binding.root.findViewById<TextView>(resources.getIdentifier("tvWeekday$i", "id", requireContext().packageName))
                val chartContainer = binding.root.findViewById<View>(resources.getIdentifier("chartContainer$i", "id", requireContext().packageName))

                tvDayOfWeek.text = when (i) {
                    1 -> "M"
                    2 -> "T"
                    3 -> "W"
                    4 -> "T"
                    5 -> "F"
                    6 -> "S"
                    7 -> "S"
                    else -> {
                        ""
                    }
                }

                val background = Chart(requireContext(), intArrayOf(
                    score?.scoreCount[1] ?: 0,
                    score?.scoreCount[2] ?: 0,
                    score?.scoreCount[3] ?: 0
                ))
                background.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM)
                chartContainer.background = background

                tvDayOfWeek.setOnClickListener {
                    dayClicked(score?.dayId ?: 0)
                }
                chartContainer.setOnClickListener {
                    dayClicked(score?.dayId ?: 0)
                }
            }
        }
    }

    fun selectDay(index: Int) {
        if (dayIndex > 0) {
            binding.root.findViewById<TextView>(
                resources.getIdentifier(
                    "tvWeekday$dayIndex",
                    "id",
                    requireContext().packageName
                )
            )?.background = null
        }
        if (index > 0) {
            binding.root.findViewById<TextView>(
                resources.getIdentifier(
                    "tvWeekday$index",
                    "id",
                    requireContext().packageName
                )
            )?.background = resources.getDrawable(R.drawable.circle_background)
        }
        dayIndex = index
    }

    fun dayClicked(index: Int) {
        selectDay(index)
        onDaySelected?.invoke(index)
    }
}