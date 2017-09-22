package xyz.volgoak.wordlearning;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;

import xyz.volgoak.wordlearning.services.ImageLoaderService;
import xyz.volgoak.wordlearning.services.SetsLoaderService;
import xyz.volgoak.wordlearning.utils.SetsLoader;
import xyz.volgoak.wordlearning.utils.SetsUpdatingInfo;

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


    private static long SEC_IN_TWO_DAYS = 60 * 60 * 24 * 2;
    private static long ONE_HOUR_WINDOW = 60 * 60;
    private static long MILLIS_IN_ONE_MINUT = 60;

    public static final String PREFERENCE_BASE_LOADED = "base_loaded";

    public WordsApp(){
        sInstance = this;
    }

    public static Context getContext(){
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        GcmNetworkManager networkManager = GcmNetworkManager.getInstance(this);
        Task task = new  PeriodicTask.Builder()
                .setService(SetsLoaderService.class)
                .setTag(SetsLoaderService.TASK_CHECK_UPDATE)
                .setPeriod(SEC_IN_TWO_DAYS)
                .setFlex(ONE_HOUR_WINDOW)
                .setUpdateCurrent(false)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .build();

        networkManager.schedule(task);

        //load default database if not loaded yet
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean baseLoaded = preferences.getBoolean(PREFERENCE_BASE_LOADED, false);
        if(!baseLoaded){
            SetsUpdatingInfo info = SetsLoader.loadStartBase(this);
            boolean successfulyLoaded = info.isUpdatingSuccess();
            preferences.edit().putBoolean(PREFERENCE_BASE_LOADED, successfulyLoaded).apply();
            if(successfulyLoaded) startImagesLoading();
        }
    }

    private void startImagesLoading(){
        GcmNetworkManager manager = GcmNetworkManager.getInstance(this);
        Task task = new OneoffTask.Builder()
                .setExecutionWindow(0, 10)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .setService(ImageLoaderService.class)
                .setTag(ImageLoaderService.TASK_LOAD_ALL_IMAGES)
                .build();

        manager.schedule(task);
    }
}
