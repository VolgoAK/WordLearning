package xyz.volgoak.wordlearning.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import xyz.volgoak.wordlearning.entities.Theme;

/**
 * Created by alex on 1/5/18.
 */

@Dao
public interface ThemeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTheme(Theme theme);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertThemes(Theme...themes);

    @Query("SELECT * FROM THEMES_TABLE")
    LiveData<List<Theme>> getAllThemes();
}
