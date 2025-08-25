package com.effatheresoft.androidpractice

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class DatePickerFragment: DialogFragment(), DatePickerDialog.OnDateSetListener {
    private var listener: DatePickerListener? = null

    interface DatePickerListener {
        fun onDatePicked(year: Int, month: Int, dayOfMonth: Int)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as DatePickerListener
    }

    override fun onDetach() {
        super.onDetach()
        if (listener != null) listener = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val datePickerDialog = Calendar.getInstance().run { DatePickerDialog(
            activity as Context,
            this@DatePickerFragment,
            get(Calendar.YEAR),
            get(Calendar.MONTH),
            get(Calendar.DATE)
        ) }
        return datePickerDialog
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        listener?.onDatePicked(year, month, dayOfMonth)
    }
}