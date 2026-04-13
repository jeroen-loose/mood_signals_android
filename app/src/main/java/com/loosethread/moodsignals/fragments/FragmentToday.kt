package com.loosethread.moodsignals.fragments

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.loosethread.moodsignals.FullWidthLinearLayoutManager
import com.loosethread.moodsignals.adapters.TodayAdapter
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.databinding.FragmentTodayBinding
import com.loosethread.moodsignals.datatypes.NotificationTime
import com.loosethread.moodsignals.dialogs.DatePickerDialog
import com.loosethread.moodsignals.helpers.DateManager

class FragmentToday : Fragment() {
    private var _binding: FragmentTodayBinding? = null
    lateinit var todayAdapter: TodayAdapter

    private val binding get() = _binding!!
    private val notificationTimes = Db.getNotificationTimes()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationId = arguments?.getInt("notification_time_id") ?: -1
        val notificationTime = notificationTimes.find { it.id == notificationId }?: notificationTimes[0]
        val signals = Db.getSignalsByNotificationTime(notificationTime.id)

        val date = arguments?.getString("date") ?: DateManager.formatForSql()

        if (signals.size == 0) {
            findNavController().popBackStack()
        }

        _binding = FragmentTodayBinding.inflate(inflater, container, false)

        binding.tvDate.text = DateManager.formatForDisplay(date)

        todayAdapter =
            TodayAdapter(
                childFragmentManager,
                lifecycle,
                signals,
                date
            )
        todayAdapter.onScoreSelected = { signalId, score, isLastItem ->
            if (score != -1) {
                Db.insertDaySignalValue(Db.getDayId(date), signalId, score)
            } else {
                Db.removeDaySignalValue(Db.getDayId(date), signalId)
            }
            if (isLastItem) {
                binding.llComment.setVisibility(View.VISIBLE)
                binding.btnDone.setOnClickListener {
                    val comment = binding.etComment.text.toString()
                    Db.updateComment(todayAdapter.dayId, comment)

                    findNavController().popBackStack()
                }
            } else {
                binding.llComment.setVisibility(View.INVISIBLE)
                binding.vpSignals.setCurrentItem(binding.vpSignals.currentItem + 1, true)
            }
        }
        binding.vpSignals.adapter = todayAdapter

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etComment.setText(Db.getComment(todayAdapter.dayId))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun resetViews() {
        binding.llComment.setVisibility(View.GONE)
    }

}