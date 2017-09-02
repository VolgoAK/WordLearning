package xyz.volgoak.wordlearning.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
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
        boolean isEmpty = !cursor.moveToFirst();
        cursor.close();
        return isEmpty;
    }

    public long getWordId(String word){
        Cursor cursor = mDb.rawQuery("SELECT "+DatabaseContract.Words._ID+" FROM "+DatabaseContract.Words.TABLE_NAME +
                " WHERE "+DatabaseContract.Words.COLUMN_WORD+"='"+word+"' COLLATE NOCASE", null);
        long id;
        if(cursor.moveToFirst())id=cursor.getLong(cursor.getColumnIndex(DatabaseContract.Words._ID));
        else id = -1;
        cursor.close();
        return id;
    }

    public long insertLink(ContentValues wordValues, long setId){
        //insert word or just get id if already exists
        String word = wordValues.getAsString(DatabaseContract.Words.COLUMN_WORD);
        long wordId = getWordId(word);
        if(wordId == -1){
            wordId = insertWord(wordValues);
        }

        //create ContentValues for link
        ContentValues linkValues = new ContentValues();
        linkValues.put(DatabaseContract.WordLinks.COLUMN_SET_ID, setId);
        linkValues.put(DatabaseContract.WordLinks.COLUMN_WORD_ID, wordId);
        return mDb.insert(DatabaseContract.WordLinks.TABLE_NAME, null, linkValues);
    }

    //for adding words via dictionary
    public long insertWord(String word, String translation){
        long id = getWordId(word);
        Log.d(TAG, "insertWord: " + word + " id " + id);
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Words.COLUMN_STATUS, IN_DICTIONARY);

        if(id == -1){
            Log.d(TAG, "insertWord: insert new word");
            values.put(DatabaseContract.Words.COLUMN_WORD, word);
            values.put(DatabaseContract.Words.COLUMN_TRANSLATION, translation);
            return  insertWord(values);
        }else {
            Log.d(TAG, "insertWord: update word");
            mDb.update(DatabaseContract.Words.TABLE_NAME, values,
                    DatabaseContract.Words._ID+"=?", new String[]{String.valueOf(id)});
            return id;
        }
    }

    private long insertWord(ContentValues wordValues){
        return mDb.insert(DatabaseContract.Words.TABLE_NAME, null, wordValues);
    }

    private void insertDefaultDictionary(){
//        Log.d(TAG, "insertDefaultDictionary");

        insertWord("Hello", "Привет");
        insertWord("Name", "Имя");
        insertWord("Human", "Человек");
        insertWord("He", "Он");
        insertWord("She", "Она");
        insertWord("Where", "Где");
        insertWord("When", "Когда");
        insertWord("Why", "Почему");
        insertWord("Who", "Кто");
        insertWord("What", "Что");
        insertWord("Time", "Время");
        insertWord("Country", "Страна");

        SetsLoader.loadStartBase(mContext);
//        SetsLoader.checkForDbUpdate(mContext);
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

    /***
     * Returns cursor with words sorted by trained status
     * @param trainedType type of training for sorting
     * @param wordsLimit maximum amount of words
     * @param trainedLimit maximal training status of word
     * @param setId id of set from which words will be taken
     *              use -1 for ignore set id
     * @return cursor with words
     */
    public Cursor fetchWordsByTrained(String trainedType, int wordsLimit, int trainedLimit, long setId){
        if(trainedType == null) trainedType = DatabaseContract.Words.COLUMN_STUDIED;

        String select = "";

        if(setId == -1 ){

            select = "SELECT * FROM " + DatabaseContract.Words.TABLE_NAME +
                    " WHERE " + DatabaseContract.Words.COLUMN_STATUS + " = " + DatabaseContract.Words.IN_DICTIONARY +
                    " AND " + trainedType + " < " + trainedLimit +
                    " ORDER BY " + trainedType +
                    " LIMIT " + wordsLimit + ";";
        }else {
            select = "SELECT * FROM "+DatabaseContract.Words.TABLE_NAME +
                    " WHERE "+DatabaseContract.Words._ID+" IN " +

                    "( SELECT "+DatabaseContract.WordLinks.COLUMN_WORD_ID+
                    " FROM "+DatabaseContract.WordLinks.TABLE_NAME +
                    " WHERE "+ DatabaseContract.WordLinks.COLUMN_SET_ID+"="+setId + ")" +

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
        String query = "SELECT * FROM "+DatabaseContract.Words.TABLE_NAME +
                " WHERE "+DatabaseContract.Words._ID+" IN " +
                "(SELECT "+DatabaseContract.WordLinks.COLUMN_WORD_ID+" FROM " +
                DatabaseContract.WordLinks.TABLE_NAME+
                " WHERE "+DatabaseContract.WordLinks.COLUMN_SET_ID+"="+id+")" +
                " ORDER BY " + DatabaseContract.Words.COLUMN_WORD + " COLLATE NOCASE";
        return mDb.rawQuery(query, null);
    }

    public String[] getVariants(int id, String column, long setId){
        String select = "SELECT * FROM " + DatabaseContract.Words.TABLE_NAME
                + " WHERE " + DatabaseContract.Words._ID + " != " + id;

        if(setId != -1){
            select += " AND " +DatabaseContract.Words._ID+" IN " +
                    "( SELECT "+DatabaseContract.WordLinks.COLUMN_WORD_ID +
                    " FROM "+DatabaseContract.WordLinks.TABLE_NAME +
                    " WHERE "+DatabaseContract.WordLinks.COLUMN_SET_ID+"="+setId + ")";
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

    // TODO: 16.06.2017 add varargs for update many words at once
    public void resetWordProgress2(long id){
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

        mDb.execSQL("UPDATE "+DatabaseContract.Words.TABLE_NAME +
            " SET "+DatabaseContract.Words.COLUMN_STATUS+"="+status +
            " WHERE "+DatabaseContract.Words._ID+" IN " +
            "( SELECT "+DatabaseContract.WordLinks.COLUMN_WORD_ID+" FROM "+DatabaseContract.WordLinks.TABLE_NAME +
            " WHERE "+DatabaseContract.WordLinks.COLUMN_SET_ID+"="+id +
            ");");

    }

    public void resetSetProgress(long setId){

        mDb.execSQL("UPDATE "+DatabaseContract.Words.TABLE_NAME +
                " SET "+DatabaseContract.Words.COLUMN_TRAINED_TW+"=0,"+
                DatabaseContract.Words.COLUMN_TRAINED_WT+"=0," +
                DatabaseContract.Words.COLUMN_STUDIED+"=0" +
                " WHERE "+DatabaseContract.Words._ID+" IN " +
                "( SELECT "+DatabaseContract.WordLinks.COLUMN_WORD_ID+" FROM "+DatabaseContract.WordLinks.TABLE_NAME +
                " WHERE "+DatabaseContract.WordLinks.COLUMN_SET_ID+"="+setId+");");

    }

    public int resetWordProgress(Long...ids){
        String[] stringIds = new String[ids.length];
        for(int a = 0; a < ids.length; a++){
            stringIds[a] = String.valueOf(ids[a]);
        }

        StringBuilder builder = new StringBuilder();
        builder.append(DatabaseContract.Words._ID + "=?");
        for(int a = 1; a < ids.length; a++){
            builder.append(" OR " + DatabaseContract.Words._ID + "=?");
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Words.COLUMN_TRAINED_WT, 0);
        values.put(DatabaseContract.Words.COLUMN_TRAINED_TW, 0);
        values.put(DatabaseContract.Words.COLUMN_STUDIED, 0);

        return mDb.update(DatabaseContract.Words.TABLE_NAME, values, builder.toString(), stringIds);
    }

    public int changeWordStatus(int newStatus, Long... ids){
        String[] stringIds = new String[ids.length];
        for(int a = 0; a < ids.length; a++){
            stringIds[a] = String.valueOf(ids[a]);
        }

        StringBuilder builder = new StringBuilder();
        builder.append(DatabaseContract.Words._ID + "=?");
        for(int a = 1; a < ids.length; a++){
            builder.append(" OR " + DatabaseContract.Words._ID + "=?");
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseContract.Words.COLUMN_STATUS, newStatus);

        return mDb.update(DatabaseContract.Words.TABLE_NAME, values, builder.toString(), stringIds);
    }

    public DictionaryInfo getDictionaryInfo(){
        String select = "SELECT COUNT("+DatabaseContract.Words._ID+") AS "+DatabaseContract.Info.ALL_WORDS_COUNT + ", "

                + " COUNT( CASE WHEN "+DatabaseContract.Words.COLUMN_TRAINED_WT+">="+TRAINING_LIMIT
                + " AND "+DatabaseContract.Words.COLUMN_TRAINED_TW+">="+TRAINING_LIMIT
                + " THEN 1 ELSE NULL END) AS "+DatabaseContract.Info.STUDIED_WORDS_COUNT + ","

                + " COUNT( CASE WHEN "+DatabaseContract.Words.COLUMN_STATUS+"="+DatabaseContract.Words.IN_DICTIONARY
                + " THEN 1 ELSE NULL END) AS "+DatabaseContract.Info.DICTIONARY_WORDS_COUNT

                + " FROM "+DatabaseContract.Words.TABLE_NAME;

        return new DictionaryInfo(mDb.rawQuery(select, null));
    }

    /**
     * Deletes word if it's from custom dictionary
     * or hide it if from sets
     * @param id id of word for delete
     */
    public void deleteOrHideWordById(long id){
        Cursor cursor = mDb.rawQuery("SELECT * FROM "+DatabaseContract.WordLinks.TABLE_NAME+
                " WHERE "+DatabaseContract.WordLinks.COLUMN_WORD_ID+"="+id, null);
        if(cursor.moveToFirst()){
            changeWordStatus(DatabaseContract.Words.OUT_OF_DICTIONARY, id);
        }else{
            mDb.delete(DatabaseContract.Words.TABLE_NAME, DatabaseContract.Words._ID + "=?",
                    new String[]{String.valueOf(id)});
        }
        cursor.close();
    }

    @VisibleForTesting
    public boolean isSetWord(long id){
        Cursor cursor = mDb.rawQuery("SELECT * FROM "+DatabaseContract.WordLinks.TABLE_NAME+
                " WHERE "+DatabaseContract.WordLinks.COLUMN_WORD_ID+"="+id, null);
        return cursor.moveToFirst();
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
            db.execSQL(DatabaseContract.WordLinks.CREATE_TABLE);
            db.execSQL(DatabaseContract.Themes.CREATE_TABLE);
            db.execSQL(DatabaseContract.Sets.CREATE_TABLE);
            db.execSQL(DatabaseContract.Words.CREATE_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL(DatabaseContract.WordLinks.DELETE_TABLE);
            db.execSQL(DatabaseContract.Words.DELETE_TABLE);
            db.execSQL(DatabaseContract.Sets.DELETE_TABLE);
            db.execSQL(DatabaseContract.Themes.DELETE_TABLE);
            onCreate(db);
        }
    }

}
