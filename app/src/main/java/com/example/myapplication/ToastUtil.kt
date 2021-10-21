package com.example.myapplication

import android.content.Context
import android.view.Gravity
import android.widget.Toast

class ToastUtil {
    private var toast: Toast? = null
    fun showToast(context: Context?, content: String?) {
        if (toast == null) {
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT)
        } else {
            toast!!.setText(content)
        }
        toast!!.show()
    }
}

