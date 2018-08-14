package com.attiladroid.data.db.Dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert

import com.attiladroid.data.entities.Link

/**
 * Created by alex on 1/5/18.
 */

@Dao
interface LinkDao {

    @Insert
    fun insertLinks(vararg links: Link)

}
