package com.attiladroid.data.db.Dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update

import com.attiladroid.data.DataContract.*

import com.attiladroid.data.entities.Set

import io.reactivex.Flowable

/**
 * Created by alex on 1/5/18.
 */

@Dao
interface SetsDao {

    @Query("SELECT * FROM SETS_TABLE WHERE VISIBILITY =" + Sets.VISIBLE)
    fun allSets(): List<Set>

    @Query("SELECT * FROM SETS_TABLE")
    fun allSetsAsync(): LiveData<List<Set>>

    @Insert
    fun insertSet(set: Set): Long

    @Insert
    fun insertSets(vararg sets: Set)

    @Update
    fun updateSets(vararg sets: Set)

    @Query("SELECT * FROM SETS_TABLE WHERE _id = :setId")
    fun getSetById(setId: Long): Flowable<Set>

    @Query("SELECT * FROM SETS_TABLE WHERE THEME_CODES LIKE :themeCode")
    fun getSetsByTheme(themeCode: String): List<Set>
}
