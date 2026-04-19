package com.loosethread.moodsignals.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.loosethread.moodsignals.R
import com.loosethread.moodsignals.database.Db
import com.loosethread.moodsignals.datatypes.SignalCategory

class DeleteCategoryDialog : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater;

            val id = arguments?.getInt("id")
            val category = Db.getCategory(id!!)

            val dialogView = inflater.inflate(R.layout.dialog_delete_category, null)

            val tvConfirm = dialogView.findViewById<TextView>(R.id.tvConfirm)
            val spinner = dialogView.findViewById<Spinner>(R.id.spReplacementCategory)

            var confirmationText = "Are you sure you want to delete the category ${category.description}?"
            val categoryHasSignals = Db.categoryHasSignals(id)

            if (categoryHasSignals) {
                confirmationText += "\n\nThis category has signals associated with it."
                confirmationText += "\n\nPlease select a replacement category to assign to these signals."

                spinner.visibility = Spinner.VISIBLE
                val categories = Db.getCategories(id)
                val adapter =
                    ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinner.adapter = adapter
            }


            tvConfirm.setText(confirmationText)

            builder.setView(dialogView)
                .setPositiveButton("Delete",
                    DialogInterface.OnClickListener { dialog, id ->
                        val requestKey = arguments?.getString("requestKey") ?: "edit_category_request"
                        val resultBundle = Bundle().apply {
                            putBoolean("isDeleted", true)
                        }
                        if (categoryHasSignals) {
                            resultBundle.putInt("replacement", (spinner.selectedItem as SignalCategory).id!!)
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