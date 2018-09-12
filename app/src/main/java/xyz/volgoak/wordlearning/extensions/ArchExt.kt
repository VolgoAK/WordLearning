package xyz.volgoak.wordlearning.extensions

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Observer

fun <T> LiveData<T>.observeSafe(owner: LifecycleOwner, block: (T) -> Unit) {
    this.observe(owner, Observer {
        it?.let { block(it) }
    })
}