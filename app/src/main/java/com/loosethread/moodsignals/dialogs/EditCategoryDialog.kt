package com.loosethread.moodsignals.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.loosethread.moodsignals.R

class EditCategoryDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;

            val description = arguments?.getString("description")

            val dialogView = inflater.inflate(R.layout.fragment_add_category, null)

            val etDescription = dialogView.findViewById<EditText>(R.id.etDescription)
            etDescription.setText(description)

            builder.setView(dialogView)
                .setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, id ->
                        val requestKey = arguments?.getString("requestKey") ?: "edit_category_request"
                        val resultBundle = Bundle().apply {
                            putBoolean("isUpdated", true)
                            putString("description", etDescription.text.toString())
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