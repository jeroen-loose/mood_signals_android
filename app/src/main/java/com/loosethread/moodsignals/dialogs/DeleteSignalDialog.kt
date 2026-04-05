package com.loosethread.moodsignals.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.loosethread.moodsignals.R
import com.loosethread.moodsignals.database.Db

class DeleteSignalDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;

            val id = arguments?.getInt("id")
            val signal = Db.getSignal(id!!)

            val dialogView = inflater.inflate(R.layout.dialog_delete_signal, null)

            val tvConfirm = dialogView.findViewById<TextView>(R.id.tvConfirm)
            val deletePermanently = dialogView.findViewById<CheckBox>(R.id.cbDeletePermanently)

            var confirmationText = "Are you sure you want to delete the signal ${signal.description}?"
            val signalHasEntries = Db.signalHasEntries(id)

            if (signalHasEntries) {
                confirmationText += "\n\nYou already have entries for this signal."
                confirmationText += "\n\nBy default, this signal will be archived to keep the data for previous days.  You can delete it permanently by checking the box below."

                deletePermanently.visibility = CheckBox.VISIBLE
            }


            tvConfirm.setText(confirmationText)

            builder.setView(dialogView)
                .setPositiveButton("Delete",
                    DialogInterface.OnClickListener { dialog, id ->
                        val requestKey = arguments?.getString("requestKey") ?: "delete_signal_request"
                        val resultBundle = Bundle().apply {
                            putBoolean("isDeleted", true)
                        }
                        if (signalHasEntries) {
                            resultBundle.putBoolean("archive", !deletePermanently.isChecked)
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