package com.loosethread.moodsignals

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.loosethread.moodsignals.databinding.ItemTodayBinding

class TodayAdapter(
    private val signals: MutableList<Signal>
) : RecyclerView.Adapter<TodayAdapter.TodayViewHolder>() {

    inner class TodayViewHolder(private val todayBinding: ItemTodayBinding) : RecyclerView.ViewHolder(todayBinding.root) {
        fun bind(signal: Signal, position: Int) {
            val dayId = Db.getCurrentDayId()

            val buttons =  listOf(
                todayBinding.btnScoreGreen,
                todayBinding.btnScoreOrange,
                todayBinding.btnScoreRed
            )
            todayBinding.tvSignalName.setText(signal.description)

            for(index in buttons.indices) {
                buttons[index].setText(signal.scores[index].description)
                if (position < signals.size) {
                    buttons[index].setOnClickListener {
                        Db.insertDaySignalValue(dayId, signal.id!!, signal.scores[index].score)
                        val rv = itemView.parent as? RecyclerView
                        rv?.smoothScrollToPosition(position + 1)
                        verifyAllSignalsChecked()
                    }
                } else {
                    buttons[index].setOnClickListener {
                        Db.insertDaySignalValue(dayId, signal.id!!, signal.scores[index].score)
                        verifyAllSignalsChecked()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodayViewHolder {
        val todayBinding = ItemTodayBinding.inflate(LayoutInflater.from(parent.context))
        return TodayViewHolder(todayBinding)
    }

    override fun onBindViewHolder(holder: TodayViewHolder, position: Int) {
        val currentSignal = signals[position]
        holder.bind(currentSignal, position)
    }

    override fun getItemCount(): Int {
        return signals.size
    }

    fun verifyAllSignalsChecked() {

    }
}