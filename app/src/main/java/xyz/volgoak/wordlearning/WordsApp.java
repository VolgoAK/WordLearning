package xyz.volgoak.wordlearning;

import android.app.Application;
import android.content.Context;

/**
 * Created by Volgoak on 15.05.2017.
 */

/**
 * Keeps app context to pass it where it need.
 * Do not use it with classes which interact with UI.
 * As far as I know it's only good enough for SQLite
 * and TTS.
 */
public class WordsApp extends Application{
    private static WordsApp sInstance;

    public WordsApp(){
        sInstance = this;
    }

    public static Context getContext(){
        return sInstance;
    }
}
