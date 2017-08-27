package xyz.volgoak.wordlearning;

import android.database.Cursor;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import xyz.volgoak.wordlearning.data.WordsDbAdapter;
import xyz.volgoak.wordlearning.data.DatabaseContract;

import static org.junit.Assert.assertTrue;

/**
 * Created by Volgoak on 26.08.2017.
 */
@RunWith(AndroidJUnit4.class)
public class ProgressResetTest {

    WordsDbAdapter adapter;

    @Before
    public void initAdapter(){
        adapter = new WordsDbAdapter();
    }

    @Test
    public void testResetStatusWithVarArgs(){
        Cursor cursor = adapter.fetchAllSets();
        cursor.moveToFirst();
        long setId = cursor.getLong(cursor.getColumnIndex("_id"));
        cursor.close();
        cursor = adapter.fetchWordsBySetId(setId);

        assertTrue(cursor.getCount() > 0);
        cursor.moveToFirst();
        //increase trained status
        do{
            adapter.changeTrainedStatus(cursor.getLong(cursor.getColumnIndex("_id"))
                    , WordsDbAdapter.INCREASE, DatabaseContract.Words.COLUMN_TRAINED_WT);
        }while (cursor.moveToNext());
        //check trained status isn't zero
        cursor = adapter.fetchWordsBySetId(setId);
        cursor.moveToFirst();
        List<Long> ids = new ArrayList<>(cursor.getCount());
        int idIndex = cursor.getColumnIndex("_id");
        do{
            int trained = cursor.getInt(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_TRAINED_WT));
            assertTrue(trained > 0);
            ids.add(cursor.getLong(idIndex));
        }while(cursor.moveToNext());

        adapter.resetWordProgress(ids.toArray(new Long[ids.size()]));
        cursor = adapter.fetchWordsBySetId(setId);
        cursor.moveToFirst();
        do{
            int trained = cursor.getInt(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_TRAINED_WT));
            assertTrue(trained == 0);
        }while(cursor.moveToNext());
    }
}
