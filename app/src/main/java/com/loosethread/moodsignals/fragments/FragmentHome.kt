package com.loosethread.moodsignals.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.loosethread.moodsignals.R
import com.loosethread.moodsignals.databinding.FragmentHomeBinding

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FragmentHome : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            val chartFragment = FragmentChart()
            val logFragment = FragmentDaysLog()

            logFragment.onDayChanged = { dayId ->
                chartFragment.selectDay(dayId)
            }

            chartFragment.onDaySelected = { dayId ->
                logFragment.selectDay(dayId)
            }

            childFragmentManager.beginTransaction().apply {
                replace(R.id.fcvChart, chartFragment)
                replace(R.id.fcvLog, logFragment)
                setReorderingAllowed(true)
                commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}