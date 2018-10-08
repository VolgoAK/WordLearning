package xyz.volgoak.wordlearning.adapter.choice_mode

import android.os.Bundle

/**
 * Created by Volgoak on 20.08.2017.
 */

interface ChoiceMode {
    val checkedCount: Int
    val checkedPosition: Int
    val checkedList: List<Int>
    fun setChecked(position: Int, checked: Boolean)
    fun isChecked(position: Int): Boolean
    fun onSaveInstanceState(instanceState: Bundle)
    fun restoreInstanceState(instanceState: Bundle)
    fun clearChecks()
}
