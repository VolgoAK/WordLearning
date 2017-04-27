package xyz.volgoak.wordlearning.data;

import android.provider.BaseColumns;

/**
 * Created by Volgoak on 11.04.2017.
 */

public final class DatabaseContract {

    public final static int DB_VERSION = 21;
    public final static String DB_NAME = "WORDS_DATABASE";


    public static abstract class Words implements BaseColumns{

        public final static String TABLE_NAME = "WORDS_TABLE";

        public final static String COLUMN_WORD = "WORD";
        public final static String COLUMN_TRANSLATION = "TRANSLATION";
        public final static String COLUMN_TRAINED_WT = "WT_TRAINED";
        public final static String COLUMN_TRAINED_TW = "TW_TRAINED";
        public final static String COLUMN_STUDIED = "STUDIED";
        public final static String COLUMN_SET_ID = "SET_ID";

        public final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                " ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_WORD + " TEXT, " +
                COLUMN_TRANSLATION + " TEXT, " +
                COLUMN_TRAINED_WT + " INTEGER DEFAULT 0, " +
                COLUMN_TRAINED_TW + " INTEGER DEFAULT 0, " +
                COLUMN_STUDIED + " INTEGER DEFAULT 0, " +
                COLUMN_SET_ID + " INTEGER NOT NULL, " +
                " FOREIGN KEY (" + COLUMN_SET_ID + ") REFERENCES " + Sets.TABLE_NAME + "(" +
                Sets._ID + "));";

        public final static String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class Sets implements BaseColumns{

        public static final int OUT_OF_DICTIONARY = 0;
        public static final int IN_DICTIONARY = 1;
        public static final int INVISIBLE = 0;
        public static final int VISIBLE = 1;

        public static final String TABLE_NAME = "SETS_TABLE";

        public static final String COLUMN_NAME = "NAME";
        public static final String COLUMN_NUM_OF_WORDS = "WORDS_COUNT";
        public static final String COLUMN_STATUS = "STATUS";
        public static final String COLUMN_VISIBILITY = "VISIBILITY";
        public static final String COLUMN_DESCRIPTION = "DESCRIPTION";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                "( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_NUM_OF_WORDS + " INTEGER DEFAULT 0, " +
                COLUMN_VISIBILITY + " INTEGER DEFAULT " + VISIBLE + ", " +
                COLUMN_STATUS + " INTEGER DEFAULT " + OUT_OF_DICTIONARY + ");";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
