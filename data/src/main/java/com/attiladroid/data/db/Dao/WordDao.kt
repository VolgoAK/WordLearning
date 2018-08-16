package com.attiladroid.data.db.Dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import com.attiladroid.data.DataContract.*

import com.attiladroid.data.entities.Word

import io.reactivex.Flowable

/**
 * Created by alex on 1/4/18.
 */

@Dao
interface WordDao {

    @Query("SELECT * FROM words_table WHERE STATUS=" + Words.IN_DICTIONARY)
    fun dictionaryWords(): List<Word>

    @Query("SELECT * FROM words_table WHERE STATUS=" + Words.IN_DICTIONARY)
    fun dictionaryWordsFlowable(): Flowable<List<Word>>

    @Insert
    fun insertWord(word: Word): Long

    @Insert
    fun insertWords(vararg words: Word)

    @Update
    fun udateWords(vararg words: Word)

    @Query("SELECT * FROM words_table WHERE _id = :id")
    fun getWordById(id: Long): Word

    @Query("SELECT * FROM WORDS_TABLE WHERE WORD = :word AND TRANSLATION = :translation")
    fun getWord(word: String, translation: String): Word

    @Query("SELECT * FROM words_table WHERE _id != :id AND STATUS = " + Words.IN_DICTIONARY
            + " ORDER BY RANDOM() LIMIT :wordsLimit ")
    fun getVariants(id: Long, wordsLimit: Int): List<Word>

    @Query("SELECT * FROM words_table WHERE _id != :id AND _id IN "
            + "(SELECT " + WordLinks.COLUMN_WORD_ID + " FROM " + WordLinks.TABLE_NAME
            + " WHERE " + WordLinks.COLUMN_SET_ID + "=:setId)"
            + " ORDER BY RANDOM() LIMIT :wordsLimit")
    fun getVarints(id: Long, wordsLimit: Int, setId: Long): List<Word>

    @Query("SELECT * FROM words_table WHERE _id IN "
            + "(SELECT " + WordLinks.COLUMN_WORD_ID + " FROM " + WordLinks.TABLE_NAME
            + " WHERE " + WordLinks.COLUMN_SET_ID + "=:setId)"
            + " ORDER BY WORD COLLATE NOCASE")
    fun getWordsBySetId(setId: Long): List<Word>


    @Query("SELECT * FROM words_table WHERE _id IN "
            + "(SELECT " + WordLinks.COLUMN_WORD_ID + " FROM " + WordLinks.TABLE_NAME
            + " WHERE " + WordLinks.COLUMN_SET_ID + "=:setId)"
            + " ORDER BY WORD COLLATE NOCASE")
    fun getWordsBySetIdRx(setId: Long): Flowable<List<Word>>
}
