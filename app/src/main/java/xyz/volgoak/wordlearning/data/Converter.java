package xyz.volgoak.wordlearning.data;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alex on 1/3/18.
 */

public class Converter {

    public static List<Set> convertSets(Cursor setsCursor) {
        if (! setsCursor.moveToFirst()) return null;
        List<Set> setList = new ArrayList<>();
        do {
            Set set = convertSet(setsCursor);
            setList.add(set);
        } while (setsCursor.moveToNext());
        return setList;
    }

    public static Set convertSet(Cursor cursor) {
        Set set = new Set(cursor.getString(cursor.getColumnIndex(DatabaseContract.Sets.COLUMN_NAME)));
        set.setId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.Sets._ID)));
        set.setWordsCount(cursor.getInt(cursor.getColumnIndex(DatabaseContract.Sets.COLUMN_NUM_OF_WORDS)));
        set.setDescription(cursor.getString(cursor.getColumnIndex(DatabaseContract.Sets.COLUMN_DESCRIPTION)));
        set.setImageUrl(cursor.getString(cursor.getColumnIndex(DatabaseContract.Sets.COLUMN_IMAGE_URL)));
        set.setVisibitity(cursor.getInt(cursor.getColumnIndex(DatabaseContract.Sets.COLUMN_VISIBILITY)));
        set.setLang(cursor.getString(cursor.getColumnIndex(DatabaseContract.Sets.COLUMN_LANG)));
        set.setThemeCode(cursor.getInt(cursor.getColumnIndex(DatabaseContract.Sets.COLUMN_THEME_CODE)));
        set.setStatus(cursor.getInt(cursor.getColumnIndex(DatabaseContract.Sets.COLUMN_STATUS)));
        return set;
    }

    public static List<Word> convertWords(Cursor wordsCursor) {
        if(!wordsCursor.moveToFirst()) return null;
        List<Word> words = new ArrayList<>();
        do{
            Word word = convertWord(wordsCursor);
            words.add(word);
        } while (wordsCursor.moveToNext());
        return words;
    }

    public static Word convertWord(Cursor cursor) {
        Word word = new Word(cursor.getString(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_WORD)));
        word.setTranslation(cursor.getString(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_TRANSLATION)));
        word.setTranscription(cursor.getString(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_TRANSCRIPTION)));
        word.setId(cursor.getLong(cursor.getColumnIndex(DatabaseContract.Words._ID)));
        word.setTrainedWt(cursor.getInt(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_TRAINED_WT)));
        word.setTrainedTw(cursor.getInt(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_TRAINED_TW)));
        word.setStatus(cursor.getInt(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_STATUS)));
        word.setStudied(cursor.getInt(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_STUDIED)));
        return word;
    }

}
