package xyz.volgoak.wordlearning;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import xyz.volgoak.wordlearning.data.AppDatabase;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.SetsDao;
import xyz.volgoak.wordlearning.data.ThemeDao;
import xyz.volgoak.wordlearning.data.WordDao;
import xyz.volgoak.wordlearning.entities.Set;
import xyz.volgoak.wordlearning.entities.Theme;

import static junit.framework.Assert.assertEquals;

/**
 * Created by alex on 1/4/18.
 */

@RunWith(AndroidJUnit4.class)
public class RoomTests {
    AppDatabase database;
    WordDao wordDao;
    SetsDao setsDao;
    ThemeDao themesDao;

    @Before
    public void initDb(){
        Context context = InstrumentationRegistry.getTargetContext();
        database = Room.databaseBuilder(context, AppDatabase.class, DatabaseContract.DB_NAME).build();
        wordDao = database.getWordDao();
        setsDao = database.getSetsDao();
        themesDao = database.getThemeDao();
    }

    @Test
    public void themesTest() {
        List<Theme> themes = themesDao.getAllThemes();
        List<Set> sets = setsDao.getAllSets();

        int setsByThemeCount = 0;
        for(Theme theme : themes) {
            setsByThemeCount += setsDao.getSetsByTheme(theme.getCode()).size();
        }

        assertEquals(sets.size(), setsByThemeCount);
    }
}
