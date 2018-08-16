package com.attiladroid.data;

import android.provider.BaseColumns;


/**
 * Created by Alexander Karachev on 11.04.2017.
 */

public final class DataContract {

    public final static String DB_NAME = "NEW_WORDS_DATABASE";
    public static final int DB_VERSION = 17;

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

    public static abstract class Preference {
        public static final String BOOL_RECORD = "record_boolean";

        public static final String BASE_CREATED = "base_created";
        public static final String LAST_VERSION = "last_app_version";

        public static final String IMAGES_LOADED = "images_loaded";

        public static final String SOUNDS_ENABLED = "sounds_enabled_pref";
        public static final String AUTO_PLAY_PRONOUN = "auto_play_pronoun_pref";
        public static final String WORDS_IN_TRAINING = "words_in_training";

        public static final String DONT_SHOW_RATE_DIALOG = "dont_show_rate";
        public static final String LAST_RATE_SHOW_TIME = "last_show_time";
        public static final String DATE_FIRST_LAUNCH = "date_first_launch";
        public static final String LAUNCH_COUNT = "launch_times_count";
    }

    public static abstract class Firebase {
        public static final String IMAGES_FOLDER = "/images";
        public static final String IMAGES_W_50_FOLDER = IMAGES_FOLDER + "/w_50";
        public static final String IMAGES_W_400_FOLDER = IMAGES_FOLDER + "/w_400";

        public static final String PHOTOS_ARCHIVE = "photos.zip";
    }

    public static abstract class Storage {
        public static final String IMAGES_FOLDER = "/images";
        public static final String IMAGES_W_50_FOLDER = IMAGES_FOLDER + "/w_50";
        public static final String IMAGES_W_400_FOLDER = IMAGES_FOLDER + "/w_400";

        public static final String PHOTOS_ARCHIVE = "photos.zip";
    }
}
