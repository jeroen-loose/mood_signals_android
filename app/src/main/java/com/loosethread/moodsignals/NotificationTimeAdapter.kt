package com.loosethread.moodsignals

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.loosethread.moodsignals.databinding.ItemNotificationTimeBinding

class NotificationTimeAdapter(
    private val notificationTimes: MutableList<NotificationTime>
) : RecyclerView.Adapter<NotificationTimeAdapter.NotificationTimeViewHolder>() {

    inner class NotificationTimeViewHolder(private val notificationTimeBinding: ItemNotificationTimeBinding) : RecyclerView.ViewHolder(notificationTimeBinding.root) {
        fun bind(notificationTime: NotificationTime, position: Int) {
            notificationTimeBinding.etQuestion.setText(notificationTime.question)
            notificationTimeBinding.tpTime.setIs24HourView(true)
            notificationTimeBinding.tpTime.hour = notificationTime.time?.substring(0, 2)!!.toInt()
            notificationTimeBinding.tpTime.minute = notificationTime.time?.substring(3, 5)!!.toInt()

            notificationTimeBinding.ibDelete.setOnClickListener {
                deleteNotificationTime(notificationTime.id!!)
            }
        }

        private fun deleteNotificationTime(id: Int) {
            Db.deleteNotificationTime(id)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationTimeViewHolder {
        val notificationTimeBinding = ItemNotificationTimeBinding.inflate(LayoutInflater.from(parent.context))
        return NotificationTimeViewHolder(notificationTimeBinding)
    }

    override fun onBindViewHolder(holder: NotificationTimeViewHolder, position: Int) {
        val currentNotificationTime = notificationTimes[position]
        if (currentNotificationTime != null)
            holder.bind(currentNotificationTime, position)
    }

    override fun getItemCount(): Int {
        return notificationTimes.size
    }

    fun submitList(notificationTimes: MutableList<NotificationTime>) {
        notifyDataSetChanged()
    }



}