package com.loosethread.moodsignals

import EditCommentFragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.loosethread.moodsignals.databinding.ItemDayLogBinding

class FragmentSingleDay(): Fragment() {
    private val dayId: Int by lazy { arguments?.getInt("dayId") ?: -1}
    private lateinit var day: Day
    private var _binding: ItemDayLogBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        day = Db.getDay(dayId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ItemDayLogBinding.inflate(inflater, container, false)

        binding.tvComment.text = day.comment
        binding.llComment.setOnClickListener {
            val dialog = EditCommentFragment()
            dialog.arguments = bundleOf("comment" to day.comment)
            dialog.show(parentFragmentManager, "EditCommentFragment")
        }

        binding.tvDate.text = DateManager.formatStringForDisplay(day.date)

        val logSignalAdapter = LogSignalAdapter(day.scores)
        binding.rvDaySignalsLog.adapter = logSignalAdapter
        binding.rvDaySignalsLog.layoutManager = FullWidthLinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)

        val dividerItemDecoration = DividerItemDecoration(binding.root.context, LinearLayoutManager.VERTICAL)
        binding.rvDaySignalsLog.addItemDecoration(dividerItemDecoration)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener("edit_comment_request") { _, bundle ->
            val updated = bundle.getBoolean("isUpdated", false)
            if (updated) {
                day.comment = bundle.getString("comment")
                Db.updateComment(day.id, day.comment)
                binding.tvComment.text = day.comment
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}