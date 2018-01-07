package xyz.volgoak.wordlearning.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import xyz.volgoak.wordlearning.entities.Theme;

/**
 * Created by alex on 1/5/18.
 */

@Dao
public interface ThemeDao {

    @Insert
    long insertTheme(Theme theme);

    @Query("SELECT * FROM THEMES_TABLE")
    List<Theme> getAllThemes();
}
