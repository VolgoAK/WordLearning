package com.attiladroid.data;

import android.provider.BaseColumns;


/**
 * Created by Alexander Karachev on 11.04.2017.
 */

public final class DatabaseContract {

    public final static String DB_NAME = "NEW_WORDS_DATABASE";


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
    }

    public static abstract class Themes implements BaseColumns{
        public static final String THEME_ANY = "";
        public static final String TABLE_NAME = "THEMES_TABLE";
        public static final String COLUMN_NAME = "THEME_NAME";
        public static final String COLUMN_CODE = "THEME_COD";
    }

    public static abstract class WordLinks implements BaseColumns{
        public static final String TABLE_NAME = "WORD_IDS_TABLE";
        public static final String COLUMN_SET_ID = "SET_ID";
        public static final String COLUMN_WORD_ID = "WORD_ID";
    }

    public static abstract class Info {
        public static final String ALL_WORDS_COUNT = "all_words";
        public static final String DICTIONARY_WORDS_COUNT = "dictionary_words";
        public static final String STUDIED_WORDS_COUNT = "studied_words";
    }
}
