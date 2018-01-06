package xyz.volgoak.wordlearning.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import xyz.volgoak.wordlearning.entities.Word;
import xyz.volgoak.wordlearning.data.DatabaseContract.*;

/**
 * Created by alex on 1/4/18.
 */

@Dao
public interface WordDao {

    @Insert
    int insertWord(Word word);

    @Insert
    void insertWords(Word...words);

    @Update
    void udateWords(Word...words);

    @Query("SELECT * FROM words_table WHERE STATUS = "+Words.IN_DICTIONARY
        + " AND :trainedType < :trainedLimit "
        + " ORDER BY :trainedType LIMIT :wordsLimit")
    List<Word> getWordsByTrained(String trainedType, int wordsLimit, int trainedLimit);

    @Query("SELECT * FROM words_table WHERE _id IN "
            + "(SELECT "+WordLinks.COLUMN_WORD_ID+" FROM "+WordLinks.TABLE_NAME
            + " WHERE "+WordLinks.COLUMN_SET_ID+"=:setId)"
            + " AND :trainedType < :trainedLimit"
            + " ORDER BY :trainedType LIMIT :wordsLimit")
    List<Word> getWordsByTrained(String trainedType, int wordsLimit, int trainedLimit, long setId);

    @Query("SELECT * FROM words_table WHERE _id IN "
            + "(SELECT "+WordLinks.COLUMN_WORD_ID+" FROM "+WordLinks.TABLE_NAME
            + " WHERE "+WordLinks.COLUMN_SET_ID+"=:setId)"
            + " ORDER BY WORD COLLATE NOCASE")
    List<Word> getWordsBySetId(long setId);


}
