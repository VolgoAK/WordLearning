package xyz.volgoak.wordlearning.adapter.choice_mode

import android.os.Bundle

/**
 * Created by Volgoak on 20.08.2017.
 */

class SingleChoiceMode : ChoiceMode {

    override var checkedPosition = -1

    override val checkedCount: Int
        get() = if (checkedPosition == -1) 0 else 1

    override val checkedList: List<Int> = listOf()

    override fun setChecked(position: Int, checked: Boolean) {
        if (checked) {
            checkedPosition = position
        }
    }

    override fun isChecked(position: Int): Boolean {
        return position == checkedPosition
    }

    override fun onSaveInstanceState(instanceState: Bundle) {}

    override fun restoreInstanceState(instanceState: Bundle) {}

    override fun clearChecks() {
        checkedPosition = -1
    }
}
