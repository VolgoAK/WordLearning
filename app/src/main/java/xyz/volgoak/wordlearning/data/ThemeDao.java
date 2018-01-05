package xyz.volgoak.wordlearning.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

import xyz.volgoak.wordlearning.entities.Theme;

/**
 * Created by alex on 1/5/18.
 */

@Dao
public interface ThemeDao {

    @Insert
    int insertTheme(Theme theme);
}
