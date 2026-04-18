package com.loosethread.moodsignals.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.loosethread.moodsignals.R
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.databinding.FragmentHomeBinding

class FragmentHome : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var fabOpened = false
    private lateinit var chartFragment: FragmentHomeChart
    private lateinit var logFragment: FragmentHomeDaysLog

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
            chartFragment = FragmentHomeChart()
            logFragment = FragmentHomeDaysLog()

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

            setupFab()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setupFab() {
        val notificationTimes = Db.getNotificationTimes()
        for (notificationTime in notificationTimes) {
            val tvNotificationTime = TextView(requireContext())
            tvNotificationTime.text = "Run '" + notificationTime.title + "'"
            tvNotificationTime.setOnClickListener {
                editDay(notificationTime.id)
                //findNavController().navigate(R.id.action_HomeFragment_to_fragmentToday)
            }
            tvNotificationTime.setPadding(0, 0, 0, 16)
            binding.fabOptions.addView(tvNotificationTime)
        }
        binding.fabButtonOpenClose.setOnClickListener { v ->
            toggleFabOptions()
        }

        binding.tvEditComment.setOnClickListener { v ->
            logFragment.showEditCommentDialog()
            //findNavController().navigate(R.id.action_HomeFragment_to_fragmentToday)
        }
    }

    fun toggleFabOptions() {
        if (fabOpened) {
            binding.fabButtonOpenClose.background =  resources.getDrawable(android.R.drawable.ic_menu_add)
            binding.fabOptions.visibility = View.GONE

        } else {
            binding.fabButtonOpenClose.background =  resources.getDrawable(android.R.drawable.ic_menu_close_clear_cancel)
            binding.fabOptions.visibility = View.VISIBLE
        }
        fabOpened = !fabOpened
    }

    fun editDay(notificationTimeId: Int?) {
        val bundle = Bundle()
        bundle.putInt("notification_time_id", notificationTimeId ?: 0)
        bundle.putString("date", Db.getDay(logFragment.getDayId()).date)
        val navController = findNavController()
        if (navController.currentDestination?.id == R.id.fragmentHome) {
            findNavController().navigate(R.id.action_HomeFragment_to_fragmentToday, bundle)
        }
    }
}