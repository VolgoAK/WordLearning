package com.attiladroid.data.db.Dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.attiladroid.data.DatabaseContract;
import com.attiladroid.data.entities.DictionaryInfo;


/**
 * Created by alex on 1/5/18.
 */

@Dao
public interface InfoDao {

    int TRAINING_LIMIT = 4;

    @Query("SELECT COUNT(" + DatabaseContract.Words._ID + ") AS " + DatabaseContract.Info.ALL_WORDS_COUNT + ", "

            + " COUNT( CASE WHEN " + DatabaseContract.Words.COLUMN_TRAINED_WT + ">=" + TRAINING_LIMIT
            + " AND " + DatabaseContract.Words.COLUMN_TRAINED_TW + ">=" + TRAINING_LIMIT
            + " THEN 1 ELSE NULL END) AS " + DatabaseContract.Info.STUDIED_WORDS_COUNT + ","

            + " COUNT( CASE WHEN " + DatabaseContract.Words.COLUMN_STATUS + "=" + DatabaseContract.Words.IN_DICTIONARY
            + " THEN 1 ELSE NULL END) AS " + DatabaseContract.Info.DICTIONARY_WORDS_COUNT

            + " FROM " + DatabaseContract.Words.TABLE_NAME)
    LiveData<DictionaryInfo> getDictionaryInfo();
}
