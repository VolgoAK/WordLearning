package xyz.volgoak.wordlearning;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import static xyz.volgoak.wordlearning.WordsSqlHelper.*;

/**
 * Created by 777 on 07.06.2016.
 */
class WordsDbAdapter {
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

    public Cursor fetchWardsByTrained(){
        Cursor cursor = db.rawQuery("SELECT * FROM " + WORDS_TABLE + " ORDER BY " + COLUMN_TRAINED_WT + ";", null);
        if(!cursor.moveToFirst()){
            insertTestData();
            cursor = db.rawQuery("SELECT * FROM " + WORDS_TABLE + " ORDER BY " + COLUMN_TRAINED_WT + ";", null);
        }
        return cursor;
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
