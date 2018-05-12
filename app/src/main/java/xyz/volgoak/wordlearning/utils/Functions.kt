package xyz.volgoak.wordlearning.utils

import android.content.Context
import android.util.TypedValue

/**
 * Created by alex on 5/10/18.
 */
object Functions {

    fun dpToPx(context: Context, dp : Float) : Float {
        val r = context.getResources()
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics())
    }
}