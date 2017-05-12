package xyz.volgoak.wordlearning.data;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.utils.SetsParser;

import static xyz.volgoak.wordlearning.utils.SetsParser.TAG;
import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.IN_DICTIONARY;


/**
 * Created by 777 on 07.06.2016.
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
    //for test
    static int wordCount = 0;

    public WordsDbAdapter(Context context){
        WordsSqlHelper helper = new WordsSqlHelper(context);
        mDb = helper.getWritableDatabase();
        mContext = context;

        if(isDbEmpty()){
            insertDefaultDictionary();
        }
    }

    private boolean isDbEmpty(){
        Cursor cursor = mDb.rawQuery("SELECT * FROM " + DatabaseContract.Sets.TABLE_NAME + " LIMIT 1;", null);
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

        return  mDb.insert(DatabaseContract.Words.TABLE_NAME, null, values);
    }

    public void insertDefaultDictionary(){
        Log.d(TAG, "insertDefaultDictionary");

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Sets.COLUMN_NAME, USERS_SET_NAME);
        values.put(DatabaseContract.Sets.COLUMN_VISIBILITY, DatabaseContract.Sets.INVISIBLE);

        long setId = mDb.insert(DatabaseContract.Sets.TABLE_NAME, null, values);
        //save id of default set for storage users words
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(DEFAULT_DICTIONARY_ID, setId);
        editor.apply();

        insertWord("Cat", "Кот", setId, IN_DICTIONARY );
        insertWord("Dog", "Собака", setId, IN_DICTIONARY);
        insertWord("Monkey", "Обезьяна", setId, IN_DICTIONARY);
        insertWord("Donkey", "Осел", setId, IN_DICTIONARY);
        insertWord("Pigeon", "Голубь", setId, IN_DICTIONARY);
        insertWord("Run", "Бежать", setId, IN_DICTIONARY);
        insertWord("Perfect", "Совершенный", setId, IN_DICTIONARY);
        insertWord("Stupid", "Тупой", setId, IN_DICTIONARY);
        insertWord("Asshole", "Придурок", setId, IN_DICTIONARY);
        insertWord("Beach", "Пляж", setId, IN_DICTIONARY);
        insertWord("Temple", "Храм", setId, IN_DICTIONARY);
        insertWord("Country", "Страна", setId, IN_DICTIONARY);

        SetsParser.loadStartBase(mContext);
    }

    public long insertSet(ContentValues set){
        Log.d(TAG, "insertSet: ");
        return mDb.insert(DatabaseContract.Sets.TABLE_NAME, null, set);
    }

    public Cursor fetchDictionaryWords(){
        /*String select = "SELECT a.* " +
                " FROM " + DatabaseContract.Words.TABLE_NAME + " a, " + DatabaseContract.Sets.TABLE_NAME + " b " +
                " WHERE a." + DatabaseContract.Words.COLUMN_SET_ID + " = b." + DatabaseContract.Sets._ID +
                " AND b." + DatabaseContract.Sets.COLUMN_STATUS + " = " + DatabaseContract.Sets.IN_DICTIONARY;*/

        String select = "SELECT * FROM " + DatabaseContract.Words.TABLE_NAME +
                " WHERE " + DatabaseContract.Words.COLUMN_STATUS + " = " + DatabaseContract.Words.IN_DICTIONARY ;

        Cursor cursor = mDb.rawQuery(select, null);
        Log.d(TAG, "fetchDictionaryWords: size" + cursor.getCount());
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
        Log.d(TAG, "fetchWordsByTrained: count " + cursor.getCount());

        return cursor;
    }

    public Cursor fetchSets(){
        String query = "SELECT * FROM " + DatabaseContract.Sets.TABLE_NAME +
                " WHERE " + DatabaseContract.Sets.COLUMN_VISIBILITY + " = " + DatabaseContract.Sets.VISIBLE;
        return mDb.rawQuery(query, null);
    }

    public Cursor fetchSetById(long setId){
        return mDb.rawQuery("SELECT * FROM " + DatabaseContract.Sets.TABLE_NAME +
            " WHERE " + DatabaseContract.Sets._ID + "=?", new String[]{Long.toString(setId)});
    }

    public Cursor fetchWordsBySetId(long id){
        String query = "SELECT * FROM " + DatabaseContract.Words.TABLE_NAME +
                " WHERE " + DatabaseContract.Words.COLUMN_SET_ID + " = " + id +
                " ORDER BY " + DatabaseContract.Words.COLUMN_WORD + " COLLATE NOCASE";
        return mDb.rawQuery(query, null);
    }

    public Cursor rawQuery(String query){
        return mDb.rawQuery(query, null);
    }

    // TODO: 12.05.2017 add setid arg
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

        Log.d(TAG, "resetSetProgress: " + updated + " words resit");
    }

    public void deleteWordById(long id){
        mDb.delete(DatabaseContract.Words.TABLE_NAME,  DatabaseContract.Words._ID + "=?", new String[]{Long.toString(id)});
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
