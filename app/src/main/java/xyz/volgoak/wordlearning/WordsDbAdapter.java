package xyz.volgoak.wordlearning;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static xyz.volgoak.wordlearning.WordsSqlHelper.*;

/**
 * Created by 777 on 07.06.2016.
 */
class WordsDbAdapter {

    public final static int INCREASE = 1;
    public final static int DECREASE = 2;
    public final static int TO_ZERO = 3;

    private SQLiteDatabase db;
    //for test
    static int wordCount = 0;

    public WordsDbAdapter(Context context){
        Context context1 = context;
        WordsSqlHelper helper = new WordsSqlHelper(context);
        db = helper.getWritableDatabase();
    }

    public void insertWord(String word, String translation){
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORD, word);
        values.put(COLUMN_TRANSLATION, translation);
        //begin of test data
        if(wordCount > 5) {
            values.put(COLUMN_TRAINED_WT, 0);
        }else{
            values.put(COLUMN_TRAINED_WT, 1);
        }
        values.put(COLUMN_TRAINED_TW, 0);
        db.insert(WORDS_TABLE, null, values);
        wordCount++;
    }

    public Cursor fetchAllWords(){
        Cursor cursor = db.rawQuery("SELECT * FROM " + WORDS_TABLE, null);
        // if db is empty add some test words
        if(!cursor.moveToFirst()){
            insertTestData();
            cursor = db.rawQuery("SELECT * FROM " + WORDS_TABLE, null);
        }
        return cursor;
    }

    public Cursor fetchWardsByTrained(String trainedType){
        Cursor cursor = db.rawQuery("SELECT * FROM " + WORDS_TABLE + " ORDER BY " + trainedType + ";", null);
        if(!cursor.moveToFirst()){
            insertTestData();
            cursor = db.rawQuery("SELECT * FROM " + WORDS_TABLE + " ORDER BY " + trainedType + ";", null);
        }
        return cursor;
    }

    public void changeTrainedStatus(int id, int operation, String trainedType){
        int currentStatus;
        Cursor cursor = db.rawQuery("SELECT * FROM " + WORDS_TABLE + " WHERE " + COLUMN_ID + "=" + id, null);
        cursor.moveToFirst();
        currentStatus = cursor.getInt(cursor.getColumnIndex(trainedType));

        if(operation == INCREASE && currentStatus < 3){
            currentStatus++;
        }else if(operation == TO_ZERO){
            currentStatus = 0;
        }
        ContentValues values = new ContentValues();
        values.put(trainedType, currentStatus);
        values.put(COLUMN_WORD, "wer");

        db.update(WORDS_TABLE, values, COLUMN_ID + "=?", new String[]{Integer.toString(id)});
    }

    public void insertTestData(){
        insertWord("Cat", "Кот");
        insertWord("Dog", "Собака");
        insertWord("Monkey", "Обезьяна");
        insertWord("Donkey", "Осел");
        insertWord("Pigeon", "Голубь");
        insertWord("Run", "Бежать");
        insertWord("Perfect", "Совершенный");
        insertWord("Stupid", "Тупой");
        insertWord("Asshole", "Придурок");
        insertWord("Beach", "Пляж");
        insertWord("Temple", "Храм");
        insertWord("Country", "Страна");
    }

}
