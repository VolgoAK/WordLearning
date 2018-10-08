package com.attiladroid.data.entities

/**
 * Created by Volgoak on 02.09.2017.
 */

import android.arch.persistence.room.ColumnInfo

import com.attiladroid.data.DataContract.Info


/**Keeps info about database
 * amount of words, words in users dictionary,
 * learned words */
class DictionaryInfo {
    @ColumnInfo(name = Info.ALL_WORDS_COUNT)
    var allWords: Int = 0
    @ColumnInfo(name = Info.DICTIONARY_WORDS_COUNT)
    var wordsInDictionary: Int = 0
    @ColumnInfo(name = Info.STUDIED_WORDS_COUNT)
    var learnedWords: Int = 0
}
