package xyz.volgoak.wordlearning.data;

import android.provider.BaseColumns;


/**
 * Created by Alexander Karachev on 11.04.2017.
 */

public final class DatabaseContract {
    //db version at release was 15
    public final static int DB_VERSION = 15;
    public final static String DB_NAME = "WORDS_DATABASE";


    public static abstract class Words implements BaseColumns{

        public static final int OUT_OF_DICTIONARY = 0;
        public static final int IN_DICTIONARY = 1;

        public final static String TABLE_NAME = "WORDS_TABLE";

        public final static String COLUMN_WORD = "WORD";
        public final static String COLUMN_TRANSLATION = "TRANSLATION";
        public static final String COLUMN_TRANSCRIPTION = "TRANSCRIPTION";
        public final static String COLUMN_TRAINED_WT = "WT_TRAINED";
        public final static String COLUMN_TRAINED_TW = "TW_TRAINED";
        public final static String COLUMN_STUDIED = "STUDIED";
        public static final String COLUMN_STATUS = "STATUS";

        public final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                " ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_WORD + " TEXT, " +
                COLUMN_TRANSLATION + " TEXT, " +
                COLUMN_TRANSCRIPTION + " TEXT ," +
                COLUMN_TRAINED_WT + " INTEGER DEFAULT 0, " +
                COLUMN_TRAINED_TW + " INTEGER DEFAULT 0, " +
                COLUMN_STUDIED + " INTEGER DEFAULT 0, " +
                COLUMN_STATUS + " INTEGER DEFAULT 0 " +
                ");";

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
        public static final String COLUMN_IMAGE_URL = "image_url";
        public static final String COLUMN_LANG = "LANG";
        public static final String COLUMN_THEME_CODES = "THEME_CODES";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                "( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_NUM_OF_WORDS + " INTEGER DEFAULT 0, " +
                COLUMN_IMAGE_URL + " TEXT," +
                COLUMN_VISIBILITY + " INTEGER DEFAULT " + VISIBLE + ", " +
                COLUMN_LANG + " TEXT ," +
                COLUMN_THEME_CODES + " STRING ," +
                COLUMN_STATUS + " INTEGER DEFAULT " + OUT_OF_DICTIONARY + ");";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class Themes implements BaseColumns{
        public static final String THEME_ANY = "ANY_THEME";
        public static final String TABLE_NAME = "THEMES_TABLE";
        public static final String COLUMN_NAME = "THEME_NAME";
        public static final String COLUMN_CODE = "THEME_COD";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                "( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_CODE + " INTEGER UNIQUE ON CONFLICT REPLACE" +
                ");";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class WordLinks implements BaseColumns{
        public static final String TABLE_NAME = "WORD_IDS_TABLE";
        public static final String COLUMN_SET_ID = "SET_ID";
        public static final String COLUMN_WORD_ID = "WORD_ID";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                "( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_SET_ID + " INTEGER, " +
                COLUMN_WORD_ID + " INTEGER, " +
                "FOREIGN KEY ("+COLUMN_WORD_ID+") REFERENCES "+Words.TABLE_NAME+"("+Words._ID+"), " +
                "FOREIGN KEY ("+COLUMN_SET_ID+") REFERENCES "+Sets.TABLE_NAME+"("+Sets._ID+")" +
                ");";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static abstract class Info {
        public static final String ALL_WORDS_COUNT = "all_words";
        public static final String DICTIONARY_WORDS_COUNT = "dictionary_words";
        public static final String STUDIED_WORDS_COUNT = "studied_words";
    }
}
