package xyz.volgoak.wordlearning.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import xyz.volgoak.wordlearning.entities.Set;

/**
 * Created by alex on 1/5/18.
 */

@Dao
public interface SetsDao {

    @Insert
    long insertSet(Set set);

    @Insert
    void insertSets(Set...sets);

    @Update
    void updateSets(Set...sets);

    @Query("SELECT * FROM SETS_TABLE WHERE VISIBILITY ="+ DatabaseContract.Sets.VISIBLE)
    List<Set> getAllSets();

    @Query("SELECT * FROM SETS_TABLE WHERE _id = :setId")
    Set getSetById(long setId);

    @Query("SELECT * FROM SETS_TABLE WHERE THEME_CODE = :themeCode")
    List<Set> getSetsByTheme(int themeCode);

}
