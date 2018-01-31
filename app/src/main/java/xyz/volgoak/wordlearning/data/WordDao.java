package xyz.volgoak.wordlearning.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.support.annotation.NonNull;

import java.util.List;

import xyz.volgoak.wordlearning.entities.Word;
import xyz.volgoak.wordlearning.data.DatabaseContract.*;

/**
 * Created by alex on 1/4/18.
 */

@Dao
public interface WordDao {

    @Insert
    long insertWord(Word word);

    @Insert
    void insertWords(Word...words);

    @Update
    void udateWords(Word...words);

    @Query("SELECT * FROM words_table WHERE _id = :id")
    Word getWordById(long id);

    @Query("SELECT * FROM WORDS_TABLE WHERE WORD = :word AND TRANSLATION = :translation")
    Word getWord(String word, String translation);

    @Query("SELECT * FROM words_table WHERE STATUS=" + Words.IN_DICTIONARY)
    List<Word> getDictionaryWords();

    @Query("SELECT * FROM words_table WHERE _id != :id ORDER BY RANDOM() LIMIT :wordsLimit ")
    List<Word> getVariants(long id, int wordsLimit);

    @Query("SELECT * FROM words_table WHERE _id != :id AND _id IN "
            + "(SELECT "+WordLinks.COLUMN_WORD_ID+" FROM "+WordLinks.TABLE_NAME
            + " WHERE "+WordLinks.COLUMN_SET_ID+"=:setId)"
            + " ORDER BY RANDOM() LIMIT :wordsLimit")
    List<Word> getVarints(long id, int wordsLimit, long setId);

    @Query("SELECT * FROM words_table WHERE _id IN "
            + "(SELECT "+WordLinks.COLUMN_WORD_ID+" FROM "+WordLinks.TABLE_NAME
            + " WHERE "+WordLinks.COLUMN_SET_ID+"=:setId)"
            + " ORDER BY WORD COLLATE NOCASE")
    List<Word> getWordsBySetId(long setId);


}
