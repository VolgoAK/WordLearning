package xyz.volgoak.wordlearning.screens.set.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.attiladroid.data.entities.DataEntity
import com.attiladroid.data.entities.Word
import timber.log.Timber
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.databinding.WordCardHolderBinding
import xyz.volgoak.wordlearning.extensions.setVisibility
import xyz.volgoak.wordlearning.utils.WordSpeaker


/**
 * Created by alex on 1/26/18.
 */

class CardsRecyclerAdapter : RecyclerView.Adapter<CardsRecyclerAdapter.WordCardRowController>() {

    private val dataList = mutableListOf<Word>()
    var progressResetListener: ((Word) -> Unit) = {}
    var removeListener: ((Word) -> Unit) = {}

    companion object {

        private val sProgresIcons = arrayOfNulls<Drawable>(5)
        private var isDrawableInit: Boolean = false

        fun initDrawables(context: Context) {
            sProgresIcons[0] = ContextCompat.getDrawable(context, R.drawable.ic_progress_0)
            sProgresIcons[1] = ContextCompat.getDrawable(context, R.drawable.ic_progress_1)
            sProgresIcons[2] = ContextCompat.getDrawable(context, R.drawable.ic_progress_2)
            sProgresIcons[3] = ContextCompat.getDrawable(context, R.drawable.ic_progress_3)
            sProgresIcons[4] = ContextCompat.getDrawable(context, R.drawable.ic_progress_4)
            isDrawableInit = true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordCardRowController {
        val binding = WordCardHolderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WordCardRowController(binding)
    }

    override fun onBindViewHolder(holder: WordCardRowController, position: Int) {
        try {
            holder.bindController(dataList[position % dataList.size])
        } catch (ex: ArithmeticException) {
            Timber.e(ex)
        }
    }

    override fun getItemCount() = if (dataList.size == 0) 0 else Int.MAX_VALUE

    fun setDataList(dataList: List<Word>) {
        this.dataList.clear()
        this.dataList.addAll(dataList)
        notifyDataSetChanged()
    }

    inner class WordCardRowController(private val binding: WordCardHolderBinding)
        : RecyclerView.ViewHolder(binding.root) {

        private var settingsVisible: Boolean = false

        init {
            if (!isDrawableInit) initDrawables(binding.root.context)
        }

        fun bindController(dataEntity: DataEntity) {
            settingsVisible = false
            val word = dataEntity as Word

            ViewCompat.setTransitionName(binding.cardWord, word.word)
            ViewCompat.setTransitionName(binding.tvCardWord, word.word + "text")

            binding.tvCardTranslation.text = word.translation
            binding.tvCardTranscription.text = word.transcription
            binding.tvCardWord.text = word.word

            binding.tvCardTranslation.visibility = View.INVISIBLE

            if (word.isInDictionary) {
                binding.ivAddRemove.setImageResource(R.drawable.ic_remove)
                binding.tvWordInDictionary.setText(R.string.remove)
                binding.tvInDictionaryLabal.visibility = View.VISIBLE
                binding.ivInDictionary.visibility = View.VISIBLE
            } else {
                binding.ivAddRemove.setImageResource(R.drawable.ic_add_blue_50dp)
                binding.tvWordInDictionary.setText(R.string.add)
                binding.tvInDictionaryLabal.visibility = View.GONE
                binding.ivInDictionary.visibility = View.GONE
            }

            val wtProgress = if (word.trainedWt >= sProgresIcons.size)
                sProgresIcons[sProgresIcons.size - 1]
            else
                sProgresIcons[word.trainedWt]
            binding.ivProgressWt.setImageDrawable(wtProgress)

            val twProgress = if (word.trainedTw >= sProgresIcons.size)
                sProgresIcons[sProgresIcons.size - 1]
            else
                sProgresIcons[word.trainedTw]
            binding.ivProgressTw.setImageDrawable(twProgress)

            binding.ivAddRemove.setOnClickListener { removeListener(word) }
            binding.ivReset.setOnClickListener { progressResetListener(word) }

            binding.btShow.setOnClickListener { binding.tvCardTranslation.setVisibility(true) }
            binding.btPronounce.setOnClickListener { WordSpeaker.speakWord(word.word) }

            hideShowSettings()
            binding.ivShowSettings.setOnClickListener { v ->
                settingsVisible = !settingsVisible
                hideShowSettings()
            }
        }

        private fun hideShowSettings() {
            binding.group.visibility = if (settingsVisible) View.VISIBLE else View.GONE
            binding.ivShowSettings.setImageResource(if (settingsVisible)
                R.drawable.ic_arrow_up
            else
                R.drawable.ic_arrow_down)
        }
    }
}
