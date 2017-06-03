package xyz.volgoak.wordlearning.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.support.annotation.VisibleForTesting;
import android.util.Log;

import xyz.volgoak.wordlearning.WordsApp;
import xyz.volgoak.wordlearning.utils.SetsLoader;

import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.IN_DICTIONARY;


/**
 * Created by Alexander Karachev on 07.06.2016.
 */
public class WordsDbAdapter {

    public static final String TAG = "WordsDbAdapter";
    public static final String DB_EMPTY_PREF = "is_db_empty";

    public static final int INCREASE = 1;
    public static final int DECREASE = 2;
    public static final int TO_ZERO = 3;
    public static final int TRAINING_LIMIT = 4;

    private final static String USERS_SET_NAME = "user_set";
    //key of preference which stores id of default user dictionary
    public final static String DEFAULT_DICTIONARY_ID = "def_dictionary";

    private SQLiteDatabase mDb;
    private Context mContext;
    private WordsSqlHelper mHelper;

    public WordsDbAdapter(){
        mContext = WordsApp.getContext();
        mHelper = new WordsSqlHelper(mContext);
        mDb = mHelper.getWritableDatabase();

        if(isDbEmpty()){
            insertDefaultDictionary();
        }
    }

    private boolean isDbEmpty(){
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + DatabaseContract.Words.TABLE_NAME + " LIMIT 1;", null);
        return !cursor.moveToFirst();
    }

    public long insertWord(String word, String translation, long setId, int status){
        //if set id -1 save word in the default word set
        if(setId == -1){
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
            setId = preferences.getLong(DEFAULT_DICTIONARY_ID, 0);
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Words.COLUMN_WORD, word);
        values.put(DatabaseContract.Words.COLUMN_TRANSLATION, translation);
        values.put(DatabaseContract.Words.COLUMN_SET_ID, setId);
        values.put(DatabaseContract.Words.COLUMN_STATUS, status);

        return  insertWord(values);
    }

    public long insertWord(ContentValues wordValues){
        return mDb.insert(DatabaseContract.Words.TABLE_NAME, null, wordValues);
    }

    private void insertDefaultDictionary(){
//        Log.d(TAG, "insertDefaultDictionary");

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Sets.COLUMN_NAME, USERS_SET_NAME);
        values.put(DatabaseContract.Sets.COLUMN_VISIBILITY, DatabaseContract.Sets.INVISIBLE);

        long setId = mDb.insert(DatabaseContract.Sets.TABLE_NAME, null, values);
        //save id of default set for storage users words
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(DEFAULT_DICTIONARY_ID, setId);
        editor.apply();

        insertWord("Hello", "Привет", setId, IN_DICTIONARY );
        insertWord("Name", "Имя", setId, IN_DICTIONARY);
        insertWord("Human", "Человек", setId, IN_DICTIONARY);
        insertWord("He", "Он", setId, IN_DICTIONARY);
        insertWord("She", "Она", setId, IN_DICTIONARY);
        insertWord("Where", "Где", setId, IN_DICTIONARY);
        insertWord("When", "Когда", setId, IN_DICTIONARY);
        insertWord("Why", "Почему", setId, IN_DICTIONARY);
        insertWord("Who", "Кто", setId, IN_DICTIONARY);
        insertWord("What", "Что", setId, IN_DICTIONARY);
        insertWord("Time", "Время", setId, IN_DICTIONARY);
        insertWord("Country", "Страна", setId, IN_DICTIONARY);

        SetsLoader.loadStartBase(mContext);
        SetsLoader.checkForDbUpdate(mContext);
    }

    public long insertSet(ContentValues set){
//        Log.d(TAG, "insertSet: ");
        return mDb.insert(DatabaseContract.Sets.TABLE_NAME, null, set);
    }

    public long insertTheme(ContentValues theme){
       return mDb.insert(DatabaseContract.Themes.TABLE_NAME, null, theme);
    }

    public Cursor fetchDictionaryWords(){

        String select = "SELECT * FROM " + DatabaseContract.Words.TABLE_NAME +
                " WHERE " + DatabaseContract.Words.COLUMN_STATUS + " = " + DatabaseContract.Words.IN_DICTIONARY ;

        Cursor cursor = mDb.rawQuery(select, null);
//        Log.d(TAG, "fetchDictionaryWords: size" + cursor.getCount());
        return cursor;
    }

    public Cursor fetchWordsByTrained(String trainedType, int wordsLimit, int trainedLimit, long setId){
        if(trainedType == null) trainedType = DatabaseContract.Words.COLUMN_STUDIED;

        String select;

        if(setId == -1){
            /*select = "SELECT a.* FROM " + DatabaseContract.Words.TABLE_NAME + " a," +
                    DatabaseContract.Sets.TABLE_NAME + " b " +
                    " WHERE a." + DatabaseContract.Words.COLUMN_SET_ID + " = b." + DatabaseContract.Sets._ID +
                    " AND b." + DatabaseContract.Sets.COLUMN_STATUS + " = " + DatabaseContract.Sets.IN_DICTIONARY +
                    " AND a." + trainedType + " < " + trainedLimit +
                    " ORDER BY " + trainedType +
                    " LIMIT " + wordsLimit + ";";*/

            select = "SELECT * FROM " + DatabaseContract.Words.TABLE_NAME +
                    " WHERE " + DatabaseContract.Words.COLUMN_STATUS + " = " + DatabaseContract.Words.IN_DICTIONARY +
                    " AND " + trainedType + " < " + trainedLimit +
                    " ORDER BY " + trainedType +
                    " LIMIT " + wordsLimit + ";";

        }else {
            select = "SELECT * FROM " + DatabaseContract.Words.TABLE_NAME +
                    " WHERE " + DatabaseContract.Words.COLUMN_SET_ID + " = " + setId +
                    " AND " + trainedType + " < " + trainedLimit +
                    " ORDER BY " + trainedType +
                    " LIMIT " + wordsLimit + ";";
        }

        Cursor cursor = mDb.rawQuery(select, null);
//        Log.d(TAG, "fetchWordsByTrained: count " + cursor.getCount());

        return cursor;
    }

    public Cursor fetchAllSets(){
        String query = "SELECT * FROM " + DatabaseContract.Sets.TABLE_NAME +
                " WHERE " + DatabaseContract.Sets.COLUMN_VISIBILITY + " = " + DatabaseContract.Sets.VISIBLE;
        return mDb.rawQuery(query, null);
    }

    public Cursor fetchSetById(long setId){
        return mDb.rawQuery("SELECT * FROM " + DatabaseContract.Sets.TABLE_NAME +
            " WHERE " + DatabaseContract.Sets._ID + "=?", new String[]{Long.toString(setId)});
    }

    public Cursor fetchSetByParam(String column, String value){
        return mDb.query(DatabaseContract.Sets.TABLE_NAME, null, column + "=?", new String[]{value},
                null, null, null);
    }

    public Cursor fetchWordsBySetId(long id){
        String query = "SELECT * FROM " + DatabaseContract.Words.TABLE_NAME +
                " WHERE " + DatabaseContract.Words.COLUMN_SET_ID + " = " + id +
                " ORDER BY " + DatabaseContract.Words.COLUMN_WORD + " COLLATE NOCASE";
        return mDb.rawQuery(query, null);
    }

    public String[] getVariants(int id, String column, long setId){
        String select = "SELECT * FROM " + DatabaseContract.Words.TABLE_NAME
                + " WHERE " + DatabaseContract.Words._ID + " != " + id;

        if(setId != -1){
            select += " AND " + DatabaseContract.Words.COLUMN_SET_ID + " = " + setId;
        }
        select += " ORDER BY RANDOM() LIMIT 3";


        Cursor cursor = mDb.rawQuery(select, null);
        cursor.moveToFirst();

        String[] variants = new String[cursor.getCount()];
        for(int a = 0; a < cursor.getCount(); a++){
            variants[a] = cursor.getString(cursor.getColumnIndex(column));
            cursor.moveToNext();
        }
        cursor.close();
        return variants;
    }

    public void changeTrainedStatus(long id, int operation, String trainedType){
        int currentStatus;
        int studiedStatus;
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + DatabaseContract.Words.TABLE_NAME
                + " WHERE " + DatabaseContract.Words._ID + "=" + id, null);
        cursor.moveToFirst();

        currentStatus = cursor.getInt(cursor.getColumnIndex(trainedType));
        studiedStatus = cursor.getInt(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_STUDIED));

        cursor.close();

        if(operation == INCREASE && currentStatus < TRAINING_LIMIT){
            currentStatus++;
            studiedStatus++;
        }else if(operation == TO_ZERO){
            currentStatus = 0;
            studiedStatus -= currentStatus;
        }
        ContentValues values = new ContentValues();
        values.put(trainedType, currentStatus);
        values.put(DatabaseContract.Words.COLUMN_STUDIED, studiedStatus);

        mDb.update(DatabaseContract.Words.TABLE_NAME, values, DatabaseContract.Words._ID + "=?", new String[]{Long.toString(id)});
    }

    public void resetWordStatus(long id){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Words.COLUMN_TRAINED_WT, 0);
        values.put(DatabaseContract.Words.COLUMN_TRAINED_TW, 0);
        values.put(DatabaseContract.Words.COLUMN_STUDIED, 0);

        mDb.update(DatabaseContract.Words.TABLE_NAME, values, DatabaseContract.Words._ID + "=?", new String[]{Long.toString(id)});
    }

    public void changeSetStatus(long id, int status){
        ContentValues setValues = new ContentValues();
        setValues.put(DatabaseContract.Sets.COLUMN_STATUS, status);

        mDb.update(DatabaseContract.Sets.TABLE_NAME, setValues, DatabaseContract.Sets._ID + "=?",
                new String[]{String.valueOf(id)});

        ContentValues wordValues = new ContentValues();
        wordValues.put(DatabaseContract.Words.COLUMN_STATUS, status);

        mDb.update(DatabaseContract.Words.TABLE_NAME, wordValues, DatabaseContract.Words.COLUMN_SET_ID + "=?",
                new String[]{String.valueOf(id)});
    }

    public void resetSetProgress(long setId){
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Words.COLUMN_TRAINED_WT, 0);
        values.put(DatabaseContract.Words.COLUMN_TRAINED_TW, 0);
        values.put(DatabaseContract.Words.COLUMN_STUDIED, 0);

        int updated = mDb.update(DatabaseContract.Words.TABLE_NAME, values, DatabaseContract.Words.COLUMN_SET_ID + "=?",
                new String[]{Long.toString(setId)});

