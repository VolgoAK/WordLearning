package com.attiladroid.data.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

import com.google.gson.annotations.Expose

import com.attiladroid.data.DatabaseContract.*

/**
 * Created by alex on 1/3/18.
 */

@Entity(tableName = Words.TABLE_NAME)
class Word : DataEntity {

    @ColumnInfo(name = Words._ID, index = true)
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0
    @Expose
    @ColumnInfo(name = Words.COLUMN_WORD)
    var word: String? = null
    @Expose
    @ColumnInfo(name = Words.COLUMN_TRANSLATION)
    var translation: String? = null
    @Expose
    @ColumnInfo(name = Words.COLUMN_TRANSCRIPTION)
    var transcription: String? = null
    @ColumnInfo(name = Words.COLUMN_TRAINED_WT)
    var trainedWt: Int = 0
    @ColumnInfo(name = Words.COLUMN_TRAINED_TW)
    var trainedTw: Int = 0
    @ColumnInfo(name = Words.COLUMN_STUDIED)
    var studied: Int = 0
    @ColumnInfo(name = Words.COLUMN_STATUS)
    var status: Int = 0

    var addedTime: Long = 0
    var lastTrained: Long = 0

    val isInDictionary: Boolean
        get() = status == Words.IN_DICTIONARY

    @Ignore
    constructor(word: String) {
        this.word = word
    }

    @Ignore
    constructor(word: String, translation: String) {
        this.word = word
        this.translation = translation
        this.status = Words.IN_DICTIONARY
    }

    constructor(id: Long, word: String, translation: String, transcription: String, trainedWt: Int,
                trainedTw: Int, studied: Int, status: Int) {
        this.id = id
        this.word = word
        this.translation = translation
        this.transcription = transcription
        this.trainedWt = trainedWt
        this.trainedTw = trainedTw
        this.studied = studied
        this.status = status
    }

    fun resetProgress() {
        trainedTw = 0
        trainedWt = 0
        studied = 0
    }

    override fun toString(): String {
        return "$word, $translation, tw $trainedTw, wt $trainedWt"
    }
}
