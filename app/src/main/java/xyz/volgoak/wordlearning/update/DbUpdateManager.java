package xyz.volgoak.wordlearning.update;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;

import xyz.volgoak.wordlearning.BuildConfig;
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.services.SetsLoaderService;

/**
 * Created by alex on 1/26/18.
 */

public class DbUpdateManager {

    public static final String TAG = DbUpdateManager.class.getSimpleName();

    public static final boolean EXPORT_DB = false;
    public static final boolean IMPORT_PREBUILT_DB = true;

    public static final String PREFERENCE_BASE_CREATED = "base_loaded";
    public static final String PREFERENCE_LAST_VERSION = "last_app_version";

    private static long SEC_IN_TWO_DAYS = 60 * 60 * 24 * 2;
    private static long ONE_HOUR_WINDOW = 60 * 60;
    private static long MILLIS_IN_ONE_MINUT = 60;

    public static void manageDbState(Context context, DataProvider provider) {

        //load default database if not loaded yet
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean baseLoaded = preferences.getBoolean(PREFERENCE_BASE_CREATED, false);
        if (!baseLoaded) {
            if(IMPORT_PREBUILT_DB) {
                long startTime = System.currentTimeMillis();
                SetsLoader.importDbFromAsset(context, DatabaseContract.DB_NAME);
                Log.d(TAG, "onCreate: db import in " + (System.currentTimeMillis() - startTime));
            } else {
                SetsLoader.insertTestBase(provider, context);
            }
            new ImageDownloader().checkImagesAsynk();
            // TODO: 1/26/18 Add some check is db was loaded correctly
            preferences.edit().putBoolean(PREFERENCE_BASE_CREATED, true).apply();
        }

        if (EXPORT_DB) {
            SetsLoader.exportDbToFile(context, DatabaseContract.DB_NAME);
        }

        scheduleUpdateTasks(context);
    }

    private static void scheduleUpdateTasks(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        GcmNetworkManager networkManager = GcmNetworkManager.getInstance(context);

        //run updating as soon as possible at first launch and after update
        int currentVersion = BuildConfig.VERSION_CODE;

        if (preferences.getInt(PREFERENCE_LAST_VERSION, 0) < currentVersion) {

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
}
