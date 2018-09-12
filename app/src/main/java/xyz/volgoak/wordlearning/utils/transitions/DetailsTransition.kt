package xyz.volgoak.wordlearning.utils.transitions

import android.support.annotation.RequiresApi
import android.transition.ChangeBounds
import android.transition.ChangeTransform
import android.transition.TransitionSet

@RequiresApi(21)
class DetailsTransition : TransitionSet() {
    init {
        ordering = ORDERING_TOGETHER
        addTransition(ChangeBounds()).
                addTransition(ChangeTransform()).
                addTransition(RoundTransition())
    }
}