package com.effatheresoft.androidpractice

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class TimePickerFragment: DialogFragment(), TimePickerDialog.OnTimeSetListener {
    private var listener: TimePickerListener? = null

    interface TimePickerListener { fun onTimePicked(hourOfDay: Int, minute: Int) }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as TimePickerListener
    }

    override fun onDetach() {
        super.onDetach()
        if (listener != null) listener = null
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val timePickerDialog = Calendar.getInstance().run { TimePickerDialog(
            activity,
            this@TimePickerFragment,
            get(Calendar.HOUR_OF_DAY),
            get(Calendar.MINUTE),
            true
        ) }
        return timePickerDialog
    }

    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {
        listener?.onTimePicked(hourOfDay, minute)
    }

}