package xyz.volgoak.wordlearning.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import xyz.volgoak.wordlearning.utils.SetsParser;

import static xyz.volgoak.wordlearning.utils.SetsParser.TAG;


/**
 * Created by 777 on 07.06.2016.
 */
public class WordsDbAdapter {

    public final static int INCREASE = 1;
    public final static int DECREASE = 2;
    public final static int TO_ZERO = 3;

    private final static String USER_SET_NAME = "user_set";
    //key of preference which stores id of default user dictionary
    private final static String DEFAULT_DICTIONARY_ID = "def_dictionary";

    private SQLiteDatabase mDb;
    private Context mContext;
    //for test
    static int wordCount = 0;

    public WordsDbAdapter(Context context){
        WordsSqlHelper helper = new WordsSqlHelper(context);
        mDb = helper.getWritableDatabase();
        mContext = context;
        //insertTestData();
    }

    public long insertWord(String word, String translation, long setId){
        //if set id -1 save word in the default word set
        if(setId == -1){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            setId = preferences.getLong(DEFAULT_DICTIONARY_ID, 0);
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Words.COLUMN_WORD, word);
        values.put(DatabaseContract.Words.COLUMN_TRANSLATION, translation);
        values.put(DatabaseContract.Words.COLUMN_SET_ID, setId);

        return  mDb.insert(DatabaseContract.Words.TABLE_NAME, null, values);
    }

    public void insertTestData(){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Sets.COLUMN_NAME, USER_SET_NAME);
        values.put(DatabaseContract.Sets.COLUMN_STATUS, DatabaseContract.Sets.IN_DICTIONARY);
        values.put(DatabaseContract.Sets.COLUMN_VISIBILITY, DatabaseContract.Sets.INVISIBLE);

        long setId = mDb.insert(DatabaseContract.Sets.TABLE_NAME, null, values);
        //save id of default set for storage users words
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(DEFAULT_DICTIONARY_ID, setId);
        editor.apply();

        insertWord("Cat", "Кот", setId);
        insertWord("Dog", "Собака", setId);
        insertWord("Monkey", "Обезьяна", setId);
        insertWord("Donkey", "Осел", setId);
        insertWord("Pigeon", "Голубь", setId);
        insertWord("Run", "Бежать", setId);
        insertWord("Perfect", "Совершенный", setId);
        insertWord("Stupid", "Тупой", setId);
        insertWord("Asshole", "Придурок", setId);
        insertWord("Beach", "Пляж", setId);
        insertWord("Temple", "Храм", setId);
        insertWord("Country", "Страна", setId);

        SetsParser.loadStartBase(mContext);
    }

    public long insertSet(ContentValues set){
        Log.d(TAG, "insertSet: ");
        return mDb.insert(DatabaseContract.Sets.TABLE_NAME, null, set);
    }

   /* public Cursor fetchAllWords(){
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + DatabaseContract.Words.TABLE_NAME, null);
        // if mDb is empty add some test words
        if(!cursor.moveToFirst()){
            insertTestData();
            cursor = mDb.rawQuery("SELECT * FROM " + DatabaseContract.Words.TABLE_NAME
                    , null);
        }
        return cursor;
    }*/

    public Cursor fetchDictionaryWords(){
        String select = "SELECT a.*, b. " + DatabaseContract.Sets._ID +
                " FROM " + DatabaseContract.Words.TABLE_NAME + " a, " + DatabaseContract.Sets.TABLE_NAME + " b " +
                " WHERE a." + DatabaseContract.Words.COLUMN_SET_ID + " = b." + DatabaseContract.Sets._ID +
                " AND b." + DatabaseContract.Sets.COLUMN_STATUS + " = " + DatabaseContract.Sets.IN_DICTIONARY;

        Cursor cursor = mDb.rawQuery(select, null);
        return cursor;
    }

    public Cursor fetchWordsByTrained(String trainedType){
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + DatabaseContract.Words.TABLE_NAME + " ORDER BY " + trainedType +
                " LIMIT 10;", null);
        if(!cursor.moveToFirst()){
            insertTestData();
            cursor = mDb.rawQuery("SELECT * FROM " + DatabaseContract.Words.TABLE_NAME + " ORDER BY " + trainedType  +
                    " LIMIT 10;", null);
        }
        return cursor;
    }

    public Cursor fetchWordsByTrained(){
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + DatabaseContract.Words.TABLE_NAME + " ORDER BY " + DatabaseContract.Words.COLUMN_STUDIED +
                " LIMIT 10;", null);
        if(!cursor.moveToFirst()){
            insertTestData();
            cursor = mDb.rawQuery("SELECT * FROM " + DatabaseContract.Words.TABLE_NAME + " ORDER BY " + DatabaseContract.Words.COLUMN_STUDIED +
                    " LIMIT 10;", null);
        }
        return cursor;
    }

    public Cursor fetchSets(){
        String query = "SELECT * FROM " + DatabaseContract.Sets.TABLE_NAME +
                " WHERE " + DatabaseContract.Sets.COLUMN_VISIBILITY + " = " + DatabaseContract.Sets.IN_DICTIONARY;
        return mDb.rawQuery(query, null);
    }

    public Cursor rawQuery(String query){
        return mDb.rawQuery(query, null);
    }

    public String[] getVariants(int id, String column){
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + DatabaseContract.Words.TABLE_NAME
                + " WHERE " + DatabaseContract.Words._ID + " != " + id
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
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + DatabaseContract.Words.TABLE_NAME
                + " WHERE " + DatabaseContract.Words._ID + "=" + id, null);
        cursor.moveToFirst();

        currentStatus = cursor.getInt(cursor.getColumnIndex(trainedType));
        studiedStatus = cursor.getInt(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_STUDIED));

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
        values.put(DatabaseContract.Words.COLUMN_STUDIED, studiedStatus);

        mDb.update(DatabaseContract.Words.TABLE_NAME, values, DatabaseContract.Words._ID + "=?", new String[]{Integer.toString(id)});
    }

    public void deleteWordById(int id){
        mDb.delete(DatabaseContract.Words.TABLE_NAME,  DatabaseContract.Words._ID + "=?", new String[]{Integer.toString(id)});
    }

    public void close(){
        mContext = null;
        mDb.close();
    }

    static class WordsSqlHelper extends SQLiteOpenHelper {


        public WordsSqlHelper(Context context){
            super(context, DatabaseContract.DB_NAME, null, DatabaseContract.DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DatabaseContract.Sets.CREATE_TABLE);
            db.execSQL(DatabaseContract.Words.CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL(DatabaseContract.Words.DELETE_TABLE);
            db.execSQL(DatabaseContract.Sets.DELETE_TABLE);
            onCreate(db);
        }
    }

}
