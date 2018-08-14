package xyz.volgoak.wordlearning.adapter


import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.attiladroid.data.entities.DataEntity
import timber.log.Timber
import xyz.volgoak.wordlearning.recycler.ChoiceMode
import xyz.volgoak.wordlearning.recycler.MultiChoiceMode
import xyz.volgoak.wordlearning.recycler.SingleChoiceMode

/**
 * Created by alex on 1/3/18.
 */

abstract class RecyclerAdapter<RC : BaseHolder, DE : DataEntity>
(var entities: MutableList<DE>, private val mRecyclerView: RecyclerView) : RecyclerView.Adapter<RC>() {

    var onClick: (DE) -> Unit = {}
    var onLongClick: (DE) -> Unit = {}

    private var isSelectable: Boolean = false
    private var choiceMode: ChoiceMode? = null


    override fun onBindViewHolder(controller: RC, position: Int) {
        controller.setSelectable(isSelectable)
        if (choiceMode != null) {
            controller.setChecked(choiceMode!!.isChecked(position))
        }
        controller.bindController(entities[position])
    }

    override fun getItemCount(): Int {
        return entities.size
    }

    override fun getItemId(position: Int): Long {
        return entities[position].id
    }

    private fun setSelectable(selectable: Boolean) {
        if (isSelectable != selectable) {
            isSelectable = selectable
            for (a in 0 until itemCount) {
                val controller = mRecyclerView.findViewHolderForAdapterPosition(a) as BaseHolder?

                if (controller != null) {
                    controller.setSelectable(selectable)
                    if (selectable && choiceMode != null) {
                        controller.setChecked(choiceMode!!.isChecked(controller.adapterPosition))
                    }
                }
            }

            //dirty trick
            val llm = mRecyclerView.layoutManager as LinearLayoutManager
            val firstVisible = llm.findFirstVisibleItemPosition()
            val lastVisible = llm.findLastVisibleItemPosition()

            for (a in 1..3) {
                notifyItemChanged(firstVisible - a)
                notifyItemChanged(lastVisible + a)
            }
        }
    }

    fun setChoiceMode(choiceMode: ChoiceMode) {
        this.choiceMode = choiceMode
        if (choiceMode is MultiChoiceMode) {
            setSelectable(true)
        } else
            setSelectable(false)
    }

    fun changeData(entities: MutableList<DE>) {
        val diffResult = DiffUtil.calculateDiff(createDiffCallback(this.entities, entities))
        diffResult.dispatchUpdatesTo(this)
        this.entities = entities
    }

    open fun createDiffCallback(oldList: List<DE>, newList: List<DE>): DiffUtil.Callback =
            BaseDiffCallback(oldList, newList)

    open fun onControllerClick(controller: BaseHolder, root: View, position: Int) {
        if (choiceMode != null) {
            if (choiceMode is SingleChoiceMode) {
                if (choiceMode!!.checkedPosition != -1) {
                    val rc = mRecyclerView.findViewHolderForAdapterPosition(choiceMode!!.checkedPosition) as BaseHolder
                    rc.setChecked(false)
                }
                controller.setChecked(true)
                choiceMode!!.setChecked(position, true)
            } else if (choiceMode is MultiChoiceMode) {
                //change checked state
                val checked = !choiceMode!!.isChecked(position)
                controller.setChecked(checked)
                choiceMode!!.setChecked(position, checked)
            }
        }

        onClick(entities[position])
    }

    open inner class BaseDiffCallback(val oldList: List<DE>, val newList: List<DE>) : DiffUtil.Callback() {
        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            Timber.d("old ${oldList[oldItemPosition]} new ${newList[newItemPosition]}")
            return oldList[oldItemPosition] == newList[newItemPosition]
        }


        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            Timber.d("GET CHANGE PAYLOAD old p $oldItemPosition")
            return super.getChangePayload(oldItemPosition, newItemPosition)
        }
    }
}
