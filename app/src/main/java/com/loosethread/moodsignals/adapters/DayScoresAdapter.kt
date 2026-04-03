package com.loosethread.moodsignals.adapters

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.loosethread.moodsignals.databinding.ItemChartBinding
import com.loosethread.moodsignals.datatypes.LogDay
import com.loosethread.moodsignals.views.Chart

class DayScoresAdapter(
    private val days: MutableList<LogDay>
) : RecyclerView.Adapter<DayScoresAdapter.DayScoresViewHolder>() {

    inner class DayScoresViewHolder(private val binding: ItemChartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(day: LogDay, position: Int) {
            with (binding.tvDayDescription) {
                //text = day.description
                val scores = intArrayOf(
                    day.score_count.getOrDefault(1, 0),
                    day.score_count.getOrDefault(2, 0),
                    day.score_count.getOrDefault(3, 0)
                )
                val chart = Chart(binding.root.context, scores)
                chart.setOrientation(GradientDrawable.Orientation.BOTTOM_TOP)
                background = chart
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayScoresViewHolder {
        val binding = ItemChartBinding.inflate(LayoutInflater.from(parent.context))
        return DayScoresViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DayScoresViewHolder, position: Int) {
        val currentDay = days[position]
        if (currentDay != null)
            holder.bind(currentDay, position)
    }

    override fun getItemCount(): Int {
        return days.size
    }

}