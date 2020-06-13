package com.dcrtns.adnotebook.util

import android.app.Activity
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView

object EditTextUtil {

    fun setupEditText(context: Context, editText: EditText, textView: TextView, charCount: Int) {
        editText.setHorizontallyScrolling(false)
        editText.maxLines = Integer.MAX_VALUE

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                textView.text = (charCount - s.length).toString()
                (context as Activity).invalidateOptionsMenu()
            }

            override fun afterTextChanged(s: Editable) {}
        }
        editText.addTextChangedListener(textWatcher)
    }

    fun hasText(editText: EditText): Boolean {
        return editText.text.toString().isNotEmpty()
    }

    fun hasNoText(editText: EditText): Boolean {
        return editText.text.toString().isEmpty()
    }

}