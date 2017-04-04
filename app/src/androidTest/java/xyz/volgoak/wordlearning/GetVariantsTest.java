package xyz.volgoak.wordlearning;

/**
 * Created by Volgoak on 28.01.2017.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import xyz.volgoak.wordlearning.data.WordsDbAdapter;
import xyz.volgoak.wordlearning.data.WordsSqlHelper;

import static org.junit.Assert.*;

@RunWith (AndroidJUnit4.class )
public class GetVariantsTest {
    Context appContext = InstrumentationRegistry.getTargetContext();
    WordsDbAdapter dbAdapter = new WordsDbAdapter(appContext);

    @Test
    public void testContext(){
        assertEquals("xyz.volgoak.wordlearning", appContext.getPackageName());
    }

    @Test
    public void variantsNotNull(){
        String[] words = dbAdapter.getVariants(5, WordsSqlHelper.COLUMN_WORD);
        assertNotNull(words);
    }

    @Test
    public void testVariants(){
        Cursor ids = dbAdapter.rawQuery("SELECT _id FROM " + WordsSqlHelper.WORDS_TABLE);
        ids.moveToFirst();
        int idsSize = ids.getCount();
        for(int a = 0; a < 100; a++){
            int randomId = (int)( Math.random() * idsSize);
            ids.moveToPosition(randomId);
            int id = ids.getInt(0);
            assertTrue(variantsNotEqualWord(id));
        }

        System.out.println("Tested times " + idsSize);
    }

    // make method to get max id from table then pick random id
    public boolean variantsNotEqualWord(int id){
        Cursor oneWord = dbAdapter.rawQuery("SELECT * FROM " + WordsSqlHelper.WORDS_TABLE + " WHERE _id=" + id);
        oneWord.moveToFirst();
        String word = oneWord.getString(oneWord.getColumnIndex(WordsSqlHelper.COLUMN_WORD));
        int wordId = oneWord.getInt(oneWord.getColumnIndex(WordsSqlHelper.COLUMN_ID));
        oneWord.close();

        String[] variants = dbAdapter.getVariants(wordId, WordsSqlHelper.COLUMN_WORD);

        boolean notEqual = true;

        System.out.println("Word is " + word);

        for(int a = 0; a < 3; a++){
            if(variants[a].equals(word)) notEqual = false;
        }

        return notEqual;
    }

}
