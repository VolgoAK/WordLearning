package xyz.volgoak.wordlearning.screens.training

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.AccelerateInterpolator
import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar
import xyz.volgoak.wordlearning.utils.animation.MetallBounceInterpoltor

object AnimatorHelper {

    @JvmStatic
    @BindingAdapter("changeProgress")
    fun changeProgress(progressBar: RoundCornerProgressBar, newProgress: Float) {
        val oldProgress = progressBar.progress
        if (newProgress > oldProgress) {
            val animator = ObjectAnimator.ofFloat(progressBar, "progress",
                    oldProgress, newProgress)
            animator.start()
        }
    }

    @JvmStatic
    @BindingAdapter("changeBackground")
    fun changeBackground(view: View, background: Drawable) {
        val oldBackground = view.background
        if (oldBackground != null) {
            val transition = TransitionDrawable(arrayOf(oldBackground, background))
            view.background = transition
            transition.startTransition(500)
        } else {
            view.background = background
        }
    }

    @JvmStatic
    @BindingAdapter("hideShow")
    fun hideShowNextButton(view: View, show: Boolean) {
        val metrics = view.context.resources.displayMetrics
        val leftPosition = metrics.widthPixels - view.width - 16

        val animator: ValueAnimator
        if (show) {
            animator = ValueAnimator.ofFloat( 1F, 0F)
            animator.interpolator = MetallBounceInterpoltor()
        } else {
            animator = ValueAnimator.ofFloat( 0F, 1F)
            animator.interpolator = AccelerateInterpolator()
        }

        animator.addUpdateListener {
            val animated = it.animatedValue as Float
            view.x = leftPosition + view.width * animated
        }

        animator.duration = 500
        animator.start()
    }
}