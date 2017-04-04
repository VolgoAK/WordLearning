package xyz.volgoak.wordlearning.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import static xyz.volgoak.wordlearning.data.WordsSqlHelper.*;

/**
 * Created by 777 on 07.06.2016.
 */
public class WordsDbAdapter {

    public final static int INCREASE = 1;
    public final static int DECREASE = 2;
    public final static int TO_ZERO = 3;

    private SQLiteDatabase db;
    //for test
    static int wordCount = 0;

    public WordsDbAdapter(Context context){
        WordsSqlHelper helper = new WordsSqlHelper(context);
        db = helper.getWritableDatabase();
        //insertTestData();
    }

    public void insertWord(String word, String translation){
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORD, word);
        values.put(COLUMN_TRANSLATION, translation);
        values.put(COLUMN_TRAINED_WT, 0);
        values.put(COLUMN_TRAINED_TW, 0);
        values.put(COLUMN_STUDIED, 0);
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

    public Cursor fetchWordsByTrained(String trainedType){
        Cursor cursor = db.rawQuery("SELECT * FROM " + WORDS_TABLE + " ORDER BY " + trainedType +
                " LIMIT 10;", null);
        if(!cursor.moveToFirst()){
            insertTestData();
            cursor = db.rawQuery("SELECT * FROM " + WORDS_TABLE + " ORDER BY " + trainedType  +
                    " LIMIT 10;", null);
        }
        return cursor;
    }

    public Cursor fetchWordsByTrained(){
        Cursor cursor = db.rawQuery("SELECT * FROM " + WORDS_TABLE + " ORDER BY " + COLUMN_STUDIED +
                " LIMIT 10;", null);
        if(!cursor.moveToFirst()){
            insertTestData();
            cursor = db.rawQuery("SELECT * FROM " + WORDS_TABLE + " ORDER BY " + COLUMN_STUDIED +
                    " LIMIT 10;", null);
        }
        return cursor;
    }

    public Cursor rawQuery(String query){
        return db.rawQuery(query, null);
    }

    public String[] getVariants(int id, String column){
        Cursor cursor = db.rawQuery("SELECT * FROM " + WORDS_TABLE
                + " WHERE " + COLUMN_ID + " != " + id
                + " ORDER BY RANDOM() LIMIT 3", null);
        cursor.moveToFirst();

        String[] variants = new String[cursor.getCount()];
        for(int a = 0; a < cursor.getCount(); a++){
            variants[a] = cursor.getString(cursor.getColumnIndex(column));
            cursor.moveToNext();
        }
        return variants;
    }

    public void changeTrainedStatus(int operation, String trainedType, int...id){

    }

    public void changeTrainedStatus(int id, int operation, String trainedType){
        int currentStatus;
        int studiedStatus;
        Cursor cursor = db.rawQuery("SELECT * FROM " + WORDS_TABLE + " WHERE " + COLUMN_ID + "=" + id, null);
        cursor.moveToFirst();

        currentStatus = cursor.getInt(cursor.getColumnIndex(trainedType));
        studiedStatus = cursor.getInt(cursor.getColumnIndex(COLUMN_STUDIED));

        cursor.close();

        if(operation == INCREASE && currentStatus < 3){
            currentStatus++;
            studiedStatus++;
        }else if(operation == TO_ZERO){
            currentStatus = 0;
            studiedStatus -= currentStatus;
        }
        ContentValues values = new ContentValues();
        values.put(trainedType, currentStatus);
        values.put(COLUMN_STUDIED, studiedStatus);

        db.update(WORDS_TABLE, values, COLUMN_ID + "=?", new String[]{Integer.toString(id)});
    }

    public void deleteWordById(int id){
        db.delete(WORDS_TABLE, COLUMN_ID + "=?", new String[]{Integer.toString(id)});
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
