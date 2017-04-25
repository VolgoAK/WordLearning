package xyz.volgoak.wordlearning;

import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import xyz.volgoak.wordlearning.data.DatabaseContract;
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
        Cursor cursor = dbAdapter.fetchWordsByTrained(null, Integer.MAX_VALUE, Integer.MAX_VALUE);
       // assertEquals(10, cursor.getCount());
    }

    @Test
    public void getWordsInDictionaryTest(){
        Cursor cursor = dbAdapter.fetchDictionaryWords();
        assertTrue(cursor.getCount() > 0);
    }

    @Test
    public void getWordsByStudiedTest(){
        Cursor cursor = dbAdapter.fetchWordsByTrained(null, Integer.MAX_VALUE, Integer.MAX_VALUE);
        cursor.moveToFirst();
        do{
            String word = cursor.getString(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_WORD));
            int studied = cursor.getInt(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_STUDIED));
            System.out.println("word " + word + " studied " + studied);
        }while (cursor.moveToNext());
    }

}
