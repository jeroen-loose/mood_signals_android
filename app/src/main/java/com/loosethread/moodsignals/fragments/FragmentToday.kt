package com.loosethread.moodsignals.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.loosethread.moodsignals.adapters.TodayAdapter
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.databinding.FragmentTodayBinding
import com.loosethread.moodsignals.helpers.DateHelper
import java.util.Date

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

        val date = arguments?.getString("date") ?: DateHelper.formatForSql(Date())

        if (signals.size == 0) {
            findNavController().popBackStack()
        }

        _binding = FragmentTodayBinding.inflate(inflater, container, false)
        (requireActivity() as? androidx.appcompat.app.AppCompatActivity)?.supportActionBar?.title = DateHelper.formatForDisplay(date)

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
        todayAdapter.onCategoryId = { categoryId ->
            val categoryName = when {
                categoryId > 0 -> Db.getCategory(categoryId).description
                else -> "Uncategorized"
            }

            binding.tvCategoryName.text = categoryName
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