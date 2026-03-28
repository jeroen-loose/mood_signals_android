package com.loosethread.moodsignals.fragments

import com.loosethread.moodsignals.fragments.EditCommentFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.loosethread.moodsignals.FullWidthLinearLayoutManager
import com.loosethread.moodsignals.adapters.LogSignalAdapter
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.databinding.ItemDayLogBinding
import com.loosethread.moodsignals.datatypes.Day
import com.loosethread.moodsignals.datatypes.DaySignalValue
import com.loosethread.moodsignals.helpers.DateManager

class FragmentSingleDay(): Fragment() {
    private val dayId: Int by lazy { requireArguments().getInt("dayId") }
    private lateinit var day: Day
    private lateinit var scores: MutableList<DaySignalValue>
    private var _binding: ItemDayLogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        day = Db.getDay(dayId)
        scores = Db.getDaySignalValues(dayId)

        _binding = ItemDayLogBinding.inflate(inflater, container, false)

        val logSignalAdapter = LogSignalAdapter(scores)
        binding.rvDaySignalsLog.adapter = logSignalAdapter
        binding.rvDaySignalsLog.layoutManager =
            FullWidthLinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)

        val dividerItemDecoration = DividerItemDecoration(binding.root.context, LinearLayoutManager.VERTICAL)
        binding.rvDaySignalsLog.addItemDecoration(dividerItemDecoration)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvComment.text = day.comment
        val requestKey = "edit_comment_request_$dayId"

        binding.llComment.setOnClickListener {
            val dialog = EditCommentFragment()
            dialog.arguments = bundleOf(
                "comment" to day.comment,
                "requestKey" to requestKey
            )
            dialog.show(parentFragmentManager, "EditCommentFragment")
        }

        binding.tvDate.text = DateManager.formatStringForDisplay(day.date)

        setFragmentResultListener(requestKey) { _, bundle ->
            val updated = bundle.getBoolean("isUpdated", false)
            if (updated) {
                day.comment = bundle.getString("comment")
                Db.updateComment(dayId, day.comment)
                binding.tvComment.text = day.comment
            }
        }
    }

    override fun onResume() {
        super.onResume()
        day = Db.getDay(dayId)
        scores = Db.getDaySignalValues(dayId)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}