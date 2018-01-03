package xyz.volgoak.wordlearning;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import xyz.volgoak.wordlearning.data.Converter;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.Set;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;

import static junit.framework.Assert.assertEquals;

/**
 * Created by volgoak on 02.12.2017.
 */

@RunWith(AndroidJUnit4.class)
public class DbTests {

    private WordsDbAdapter mDbAdapter;

    @Before
    public void initAdapter(){
        mDbAdapter = new WordsDbAdapter();
    }

    @Test
    public void testGetTestByThemeCode(){
        Cursor themes = mDbAdapter.fetchAllThemes();
        Cursor sets = mDbAdapter.fetchAllSets();
        int allSetsCount = sets.getCount();
        sets.close();

        int setsByThemesCount = 0;
        themes.moveToFirst();

        do{
            Cursor themeSets = mDbAdapter.fetchSetsByThemeCode(themes.getInt
                    (themes.getColumnIndex(DatabaseContract.Themes.COLUMN_CODE)));
            setsByThemesCount += themeSets.getCount();
            themeSets.close();
        }while (themes.moveToNext());

        themes.close();

        assertEquals(allSetsCount, setsByThemesCount);
    }

    @Test
    public void testSetsConverter() {
        Cursor sets = mDbAdapter.fetchAllSets();
        int count = sets.getCount();
        sets.moveToFirst();
        String firstSetName = sets.getString(sets.getColumnIndex(DatabaseContract.Sets.COLUMN_NAME));
        List<Set> setList = Converter.convertSets(sets);

        assertEquals(count, setList.size());
        assertEquals(firstSetName, setList.get(0).getName());
    }
}
