package com.effatheresoft.androidpractice.util

import android.content.Context
import android.widget.Toast

object Utils {
    fun showErrorToast(context: Context, errorCode: String) {
        Toast.makeText(
            context,
            "Action Failed. Error Code: $errorCode",
            Toast.LENGTH_SHORT)
            .show()
    }
}