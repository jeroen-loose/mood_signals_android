package com.loosethread.moodsignals.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.loosethread.moodsignals.R
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.datatypes.NotificationTime
import com.loosethread.moodsignals.datatypes.SignalCategory

class DeleteNotificationTimeDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;

            val id = arguments?.getInt("id")
            val notificationTime = Db.getNotificationTime(id!!)

            val dialogView = inflater.inflate(R.layout.dialog_delete_notification_time, null)

            val tvConfirm = dialogView.findViewById<TextView>(R.id.tvConfirm)
            val spinner = dialogView.findViewById<Spinner>(R.id.spReplacementNotificationTime)

            var confirmationText = "Are you sure you want to delete the nontification \"${notificationTime.title}\"?"
            val notificationTimeHasSignals = Db.notificationTimeHasSignals(id)

            if (notificationTimeHasSignals) {
                confirmationText += "\n\nThis notification has signals associated with it."
                confirmationText += "\n\nPlease select a replacement notification to assign to these signals."

                spinner.visibility = Spinner.VISIBLE
                val notificationTimes = Db.getNotificationTimes(id)
                val adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, notificationTimes)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }


            tvConfirm.setText(confirmationText)

            builder.setView(dialogView)
                .setPositiveButton("Delete",
                    DialogInterface.OnClickListener { dialog, id ->
                        val requestKey = arguments?.getString("requestKey") ?: "edit_notification_time_request"
                        val resultBundle = Bundle().apply {
                            putBoolean("isDeleted", true)
                        }
                        if (notificationTimeHasSignals) {
                            resultBundle.putInt("replacement", (spinner.selectedItem as NotificationTime).id!!)
                        }
                        setFragmentResult(requestKey, resultBundle)
                        dismiss()
                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->
                        getDialog()?.cancel()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}