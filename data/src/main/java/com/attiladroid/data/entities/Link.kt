package com.attiladroid.data.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import com.attiladroid.data.DatabaseContract


import com.attiladroid.data.DatabaseContract.*

/**
 * Created by alex on 1/4/18.
 */

@Entity(tableName = WordLinks.TABLE_NAME, foreignKeys = [
        ForeignKey(entity = Set::class, parentColumns = arrayOf(DatabaseContract.Sets._ID),
                childColumns = arrayOf(DatabaseContract.WordLinks.COLUMN_SET_ID)),
        ForeignKey(entity = Word::class, parentColumns = arrayOf(Words._ID),
                childColumns = arrayOf(DatabaseContract.WordLinks.COLUMN_WORD_ID))])
class Link : DataEntity {

    @ColumnInfo(name = DatabaseContract.WordLinks._ID)
    @PrimaryKey(autoGenerate = true)
    override var id: Long = 0
    @ColumnInfo(name = WordLinks.COLUMN_WORD_ID, index = true)
    var wordId: Long = 0
    @ColumnInfo(name = WordLinks.COLUMN_SET_ID, index = true)
    var idOfSet: Long = 0
}