//        Log.d(TAG, "resetSetProgress: " + updated + " words resit");
    }

    public void deleteWordById(long id){
        mDb.delete(DatabaseContract.Words.TABLE_NAME,  DatabaseContract.Words._ID + "=?", new String[]{Long.toString(id)});
    }

    /**
     * Deletes word if it's from custom dictionary
     * or hide it if from sets
     * @param id id of word for delete
     */
    public void deleteOrHideWordById(long id){
//        Log.d(TAG, "deleteOrHideWordById: id " + id);
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + DatabaseContract.Words.TABLE_NAME  +
                " WHERE " + DatabaseContract.Words._ID + " = ?", new String[]{String.valueOf(id)});
        if(!cursor.moveToFirst()){
//            Log.d(TAG, "deleteOrHideWordById: incorrect id");
            return;
        }
        long wordSetId = cursor.getLong(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_SET_ID));
        cursor.close();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        long userSetId =  prefs.getLong(DEFAULT_DICTIONARY_ID, 0);

        if(wordSetId == userSetId){
            deleteWordById(id);
        }else{
            ContentValues values = new ContentValues();
            values.put(DatabaseContract.Words.COLUMN_STATUS, DatabaseContract.Words.OUT_OF_DICTIONARY);
            mDb.update(DatabaseContract.Words.TABLE_NAME, values, DatabaseContract.Words._ID + "=?",
                    new String[]{String.valueOf(id)});
        }
    }

    public void beginTransaction(){
        mDb.beginTransactionNonExclusive();
    }

    public void endTransaction(boolean success){
        if(mDb.inTransaction()) {
            if (success) mDb.setTransactionSuccessful();
            mDb.endTransaction();
        }
    }

    @VisibleForTesting
    public Cursor rawQuery(String query){
        return mDb.rawQuery(query, null);
    }

    static class WordsSqlHelper extends SQLiteOpenHelper {

        public WordsSqlHelper(Context context){
            super(context, DatabaseContract.DB_NAME, null, DatabaseContract.DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(DatabaseContract.Themes.CREATE_TABLE);
            db.execSQL(DatabaseContract.Sets.CREATE_TABLE);
            db.execSQL(DatabaseContract.Words.CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL(DatabaseContract.Words.DELETE_TABLE);
            db.execSQL(DatabaseContract.Sets.DELETE_TABLE);
            db.execSQL(DatabaseContract.Themes.DELETE_TABLE);
            onCreate(db);
        }
    }

}
