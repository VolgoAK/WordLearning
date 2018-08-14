package com.attiladroid.data.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

import com.google.gson.annotations.Expose

import com.attiladroid.data.DatabaseContract.*

/**
 * Created by alex on 1/3/18.
 */

@Entity(tableName = Themes.TABLE_NAME, indices = arrayOf(Index(value = Themes.COLUMN_CODE, unique = true)))
class Theme : DataEntity {

    @ColumnInfo(name = Themes._ID)
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0
    @Expose
    @ColumnInfo(name = Themes.COLUMN_NAME)
    var name: String? = null
    @Expose
    @ColumnInfo(name = Themes.COLUMN_CODE)
    var code: String? = null
}
