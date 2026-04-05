package com.loosethread.moodsignals.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.loosethread.moodsignals.MainActivity
import com.loosethread.moodsignals.R
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.databinding.ItemNotificationTimeBinding
import com.loosethread.moodsignals.datatypes.NotificationTime
import com.loosethread.moodsignals.dialogs.DeleteCategoryDialog
import com.loosethread.moodsignals.dialogs.DeleteNotificationTimeDialog

class NotificationTimeAdapter(
    private val notificationTimes: MutableList<NotificationTime>,
    private val fragmentManager: FragmentManager,
    private val lifecycleOwner: LifecycleOwner
) : RecyclerView.Adapter<NotificationTimeAdapter.NotificationTimeViewHolder>() {

    inner class NotificationTimeViewHolder(private val binding: ItemNotificationTimeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(notificationTime: NotificationTime, position: Int) {
            binding.tvTitle.setText(notificationTime.title)
            binding.tvTime.setText(notificationTime.time)
            binding.tvQuestion.setText(notificationTime.question)

            binding.clNotificationTime.setOnClickListener {
                editNotificationTime(notificationTime.id!!)
            }

            if (notificationTimes.size == 1) {
                binding.ibDelete.visibility = ImageButton.GONE
            } else {
                val requestKeyDelete = "delete_notification_time_${notificationTime.id}"
                binding.ibDelete.setOnClickListener {
                    val dialog = DeleteNotificationTimeDialog()
                    dialog.arguments = bundleOf(
                        "requestKey" to requestKeyDelete,
                        "id" to notificationTime.id
                    )
                    dialog.show(fragmentManager, "DeleteNotificationTimeFragment${notificationTime.id}")
                }

                fragmentManager.setFragmentResultListener(
                    requestKeyDelete,
                    lifecycleOwner
                ) { _, bundle ->
                    val deleted = bundle.getBoolean("isDeleted", false)
                    if (deleted) {
                        val replacement = bundle.getInt("replacement")
                        Db.deleteNotificationTime(notificationTime.id!!, replacement)
                        notificationTimes.removeAt(position)
                        notifyItemRemoved(position)
                    }
                }
            }
        }

        private fun editNotificationTime(id: Int) {
            val bundle = bundleOf("id" to id)
            Navigation.findNavController(
                activity = binding.root.context as MainActivity,
                viewId = R.id.rvNotificationTimes
            ).navigate(R.id.action_fragmentNotificationTimes_to_fragmentAddNotificationTime, bundle)
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