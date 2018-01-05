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
import xyz.volgoak.wordlearning.data.WordDao;
import xyz.volgoak.wordlearning.entities.Word;

/**
 * Created by alex on 1/4/18.
 */

@RunWith(AndroidJUnit4.class)
public class RoomTests {
    AppDatabase database;
    WordDao dao;

    @Before
    public void initDb(){
        Context context = InstrumentationRegistry.getTargetContext();
        database = Room.databaseBuilder(context, AppDatabase.class, DatabaseContract.DB_NAME).build();
        dao = database.getWordDao();
    }

    @Test
    public void firstTest() {
        List<Word> words = dao.getWordsByTrained(DatabaseContract.Words.COLUMN_TRAINED_TW, 10, 2);
    }
}
