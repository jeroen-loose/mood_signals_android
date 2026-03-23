package com.loosethread.moodsignals

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.loosethread.moodsignals.databinding.ItemDaySignalsLogBinding

class LogSignalAdapter(
    private val signal_scores: MutableList<DaySignalValue>
) : RecyclerView.Adapter<LogSignalAdapter.LogSignalViewHolder>() {

    inner class LogSignalViewHolder(private val logSignalsBinding: ItemDaySignalsLogBinding) : RecyclerView.ViewHolder(logSignalsBinding.root) {
        fun bind(signal: DaySignalValue, position: Int) {

            logSignalsBinding.clDaySignalLog.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(logSignalsBinding.root.context, ColorPicker.get(signal.score)))

            logSignalsBinding.tvSignalName.text = signal.signalDescription
            logSignalsBinding.tvSignalScore.text = signal.scoreDescription
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogSignalViewHolder {
        val logSignalBinding = ItemDaySignalsLogBinding.inflate(LayoutInflater.from(parent.context))
        return LogSignalViewHolder(logSignalBinding)
    }

    override fun onBindViewHolder(holder: LogSignalViewHolder, position: Int) {
        val currentSignal = signal_scores[position]
        if (currentSignal != null)
            holder.bind(currentSignal, position)
    }

    override fun getItemCount(): Int {
        return signal_scores.size
    }
}