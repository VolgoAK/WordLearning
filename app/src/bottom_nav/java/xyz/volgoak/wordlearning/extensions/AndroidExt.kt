package xyz.volgoak.wordlearning.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.ViewCompat
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast

fun Context.toast(message: String, lenght: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, lenght).show()
}

fun View.setVisibility(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.onDrawn(delay: Long = 0, block: () -> Unit) {
    val view = this
    this.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            view.viewTreeObserver.removeOnGlobalLayoutListener(this)
            Handler().postDelayed({
                block.invoke()
            }, delay)
        }
    })
}

fun View.onPreDraw(block: () -> Unit) {
    val view = this
    view.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
        override fun onPreDraw(): Boolean {
            view.viewTreeObserver.removeOnPreDrawListener(this)
            block.invoke()
            return true
        }
    })
}

fun sinceLollipop(block: () -> Unit) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) block.invoke()
}

fun FragmentTransaction.addSharedElement(view: View) {
    addSharedElement(view, ViewCompat.getTransitionName(view))
}