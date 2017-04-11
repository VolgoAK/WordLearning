package xyz.volgoak.wordlearning.data;

import android.provider.BaseColumns;

/**
 * Created by Volgoak on 11.04.2017.
 */

public final class DatabaseContract {

    public final static int DB_VERSION = 6;
    public final static String DB_NAME = "WORDS_DATABASE";


    public static abstract class Words implements BaseColumns{

        public final static String TABLE_NAME = "TABLE_NAME";

        public final static String COLUMN_WORD = "WORD";
        public final static String COLUMN_TRANSLATION = "TRANSLATION";
        public final static String COLUMN_TRAINED_WT = "WT_TRAINED";
        public final static String COLUMN_TRAINED_TW = "TW_TRAINED";
        public final static String COLUMN_STUDIED = "STUDIED";

        public final static String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                " ( " + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_WORD + " TEXT, " +
                COLUMN_TRANSLATION + " TEXT, " +
                COLUMN_TRAINED_WT + " INTEGER, " +
                COLUMN_TRAINED_TW + " INTEGER, " +
                COLUMN_STUDIED + " INTEGER);";

        public final static String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
