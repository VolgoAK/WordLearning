package xyz.volgoak.wordlearning.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.attiladroid.data.DataContract
import com.attiladroid.data.entities.DataEntity
import com.attiladroid.data.entities.Word
import timber.log.Timber
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.utils.WordSpeaker

/**
 * Created by Volgoak on 18.08.2017.
 */

class WordsRecyclerAdapter(context: Context, wordList: MutableList<Word>, rv: RecyclerView) :
        RecyclerAdapter<WordsRecyclerAdapter.WordsRowController, Word>(wordList, rv) {

    private val progressIcons by lazy {
        listOf(
                ContextCompat.getDrawable(context, R.drawable.ic_progress_0),
                ContextCompat.getDrawable(context, R.drawable.ic_progress_1),
                ContextCompat.getDrawable(context, R.drawable.ic_progress_2),
                ContextCompat.getDrawable(context, R.drawable.ic_progress_3),
                ContextCompat.getDrawable(context, R.drawable.ic_progress_4)
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordsRowController {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.redactor_cursor_adapter, parent, false)
        return WordsRowController(view)
    }

    inner class WordsRowController(rootView: View) : BaseHolder(rootView) {

        private var isSelectable: Boolean = false

        private val wordText = rootView.findViewById<TextView>(R.id.tv_word_adapter)
        private val translationText = rootView.findViewById<TextView>(R.id.tv_translation_adapter)
        private val trainWTImage = rootView.findViewById<ImageView>(R.id.iv_progress_wt_adapter)
        private val trainTWImage = rootView.findViewById<ImageView>(R.id.iv_progress_tw_adapter)
        private val inDictionaryImage = rootView.findViewById<ImageView>(R.id.iv_status_word_controller)
        private val soundButton = rootView.findViewById<ImageButton>(R.id.adapter_button)
        private val checkBox = rootView.findViewById<CheckBox>(R.id.cb_selected_dict_adapter)

        init {
            rootView.setOnClickListener { v ->
                onControllerClick(this@WordsRowController, v, adapterPosition, listOf(v, wordText))
            }
            rootView.setOnLongClickListener {
                onLongClick(entities[adapterPosition], adapterPosition)
                true
            }
        }

        override fun bindController(dataEntity: DataEntity) {
            val word = dataEntity as Word
            ViewCompat.setTransitionName(itemView, word.word)
            ViewCompat.setTransitionName(wordText, word.word + "text")
            wordText.text = word.word
            translationText.text = word.translation

            try {
                trainTWImage.setImageDrawable(progressIcons[word.trainedTw])
            } catch (ex: IndexOutOfBoundsException) {
                Timber.e(ex)
                trainTWImage.setImageResource(R.drawable.ic_progress_4)
            }

            try {
                trainWTImage.setImageDrawable(progressIcons[word.trainedWt])
            } catch (ex: IndexOutOfBoundsException) {
                Timber.e(ex)
                trainWTImage.setImageResource(R.drawable.ic_progress_4)
            }

            val status = word.status
            inDictionaryImage.visibility = if (status == DataContract.Words.IN_DICTIONARY)
                View.VISIBLE
            else
                View.INVISIBLE

            soundButton.setOnClickListener { v -> WordSpeaker.speakWord(word.word) }
        }

        override fun setChecked(checked: Boolean) {
            if (isSelectable) checkBox.isChecked = checked
        }

        override fun setSelectable(selectable: Boolean) {
            isSelectable = selectable
            checkBox.visibility = if (selectable) View.VISIBLE else View.GONE
        }
    }
}
