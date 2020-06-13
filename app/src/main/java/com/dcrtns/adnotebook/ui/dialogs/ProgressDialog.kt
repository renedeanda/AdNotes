package com.dcrtns.adnotebook.ui.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.dcrtns.adnotebook.R
import kotlinx.android.synthetic.main.progress_dialog.view.*

class ProgressDialog {

    companion object {
        @SuppressLint("InflateParams")
        fun progressDialog(context: Context, message: String?): Dialog {
            val dialog = Dialog(context)
            val view = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null)
            dialog.setContentView(view)
            dialog.setCancelable(false)
            dialog.window!!.setBackgroundDrawable(
                    ColorDrawable(Color.TRANSPARENT))
            view.progress_textview.text = message
            return dialog
        }
    }


}