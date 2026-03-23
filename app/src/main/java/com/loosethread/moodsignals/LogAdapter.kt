package com.loosethread.moodsignals

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loosethread.moodsignals.databinding.ItemDayLogBinding
import com.loosethread.moodsignals.databinding.ItemSignalBinding
import com.loosethread.moodsignals.databinding.ItemTodayBinding

class LogAdapter(
    val days: MutableList<Day>
) : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

    inner class LogViewHolder(private val logBinding: ItemDayLogBinding) : RecyclerView.ViewHolder(logBinding.root) {
        fun bind(day: Day, position: Int) {
            val logSignalAdapter = LogSignalAdapter(day.scores)
            logBinding.rvDaySignalsLog.adapter = logSignalAdapter
            logBinding.rvDaySignalsLog.layoutManager = FullWidthLinearLayoutManager(logBinding.root.context, LinearLayoutManager.VERTICAL, false)

            val dividerItemDecoration = DividerItemDecoration(logBinding.root.context, LinearLayoutManager.VERTICAL)
            logBinding.rvDaySignalsLog.addItemDecoration(dividerItemDecoration)

            logBinding.tvComment.text = day.comment
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val logBinding = ItemDayLogBinding.inflate(LayoutInflater.from(parent.context))
        return LogViewHolder(logBinding)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        val currentDay = days[position]
        if (currentDay != null)
            holder.bind(currentDay, position)
    }

    override fun getItemCount(): Int {
        return days.size
    }
}