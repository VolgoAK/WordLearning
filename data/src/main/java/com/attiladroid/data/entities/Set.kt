package com.attiladroid.data.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.attiladroid.data.DatabaseContract.Sets
import com.google.gson.annotations.Expose


/**
 * Created by alex on 1/3/18.
 */

@Entity(tableName = Sets.TABLE_NAME)
data class Set(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = Sets._ID, index = true)
        override var id: Long = 0,
        @Expose
        @ColumnInfo(name = Sets.COLUMN_NAME)
        var name: String? = null,
        @Expose
        @ColumnInfo(name = Sets.COLUMN_DESCRIPTION)
        var description: String? = null,
        @Expose
        @ColumnInfo(name = Sets.COLUMN_IMAGE_URL)
        var imageUrl: String? = null,
        @Expose
        @ColumnInfo(name = Sets.COLUMN_LANG)
        var lang: String? = null,
        @ColumnInfo(name = Sets.COLUMN_NUM_OF_WORDS)
        var wordsCount: Int = 0,
        @ColumnInfo(name = Sets.COLUMN_STATUS)
        var status: Int = 0,
        @ColumnInfo(name = Sets.COLUMN_VISIBILITY)
        var visibitity: Int = 0,
        @Expose
        @ColumnInfo(name = Sets.COLUMN_THEME_CODES, index = true)
        var themeCodes: String? = null,
        @Expose
        @Ignore
        var words: List<Word>? = null
) : DataEntity
