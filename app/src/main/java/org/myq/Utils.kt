package org.myq

import android.content.Context
import android.widget.Toast

fun makeToast(msg: String, context: Context) {
    val toast = Toast.makeText(context, msg, Toast.LENGTH_LONG)
    toast.show()
}