package xyz.volgoak.wordlearning.adapter.choice_mode

import android.arch.lifecycle.MutableLiveData
import android.os.Bundle

import java.util.ArrayList

/**
 * Created by Volgoak on 20.08.2017.
 */

class MultiChoiceMode : ChoiceMode {

    private var mSparceBooleanArray = ParcelableSparseBooleanArray()

    override val checkedCount: Int
        get() = mSparceBooleanArray.size()

    val checkedCountLD = MutableLiveData<Int>().apply {
        value = 0
    }

    override val checkedList: List<Int>
        get() {
            val checked = ArrayList<Int>()
            for (a in 0 until mSparceBooleanArray.size()) {
                val x = mSparceBooleanArray.keyAt(a)
                if (mSparceBooleanArray.get(x)) {
                    checked.add(x)
                }
            }
            return checked
        }

    override val checkedPosition: Int
        get() = 0

    override fun setChecked(position: Int, checked: Boolean) {
        if (checked) {
            mSparceBooleanArray.put(position, checked)
        } else {
            mSparceBooleanArray.delete(position)
        }
        checkedCountLD.value = mSparceBooleanArray.size()
    }

    override fun isChecked(position: Int): Boolean {
        return mSparceBooleanArray.get(position)
    }

    override fun onSaveInstanceState(instanceState: Bundle) {
        instanceState.putParcelable(SAVED_STATE, mSparceBooleanArray)
    }

    override fun restoreInstanceState(instanceState: Bundle) {
        mSparceBooleanArray = instanceState.getParcelable(SAVED_STATE)
        checkedCountLD.value = mSparceBooleanArray.size()
    }

    override fun clearChecks() {
        mSparceBooleanArray.clear()
    }

    companion object {
        private const val SAVED_STATE = "saved_state"
    }
}
