package com.loosethread.moodsignals.adapters

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.loosethread.moodsignals.databinding.ItemDaySignalsLogBinding
import com.loosethread.moodsignals.datatypes.DaySignalValue
import com.loosethread.moodsignals.helpers.ColorPicker

class LogSignalAdapter(
    private val signal_scores: MutableList<DaySignalValue>
) : RecyclerView.Adapter<LogSignalAdapter.LogSignalViewHolder>() {

    inner class LogSignalViewHolder(private val binding: ItemDaySignalsLogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(signal: DaySignalValue, position: Int) {

            binding.clDaySignalLog.backgroundTintList = ColorStateList.valueOf(
                ContextCompat.getColor(binding.root.context, ColorPicker.get(signal.score)))
            binding.tvSignalName.text = signal.signalDescription
            binding.tvSignalScore.text = signal.scoreDescription
            activate()
        }

        fun activate() {
            binding.tvSignalName.isSelected = true
            binding.tvSignalScore.isSelected = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogSignalViewHolder {
        val logSignalBinding = ItemDaySignalsLogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LogSignalViewHolder(logSignalBinding)
    }

    override fun onBindViewHolder(holder: LogSignalViewHolder, position: Int) {
        val currentSignal = signal_scores[position]
        if (currentSignal != null) {
            holder.bind(currentSignal, position)
        }
    }

    override fun getItemCount(): Int {
        return signal_scores.size
    }

}