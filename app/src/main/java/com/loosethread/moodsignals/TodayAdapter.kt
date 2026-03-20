package com.loosethread.moodsignals

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.loosethread.moodsignals.databinding.FragmentTodayBinding
import com.loosethread.moodsignals.databinding.ItemTodayBinding

class TodayAdapter(
    private var signals: MutableList<Signal>,
    private var selectedDate: String,
    private val onAllSignalsChecked: () -> Unit
) : RecyclerView.Adapter<TodayAdapter.TodayViewHolder>() {
    var dayId = Db.getDayId(selectedDate)
    private var daySignalValues = mutableListOf<DaySignalValue>()

    inner class TodayViewHolder(private val todayBinding: ItemTodayBinding) : RecyclerView.ViewHolder(todayBinding.root) {
        fun bind(signal: Signal, position: Int) {
            dayId = Db.getDayId(selectedDate)
            daySignalValues = Db.getDaySignalValues(dayId)

            val buttons =  listOf(
                todayBinding.btnScoreGreen,
                todayBinding.btnScoreOrange,
                todayBinding.btnScoreRed
            )
            todayBinding.tvSignalName.setText(signal.description)

            for(index in buttons.indices) {
                buttons[index].setText(signal.scores[index].description)
                if (daySignalValues.any { it.signalId == signal.id && it.score == signal.scores[index].score }) {
                    buttons[index].setTextColor(Color.BLACK)
                }

                if (position < signals.size - 1) {
                    buttons[index].setOnClickListener {
                        Db.insertDaySignalValue(dayId, signal.id!!, signal.scores[index].score)
                        val rv = itemView.parent as? RecyclerView
                        rv?.smoothScrollToPosition(position + 1)
                    }
                } else {
                    buttons[index].setOnClickListener {
                        Db.insertDaySignalValue(dayId, signal.id!!, signal.scores[index].score)
                        onAllSignalsChecked()
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

    fun setDate(date: String) {
        if (Db.dayIsEmpty(dayId)) {
            Db.removeDay(dayId)
        }
        selectedDate = date
        dayId = Db.getDayId(selectedDate)
        daySignalValues = Db.getDaySignalValues(dayId)
        notifyDataSetChanged()
    }
}