package xyz.volgoak.wordlearning;

import android.os.Bundle;
import android.os.Parcel;
import android.util.SparseBooleanArray;

import org.junit.Test;

import xyz.volgoak.wordlearning.recycler.ParcelableSparseBooleanArray;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void sparseArrayTest(){
        ParcelableSparseBooleanArray array = new ParcelableSparseBooleanArray();
        for(int a = 0; a < 20; a++){
            array.put(a, a % 2 == 0);
        }

        String key = "key_key";
        Bundle bundle = new Bundle();
        bundle.putParcelable(key, array);

        ParcelableSparseBooleanArray restoredArray = bundle.getParcelable(key);

        int size = restoredArray.size();
        System.out.println("size of restored array = " + size);

        assertTrue(array.equals(restoredArray));
    }

    @Test
    public void bundleTest(){
        Bundle bundle = new Bundle();
        String key = "key";
        bundle.putString(key, key);
        System.out.println("bundle content " + bundle.describeContents());
        String newKey = bundle.getString(key);
        assertTrue(bundle.containsKey(key));
    }
}