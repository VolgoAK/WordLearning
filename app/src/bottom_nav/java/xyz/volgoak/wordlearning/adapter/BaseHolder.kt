package xyz.volgoak.wordlearning.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import xyz.volgoak.wordlearning.entities.DataEntity

/**
 * Created by alex on 1/3/18.
 */

abstract class BaseHolder(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bindController(dataEntity: DataEntity)

    abstract fun setChecked(checked: Boolean)

    open fun setSelectable(selectable: Boolean) {
        //do nothing
    }
}
