package xyz.volgoak.wordlearning.utils.transitions

import android.support.transition.ChangeBounds
import android.support.transition.ChangeImageTransform
import android.support.transition.ChangeTransform
import android.support.transition.TransitionSet

class DetailsSupportTransition: TransitionSet() {
    init {
        ordering = ORDERING_TOGETHER
        addTransition(ChangeBounds()).
                addTransition(ChangeTransform()).
                addTransition(ChangeImageTransform())
    }
}