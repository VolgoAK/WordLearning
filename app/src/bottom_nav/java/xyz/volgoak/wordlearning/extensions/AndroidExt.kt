package xyz.volgoak.wordlearning.extensions

import android.content.Context
import android.widget.Toast

fun Context.toast(message: String, lenght: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, lenght).show()
}