package xyz.volgoak.wordlearning.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Update;

import xyz.volgoak.wordlearning.entities.Link;

/**
 * Created by alex on 1/5/18.
 */

@Dao
public interface LinkDao {

    @Insert
    void insertLinks(Link...links);

}
