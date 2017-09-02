package xyz.volgoak.wordlearning.data;

/**
 * Created by Volgoak on 02.09.2017.
 */

import android.database.Cursor;

/**Keeps info about database
* amount of words, words in users dictionary,
* learned words*/
public class DictionaryInfo {
    private int allWords;
    private int wordsInDictionary;
    private int learnedWords;

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
}
