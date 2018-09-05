package xyz.volgoak.wordlearning.utils.transitions

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.transition.Transition
import android.transition.TransitionValues
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import xyz.volgoak.wordlearning.utils.round_bitmap.MyRoundBitmap

@SuppressLint("NewApi")
class RoundTransition : Transition() {

    companion object {
        const val SIZE = "size"
    }

    override fun captureStartValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    override fun captureEndValues(transitionValues: TransitionValues) {
        captureValues(transitionValues)
    }

    private fun captureValues(transitionValues: TransitionValues) {
        val view = transitionValues.view
        if (view is ImageView) {
            transitionValues.values[SIZE] = view.width
        }
    }

    override fun createAnimator(sceneRoot: ViewGroup, startValues: TransitionValues?, endValues: TransitionValues?): Animator? {
        if (startValues == null || endValues == null) return null
        val view = endValues.view
        if (view is ImageView && view.drawable is MyRoundBitmap) {
            val drawable = view.drawable as MyRoundBitmap

            val startSize = startValues.values[SIZE] as Int? ?: 0
            val endSize = endValues.values[SIZE] as Int? ?: 0
            val first: Float
            val second: Float
            if (startSize > endSize) {
                first = 0F
                second = 1F
            } else {
                second = 0F
                first = 1F
            }

            val startCorners = (drawable.bitmap?.height ?: 0) / 2F
            val animator = ValueAnimator.ofFloat(first, second)
            animator.addUpdateListener { animation ->
                val animated = animation.animatedValue as Float
                drawable.cornerRadius = startCorners * animated
            }

            return animator
        }
        return null
    }
}