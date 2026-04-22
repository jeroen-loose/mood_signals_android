package com.loosethread.moodsignals.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.loosethread.moodsignals.FullWidthLinearLayoutManager
import com.loosethread.moodsignals.R
import com.loosethread.moodsignals.adapters.CommentSearchAdapter
import com.loosethread.moodsignals.adapters.LogCategoriesAdapter
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.databinding.ItemDayLogBinding
import com.loosethread.moodsignals.datatypes.Day
import com.loosethread.moodsignals.datatypes.LogCategory
import com.loosethread.moodsignals.dialogs.EditCommentDialog
import com.loosethread.moodsignals.helpers.DateHelper

class FragmentSingleDay(): Fragment() {
    private val dayId: Int by lazy { requireArguments().getInt("dayId") }
    private lateinit var day: Day
    private lateinit var categories: MutableList<LogCategory>
    private var _binding: ItemDayLogBinding? = null
    private val binding get() = _binding!!
    var onDaySelected: ((dayId: Int) -> Unit) ?= null
    var onCommentSearchToggle: ((searchVisible: Boolean) -> Unit) ?= null
    var searchActive: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        day = Db.getDay(dayId)
        categories = Db.getDayCategories(dayId)

        _binding = ItemDayLogBinding.inflate(inflater, container, false)

        val logCategoriesAdapter = LogCategoriesAdapter(dayId, categories)
        binding.rvCategories.adapter = logCategoriesAdapter
        binding.rvCategories.layoutManager =
            FullWidthLinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)

        val dividerItemDecoration = DividerItemDecoration(binding.root.context, LinearLayoutManager.VERTICAL)
        dividerItemDecoration.setDrawable(binding.root.context.getDrawable(R.drawable.blankline)!!)
        binding.rvCategories.addItemDecoration(dividerItemDecoration)

        val commentAdapter = CommentSearchAdapter(Db.getComments())
        commentAdapter.onDaySelected = { id: Int ->
            onDaySelected?.invoke(id)
            toggleCommentSearch()
        }
        binding.rvCommentSearch.adapter = commentAdapter
        binding.rvCommentSearch.layoutManager =
            FullWidthLinearLayoutManager(binding.root.context, LinearLayoutManager.VERTICAL, false)
        binding.rvCommentSearch.addItemDecoration(dividerItemDecoration)

        binding.etCommentSearch.doOnTextChanged { text, _, _, _ ->
            val searchText = text.toString()
            val comments = Db.getComments(searchText)
            commentAdapter.updateComments(comments)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvComment.text = day.comment
        binding.tvDate.text = DateHelper.formatForDisplay(day.date)

        binding.llComment.setOnClickListener {
            toggleCommentSearch()
        }
    }

    override fun onResume() {
        super.onResume()
        day = Db.getDay(dayId)
        categories = Db.getDayCategories(dayId)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun showEditCommentDialog() {
        val requestKey = "edit_comment_request_$dayId"
        val dialog = EditCommentDialog()

        dialog.arguments = bundleOf(
            "comment" to day.comment,
            "requestKey" to requestKey
        )
        dialog.show(parentFragmentManager, "EditCommentFragment")

        setFragmentResultListener(requestKey) { _, bundle ->
            val updated = bundle.getBoolean("isUpdated", false)
            if (updated) {
                day.comment = bundle.getString("comment")
                Db.updateComment(dayId, day.comment)
                binding.tvComment.text = day.comment
            }
        }
    }

    fun toggleCommentSearch() {
        if (searchActive) {
            binding.rvCommentSearch.visibility = View.GONE
            binding.tvComment.visibility = View.VISIBLE
            binding.etCommentSearch.visibility = View.GONE
            onCommentSearchToggle?.invoke(false)
        } else {
            binding.rvCommentSearch.visibility = View.VISIBLE
            binding.tvComment.visibility = View.GONE
            binding.etCommentSearch.visibility = View.VISIBLE
            binding.etCommentSearch.requestFocus()
            onCommentSearchToggle?.invoke(true)
        }
        searchActive = !searchActive
    }

    fun disableCommentSearch() {
        if (searchActive) {
            toggleCommentSearch()
        }
    }
}