package xyz.volgoak.wordlearning;

import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import xyz.volgoak.wordlearning.data.WordsDbAdapter;

import static org.junit.Assert.*;

/**
 * Created by Volgoak on 28.01.2017.
 */
@RunWith(AndroidJUnit4.class)
public class DbAdapterTests {

    private Context context = InstrumentationRegistry.getTargetContext();
    private WordsDbAdapter dbAdapter = new WordsDbAdapter(context);

    @Test
    public void getWordsByTrainedCountTest(){
        Cursor cursor = dbAdapter.fetchWordsByTrained();
        assertEquals(10, cursor.getCount());
    }

}
