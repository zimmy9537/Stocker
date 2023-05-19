package com.zimmy.best.stocker.utility

import android.content.Context
import android.widget.Toast

fun Toast.toast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}