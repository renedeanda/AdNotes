package com.dcrtns.adnotebook.util

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.dcrtns.adnotebook.R

object DialogUtil {
    fun cancelAdDialog(context: Context, title: Int, message: Int) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(R.string.ok) { _, _ -> (context as Activity).finish() }
            setNegativeButton(R.string.cancel, null)
        }.show()
    }

    fun showInfoDialog(context: Context, title: String, message: String) {
        AlertDialog.Builder(context).apply {
            setTitle(title)
            setMessage(message)
            setPositiveButton(R.string.ok) { dialog, _ -> dialog.dismiss() }
        }.show()
    }
}