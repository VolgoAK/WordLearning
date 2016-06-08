package xyz.volgoak.wordlearning;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by 777 on 07.06.2016.
 */
class WordsSqlHelper extends SQLiteOpenHelper {
    public final static int DB_VERSION = 4;

    public final static String DB_NAME = "WORDS_DATABASE";
    public final static String WORDS_TABLE = "WORDS_TABLE";

    public final static String COLUMN_ID = "_id";
    public final static String COLUMN_WORD = "WORD";
    public final static String COLUMN_TRANSLATION = "TRANSLATION";
    public final static String COLUMN_TRAINED_WT = "WT_TRAINED";
    public final static String COLUMN_TRAINED_TW = "TW_TRAINED";

    public final static String CREATE_WORDS_TABLE = "CREATE TABLE " + WORDS_TABLE +
            " ( " + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_WORD + " TEXT, " +
            COLUMN_TRANSLATION + " TEXT, " +
            COLUMN_TRAINED_WT + " INTEGER, " +
            COLUMN_TRAINED_TW + " INTEGER);";

    public WordsSqlHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_WORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + WORDS_TABLE + ";");
        onCreate(db);
    }
}
