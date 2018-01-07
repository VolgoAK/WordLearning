package xyz.volgoak.wordlearning.data;

/**
 * Created by Volgoak on 02.09.2017.
 */

import android.arch.persistence.room.ColumnInfo;
import android.database.Cursor;
import android.provider.ContactsContract;

import xyz.volgoak.wordlearning.entities.Dictionary;

/**Keeps info about database
* amount of words, words in users dictionary,
* learned words*/
public class DictionaryInfo {
    @ColumnInfo(name = DatabaseContract.Info.ALL_WORDS_COUNT)
    private int allWords;
    @ColumnInfo(name = DatabaseContract.Info.DICTIONARY_WORDS_COUNT)
    private int wordsInDictionary;
    @ColumnInfo(name = DatabaseContract.Info.STUDIED_WORDS_COUNT)
    private int learnedWords;

    public DictionaryInfo() {}

    public DictionaryInfo(Cursor cursor){
        cursor.moveToFirst();
        allWords = cursor.getInt(cursor.getColumnIndex(DatabaseContract.Info.ALL_WORDS_COUNT));
        wordsInDictionary = cursor.getInt(cursor.getColumnIndex(DatabaseContract.Info.DICTIONARY_WORDS_COUNT));
        learnedWords = cursor.getInt(cursor.getColumnIndex(DatabaseContract.Info.STUDIED_WORDS_COUNT));
        cursor.close();
    }

    public int getAllWords() {
        return allWords;
    }

    public int getWordsInDictionary() {
        return wordsInDictionary;
    }

    public int getLearnedWords() {
        return learnedWords;
    }

    public void setAllWords(int allWords) {
        this.allWords = allWords;
    }

    public void setWordsInDictionary(int wordsInDictionary) {
        this.wordsInDictionary = wordsInDictionary;
    }

    public void setLearnedWords(int learnedWords) {
        this.learnedWords = learnedWords;
    }
}
