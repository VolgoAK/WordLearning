package xyz.volgoak.wordlearning;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

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

    }

    @Test
    public void testSetsConverter() {

    }
}
