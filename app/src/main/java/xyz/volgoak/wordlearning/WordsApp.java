package xyz.volgoak.wordlearning;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;

import javax.inject.Inject;

import xyz.volgoak.wordlearning.dagger.DaggerDbComponent;
import xyz.volgoak.wordlearning.dagger.DbComponent;
import xyz.volgoak.wordlearning.dagger.DbModule;
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.services.SetsLoaderService;
import xyz.volgoak.wordlearning.utils.SetsLoader;
import xyz.volgoak.wordlearning.utils.SetsUpdatingInfo;
import xyz.volgoak.wordlearning.utils.WordSpeaker;

/**
 * Created by Volgoak on 15.05.2017.
 */

/**
 * Keeps app context to pass it where it need.
 * Do not use it with classes which interact with UI.
 * As far as I know it's only good enough for SQLite
 * and TTS.
 */
public class WordsApp extends Application {
    public static final String TAG = WordsApp.class.getSimpleName();
    public static final String PREFERENCE_BASE_LOADED = "base_loaded";
    public static final String PREFERENCE_LAST_VERSION = "last_app_version";
    public static final String THEME_ISSUE_FIXED = "theme_issue_fixed";
    private static WordsApp sInstance;
    private static DbComponent sComponent;

    private static long SEC_IN_TWO_DAYS = 60 * 60 * 24 * 2;
    private static long ONE_HOUR_WINDOW = 60 * 60;
    private static long MILLIS_IN_ONE_MINUT = 60;

    @Inject
    DataProvider dataProvider;

    public WordsApp() {
        sInstance = this;
        initComponent();
    }

    public static Context getContext() {
        return sInstance;
    }

    public static DbComponent getsComponent() {
        return sComponent;
    }

    private void initComponent() {
        sComponent = DaggerDbComponent.builder().dbModule(new DbModule(this)).build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sComponent.inject(this);

        // TODO: 21.10.2017 add insta checking at first launch

        //load default database if not loaded yet
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean baseLoaded = preferences.getBoolean(PREFERENCE_BASE_LOADED, false);
        if (!baseLoaded) {
            /*SetsUpdatingInfo info = SetsLoader.loadStartBase(this);
            Log.d(TAG, "onCreate: load db");
            boolean successfulyLoaded = info.isUpdatingSuccess();
            Log.d(TAG, "onCreate: soccess " + successfulyLoaded);
            preferences.edit().putBoolean(PREFERENCE_BASE_LOADED, successfulyLoaded).apply();
            //if app not updated but installed we don't need to fix
            preferences.edit().putBoolean(THEME_ISSUE_FIXED, true).apply();
            if (successfulyLoaded) startImagesLoading();*/

            SetsLoader.insertTestBase(dataProvider);
        }

        GcmNetworkManager networkManager = GcmNetworkManager.getInstance(this);

        //run updating as soon as possible at first launch and after update
        int currentVersion = BuildConfig.VERSION_CODE;

        if (preferences.getInt(PREFERENCE_LAST_VERSION, 0) < currentVersion) {
            // TODO: 21.10.2017 start task from here

            Task task = new OneoffTask.Builder()
                    .setService(SetsLoaderService.class)
                    .setExecutionWindow(0, 30)
                    .setTag(SetsLoaderService.TASK_CHECK_UPDATE_SETS)
                    .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                    .build();

            networkManager.schedule(task);
            preferences.edit().putInt(PREFERENCE_LAST_VERSION, currentVersion).apply();
        } else {

            //create task for updating db with 2 days period
            Task task = new PeriodicTask.Builder()
                    .setService(SetsLoaderService.class)
                    .setTag(SetsLoaderService.TASK_CHECK_UPDATE_SETS)
                    .setPeriod(SEC_IN_TWO_DAYS)
                    .setFlex(ONE_HOUR_WINDOW)
                    .setUpdateCurrent(false)
                    .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                    .build();

            networkManager.schedule(task);
        }
    }

    private void startImagesLoading() {
        GcmNetworkManager manager = GcmNetworkManager.getInstance(this);
        Task task = new OneoffTask.Builder()
                .setExecutionWindow(0, 10)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                .setService(SetsLoaderService.class)
                .setTag(SetsLoaderService.TASK_LOAD_IMAGES)
                .build();

        manager.schedule(task);
    }

    @Override
    public void onTerminate() {
        WordSpeaker.close();
        super.onTerminate();
    }
}
