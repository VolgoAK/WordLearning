package com.attiladroid.data.db.Dao

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.attiladroid.data.entities.Theme


/**
 * Created by alex on 1/5/18.
 */

@Dao
interface ThemeDao {

    @Query("SELECT * FROM THEMES_TABLE")
    fun allThemes(): LiveData<List<Theme>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTheme(theme: Theme): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertThemes(vararg themes: Theme)
}
