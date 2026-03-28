package com.loosethread.moodsignals.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.databinding.ItemNotificationTimeBinding
import com.loosethread.moodsignals.datatypes.NotificationTime

class NotificationTimeAdapter(
    private val notificationTimes: MutableList<NotificationTime>
) : RecyclerView.Adapter<NotificationTimeAdapter.NotificationTimeViewHolder>() {

    inner class NotificationTimeViewHolder(private val notificationTimeBinding: ItemNotificationTimeBinding) : RecyclerView.ViewHolder(notificationTimeBinding.root) {
        fun bind(notificationTime: NotificationTime, position: Int) {
            notificationTimeBinding.tvTitle.setText(notificationTime.title)
            notificationTimeBinding.tvTime.setText(notificationTime.time)
            notificationTimeBinding.tvQuestion.setText(notificationTime.question)

                notificationTimeBinding.ibDelete.setOnClickListener {
                    if (notificationTimes.size > 1) {
                        Db.deleteNotificationTime(notificationTime.id!!)
                        notificationTimes.removeAt(position)
                        notifyItemRemoved(position)
                    }
                }
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
}