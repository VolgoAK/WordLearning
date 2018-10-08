package xyz.volgoak.wordlearning.screens.dictionary

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.attiladroid.data.DataContract
import com.attiladroid.data.DataProvider
import com.attiladroid.data.entities.Word
import kotlinx.coroutines.experimental.async
import xyz.volgoak.wordlearning.WordsApp
import javax.inject.Inject

class DictionaryViewModel(application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var dataProvider: DataProvider

    val wordsLd by lazy {
        dataProvider.dictionaryWordsLD
    }

    init {
        WordsApp.getsComponent().inject(this)
    }

    fun insertWord(newWord: Word) = async {
        dataProvider.insertWord(newWord)
    }


    fun deleteOrHideWord(word: Word) = async {
        word.status = DataContract.Words.OUT_OF_DICTIONARY
        dataProvider.updateWords(word)
    }

    fun resetWordProgress(word: Word) = async {
        word.resetProgress()
        dataProvider.updateWords(word)
    }

}