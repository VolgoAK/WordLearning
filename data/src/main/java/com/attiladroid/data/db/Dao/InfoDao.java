package com.attiladroid.data.db.Dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.attiladroid.data.DataContract;
import com.attiladroid.data.entities.DictionaryInfo;


/**
 * Created by alex on 1/5/18.
 */

@Dao
public interface InfoDao {

    int TRAINING_LIMIT = 4;

    @Query("SELECT COUNT(" + DataContract.Words._ID + ") AS " + DataContract.Info.ALL_WORDS_COUNT + ", "

            + " COUNT( CASE WHEN " + DataContract.Words.COLUMN_TRAINED_WT + ">=" + TRAINING_LIMIT
            + " AND " + DataContract.Words.COLUMN_TRAINED_TW + ">=" + TRAINING_LIMIT
            + " THEN 1 ELSE NULL END) AS " + DataContract.Info.STUDIED_WORDS_COUNT + ","

            + " COUNT( CASE WHEN " + DataContract.Words.COLUMN_STATUS + "=" + DataContract.Words.IN_DICTIONARY
            + " THEN 1 ELSE NULL END) AS " + DataContract.Info.DICTIONARY_WORDS_COUNT

            + " FROM " + DataContract.Words.TABLE_NAME)
    LiveData<DictionaryInfo> getDictionaryInfo();
}
