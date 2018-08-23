package xyz.volgoak.wordlearning.screens.set.viewModel

import android.arch.lifecycle.*

import com.attiladroid.data.DataProvider
import com.attiladroid.data.DataContract
import com.attiladroid.data.entities.Word
import com.attiladroid.data.entities.Set
import java.util.concurrent.Executors

import javax.inject.Inject

import io.fabric.sdk.android.services.concurrency.AsyncTask
import xyz.volgoak.wordlearning.R.string.word
import xyz.volgoak.wordlearning.WordsApp
import xyz.volgoak.wordlearning.utils.SingleLiveEvent


/**
 * Created by alex on 2/13/18.
 */

class WordsViewModel(val id: Long) : ViewModel() {

    @Inject
    lateinit var provider: DataProvider

    val setData by lazy { provider.getSetById(id)}
    val wordsData by lazy { provider.getWordsBySetIdLD(id)}
    val startCardsData = SingleLiveEvent<Int>()

    private val executor = Executors.newSingleThreadExecutor()

    init {
        WordsApp.getsComponent().inject(this)
    }

    fun changeCurrentSetStatus() {
        val current = setData.value
        if (current != null) {
            val newStatus = if (current.status == DataContract.Sets.IN_DICTIONARY)
                DataContract.Sets.OUT_OF_DICTIONARY
            else
                DataContract.Sets.IN_DICTIONARY

            current.status = newStatus
            executor.submit {
                provider.updateSetStatus(current)
            }
        }
    }

    fun resetCurrentSetProgress() {
        val current = setData.value
        if (current != null) {
            executor.submit { provider.resetSetProgress(current) }
        }
    }

    fun updateWords(words: Array<Word>) {
        executor.submit { provider.updateWords(*words) }
    }

    fun resetWordsProgress(positions: List<Int>) {
        val set = setData.value
        if (set != null && positions.isNotEmpty()) {
            executor.submit {
                val wordsList = mutableListOf<Word>()
                val words = provider.getWordsBySetId(set.id)
                for (a in positions.indices) {
                    val word = words[positions[a]]
                    word.resetProgress()
                    wordsList.add(word)
                }
                provider.updateWords(wordsList)
            }
        }
    }

    fun changeWordsStatus(positions: List<Int>, status: Int) {
        val set = setData.value
        if (set != null && positions.isNotEmpty()) {
            executor.submit {
                val wordsList = mutableListOf<Word>()
                val words = provider.getWordsBySetId(set.id)
                for (a in positions.indices) {
                    val word = words[positions[a]]
                    word.status = status
                    wordsList.add(word)
                }
                provider.updateWords(wordsList)
            }
        }
    }

    fun updateSetStatus(set: Set) {
        executor.submit { provider.updateSetStatus(set) }
    }

    fun insertWord(newWord: Word) {
        executor.submit<Long> { provider!!.insertWord(newWord) }
    }

    fun deleteOrHideWord(word: Word) {
        word.status = DataContract.Words.OUT_OF_DICTIONARY
        AsyncTask.execute { provider!!.updateWords(word) }
    }

    override fun onCleared() {
        super.onCleared()
        executor.shutdown()
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(val id: Long): ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return WordsViewModel(id) as T
        }
    }
}
