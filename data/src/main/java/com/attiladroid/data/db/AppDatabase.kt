package com.attiladroid.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.attiladroid.data.DataContract
import com.attiladroid.data.db.Dao.*
import com.attiladroid.data.entities.Link
import com.attiladroid.data.entities.Set
import com.attiladroid.data.entities.Theme
import com.attiladroid.data.entities.Word

/**
 * Created by alex on 1/4/18.
 */

@Database(entities = [Word::class, Set::class, Link::class, Theme::class],
        version = DataContract.DB_VERSION,
        exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        fun newInstance(context: Context) : AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DataContract.DB_NAME).build()
        }
    }

    abstract fun wordDao(): WordDao
    abstract fun setsDao(): SetsDao
    abstract fun linkDao(): LinkDao
    abstract fun themeDao(): ThemeDao
    abstract fun infoDao(): InfoDao
}
