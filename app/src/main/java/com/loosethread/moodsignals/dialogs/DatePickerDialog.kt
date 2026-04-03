package com.loosethread.moodsignals.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.loosethread.moodsignals.helpers.DateManager

class DatePickerDialog : DialogFragment(), DatePickerDialog.OnDateSetListener {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val result = DatePickerDialog(
            requireContext(),
            this,
            DateManager.getYear(),
            DateManager.getMonth(),
            DateManager.getDay()
        )
        result.getDatePicker().setMaxDate(System.currentTimeMillis())

        return result

    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        DateManager.setDate(year, month, day)

        setFragmentResult("requestKey", bundleOf())
    }
}