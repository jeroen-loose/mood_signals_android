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

            var confirmationText = String.format(getString(R.string.confirm_delete_signal), signal.description)
            val signalHasEntries = Db.signalHasEntries(id)

            if (signalHasEntries) {
                confirmationText += getString(R.string.signal_has_entries)
                confirmationText += getString(R.string.signal_default_action_archive)

                deletePermanently.visibility = CheckBox.VISIBLE
            }


            tvConfirm.setText(confirmationText)

            builder.setView(dialogView)
                .setPositiveButton(getString(R.string.delete),
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
                .setNegativeButton(getString(R.string.cancel),
                    DialogInterface.OnClickListener { dialog, id ->
                        getDialog()?.cancel()
                    })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}