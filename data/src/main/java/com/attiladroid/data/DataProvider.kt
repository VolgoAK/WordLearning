package com.attiladroid.data

import android.arch.lifecycle.LiveData
import android.content.Context

import com.attiladroid.data.db.AppDatabase
import com.attiladroid.data.db.Dao.InfoDao
import com.attiladroid.data.db.Dao.LinkDao
import com.attiladroid.data.db.Dao.SetsDao
import com.attiladroid.data.db.Dao.ThemeDao
import com.attiladroid.data.db.Dao.WordDao
import com.attiladroid.data.entities.DictionaryInfo
import com.attiladroid.data.entities.Link
import com.attiladroid.data.entities.Word
import com.attiladroid.data.entities.Set
import com.attiladroid.data.entities.Theme

import io.reactivex.Flowable


/**
 * Created by alex on 1/5/18.
 */

class DataProvider private constructor(
        private val infoDao: InfoDao,
        private val linkDao: LinkDao,
        private val setsDao: SetsDao,
        private val themeDao: ThemeDao,
        private val wordDao: WordDao) {

    companion object {
        fun newInstance(context: Context): DataProvider {
            val db = AppDatabase.newInstance(context)
            return DataProvider(db.infoDao(), db.linkDao(), db.setsDao(),
                    db.themeDao(), db.wordDao())
        }
    }

    val dictionaryWords: List<Word>
        get() = wordDao.dictionaryWords()

    val dictionaryWordsFlowable: Flowable<List<Word>>
        get() = wordDao.dictionaryWordsFlowable()

    val allSets: List<Set>
        get() = setsDao.allSets()

    val allSetsLd: LiveData<List<Set>>
        get() = setsDao.allSetsAsync()

    val dictionaryInfo: LiveData<DictionaryInfo>
        get() = infoDao.dictionaryInfo

    val allThemes: LiveData<List<Theme>>
        get() = themeDao.allThemes()

    ///////////////////////////////////////////////////////////////////////////
    // Words methods
    ///////////////////////////////////////////////////////////////////////////

    fun insertWord(word: Word): Long {
        return wordDao.insertWord(word)
    }

    fun getWord(word: String, translation: String): Word {
        return wordDao.getWord(word, translation)
    }

    fun getWordById(id: Long): Word {
        return wordDao.getWordById(id)
    }

    fun insertWords(vararg words: Word) {
        wordDao.insertWords(*words)
    }

    fun updateWords(vararg words: Word) {
        wordDao.udateWords(*words)
    }

    // TODO: 1/7/18 Temp methods. Delete them and replace
    fun resetWordProgress(id: Long) {
        val word = wordDao.getWordById(id)
        word.studied = 0
        word.trainedTw = 0
        word.trainedWt = 0

        wordDao.udateWords(word)
    }

    fun updateWordStatus() {}

    @JvmOverloads
    fun getTrainingWords(setId: Long, limit: Int = Integer.MAX_VALUE): List<Word> {
        return if (setId == -1L) {
            wordDao.dictionaryWords()
        } else
            wordDao.getWordsBySetId(setId)
    }

    fun getVariants(wordId: Long, limit: Int, setId: Long): List<Word> {
        return if (setId == -1L) {
            wordDao.getVariants(wordId, limit)
        } else {
            wordDao.getVarints(wordId, limit, setId)
        }
    }

    fun getWordsBySetId(setId: Long): LiveData<MutableList<Word>> {
        return wordDao.getWordsBySetIdLiveData(setId)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Sets methods
    ///////////////////////////////////////////////////////////////////////////

    fun insertSet(set: Set): Long {
        return setsDao.insertSet(set)
    }

    fun insertSets(vararg sets: Set) {
        setsDao.insertSets(*sets)
    }

    fun updateSets(vararg sets: Set) {
        setsDao.updateSets(*sets)
    }

    fun switchSetStatus(set: Set) {
        val status = if (set.status == DataContract.Sets.IN_DICTIONARY)
            DataContract.Sets.OUT_OF_DICTIONARY
        else
            DataContract.Sets.IN_DICTIONARY
        set.status = status
        updateSetStatus(set)
    }

    fun updateSetStatus(set: Set) {
        setsDao.updateSets(set)
        val words = wordDao.getWordsBySetId(set.id)
        val time = System.currentTimeMillis()
        val updateTime = set.status == DataContract.Sets.IN_DICTIONARY
        for (word in words) {
            word.status = set.status
            if (updateTime) word.addedTime = time
        }
        val wordsArray = words.toTypedArray()
        wordDao.udateWords(*wordsArray)
    }

    fun resetSetProgress(set: Set) {
        val words = wordDao.getWordsBySetId(set.id)
        for (word in words) {
            word.resetProgress()
        }
        wordDao.udateWords(*words.toTypedArray())
    }

    fun getSetById(setId: Long): LiveData<Set> {
        return setsDao.getSetById(setId)
    }

    // TODO: 1/25/18 make with string
    fun getSetsByTheme(themeCode: String): List<Set> {
        return setsDao.getSetsByTheme("%$themeCode%")
    }

    ///////////////////////////////////////////////////////////////////////////
    // Links, themes and info
    ///////////////////////////////////////////////////////////////////////////

    fun insertLink(link: Link) {
        linkDao.insertLinks(link)
    }

    fun insertLinks(vararg links: Link) {
        linkDao.insertLinks(*links)
    }

    fun insertTheme(theme: Theme): Long {
        return themeDao.insertTheme(theme)
    }

    fun insertThemes(vararg themes: Theme) {
        themeDao.insertThemes(*themes)
    }
}
