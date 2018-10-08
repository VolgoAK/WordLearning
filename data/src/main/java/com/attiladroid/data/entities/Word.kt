package com.attiladroid.data.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import com.attiladroid.data.DataContract.Words
import com.google.gson.annotations.Expose

/**
 * Created by alex on 1/3/18.
 */

@Entity(tableName = Words.TABLE_NAME)
data class Word(
        @ColumnInfo(name = Words._ID, index = true)
        @PrimaryKey(autoGenerate = true)
        override var id: Long = 0,
        @Expose
        @ColumnInfo(name = Words.COLUMN_WORD)
        var word: String = "",
        @Expose
        @ColumnInfo(name = Words.COLUMN_TRANSLATION)
        var translation: String = "",
        @Expose
        @ColumnInfo(name = Words.COLUMN_TRANSCRIPTION)
        var transcription: String? = null,
        @ColumnInfo(name = Words.COLUMN_TRAINED_WT)
        var trainedWt: Int = 0,
        @ColumnInfo(name = Words.COLUMN_TRAINED_TW)
        var trainedTw: Int = 0,
        @ColumnInfo(name = Words.COLUMN_STUDIED)
        var studied: Int = 0,
        @ColumnInfo(name = Words.COLUMN_STATUS)
        var status: Int = 0,
        var addedTime: Long = 0,
        var lastTrained: Long = 0)

    : DataEntity {

    val isInDictionary: Boolean
        get() = status == Words.IN_DICTIONARY

    fun resetProgress() {
        trainedTw = 0
        trainedWt = 0
        studied = 0
    }

    override fun toString(): String {
        return "$word, $translation, tw $trainedTw, wt $trainedWt"
    }
}
