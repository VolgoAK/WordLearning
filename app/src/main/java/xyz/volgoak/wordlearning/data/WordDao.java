package xyz.volgoak.wordlearning.data;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import xyz.volgoak.wordlearning.entities.Word;
import xyz.volgoak.wordlearning.data.DatabaseContract.*;

/**
 * Created by alex on 1/4/18.
 */

@Dao
public interface WordDao {

    @Insert
    void insertWord(Word word);

    @Query("SELECT * FROM words_table WHERE STATUS = "+Words.IN_DICTIONARY
        + " AND :trainedType > :trainedLimit "
        + " ORDER BY :trainedType LIMIT :wordsLimit")
    List<Word> getWordsByTrained(String trainedType, int wordsLimit, int trainedLimit);


}
