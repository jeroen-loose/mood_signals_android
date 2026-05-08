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
import com.loosethread.moodsignals.databinding.DialogUpdateSignalBinding

class UpdateSignalDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;

            val dialogView = inflater.inflate(R.layout.dialog_update_signal, null)
            val binding = DialogUpdateSignalBinding.bind(dialogView)

            val updatePermanently = binding.cbUpdateSignal

            builder.setView(dialogView)
                .setPositiveButton("Save",
                    DialogInterface.OnClickListener { dialog, id ->
                        val requestKey = arguments?.getString("requestKey") ?: "update_signal_request"
                        val resultBundle = Bundle().apply {
                            putBoolean("isUpdated", true)
                            putBoolean("isUpdatedPermanently", updatePermanently.isChecked)
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