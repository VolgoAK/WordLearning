package xyz.volgoak.wordlearning.extensions

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.Toast

fun Context.toast(message: String, lenght: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, lenght).show()
}

fun View.setVisibility(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun sinceLollipop(block: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) block.invoke()
}