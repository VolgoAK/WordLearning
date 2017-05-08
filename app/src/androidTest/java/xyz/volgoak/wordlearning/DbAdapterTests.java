package xyz.volgoak.wordlearning;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashSet;
import java.util.Set;

import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;
import xyz.volgoak.wordlearning.training_utils.TrainingFabric;

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
    public void getAllWordsTest(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        long setId = preferences.getLong(WordsDbAdapter.DEFAULT_DICTIONARY_ID, -1);
        Cursor cursor = dbAdapter.fetchWordsBySetId(setId);
        System.out.println("Cursor default dictionary count is " + cursor.getCount());

        cursor = dbAdapter.fetchDictionaryWords();
        System.out.println("Cursor dictionary words count is " + cursor.getCount());

        cursor = dbAdapter.fetchWordsByTrained(null, Integer.MAX_VALUE, Integer.MAX_VALUE);
        System.out.println("Cursor words by trained count is " + cursor.getCount());
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

    @Test
    public void wordsUniqueTest(){
        Cursor setsCursor = dbAdapter.fetchSets();
        setsCursor.moveToFirst();
        do{
            long setId = setsCursor.getLong(setsCursor.getColumnIndex(DatabaseContract.Sets._ID));
            String setName = setsCursor.getString(setsCursor.getColumnIndex(DatabaseContract.Sets.COLUMN_NAME));
            System.out.println(setName);
            Cursor wordsCursor = dbAdapter.fetchWordsByTrained(DatabaseContract.Words.COLUMN_TRAINED_WT, 10, 4, setId);
            checkUnique(wordsCursor);
        }while(setsCursor.moveToNext());
    }

    public void checkUnique(Cursor cursor){
        Set<String> wordSet = new HashSet<>();
        cursor.moveToFirst();
        do{
            String word = cursor.getString(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_WORD));
            System.out.println(word);
            assertTrue(wordSet.add(word));
        }while(cursor.moveToNext());
    }

}
