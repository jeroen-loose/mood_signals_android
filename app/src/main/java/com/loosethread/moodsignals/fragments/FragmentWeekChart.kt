package com.loosethread.moodsignals.fragments

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.loosethread.moodsignals.R
import com.loosethread.moodsignals.databinding.FragmentWeekChartBinding
import com.loosethread.moodsignals.datatypes.LogDay
import com.loosethread.moodsignals.helpers.DaysLogByWeek
import com.loosethread.moodsignals.views.Chart

class FragmentWeekChart : Fragment() {
    private var _binding: FragmentWeekChartBinding? = null
    private val binding get() = _binding!!

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

            val scores = DaysLogByWeek.getWeek(requireArguments().get("index") as Int)
            for (i in 1..7) {
                val score = scores!!.get(i)
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

                if (score != null) {
                    val background = Chart(requireContext(), intArrayOf(
                        score.score_count[1] ?: 0,
                        score.score_count[2] ?: 0,
                        score.score_count[3] ?: 0
                    ))
                    background.setOrientation(GradientDrawable.Orientation.TOP_BOTTOM)
                    chartContainer.background = background
                }
            }
    }

    fun selectDay(index: Int) {
        for (i in 1..7) {
            val tvDayOfWeek = binding.root.findViewById<TextView>(
                resources.getIdentifier(
                    "tvWeekday$i",
                    "id",
                    requireContext().packageName
                )
            )
            val chartContainer = binding.root.findViewById<View>(
                resources.getIdentifier(
                    "chartContainer$i",
                    "id",
                    requireContext().packageName
                )
            )

            if (i == index) {
                tvDayOfWeek.setVisibility(View.VISIBLE)
            } else {
                tvDayOfWeek.setVisibility(View.INVISIBLE)
            }
        }

    }
}