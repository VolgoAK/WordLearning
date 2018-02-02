package xyz.volgoak.wordlearning.update;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import xyz.volgoak.wordlearning.BuildConfig;
import xyz.volgoak.wordlearning.Config;
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.services.SetsLoaderService;
import xyz.volgoak.wordlearning.utils.PreferenceContract;

/**
 * Created by alex on 1/26/18.
 */

public class DbUpdateManager {

    public static final String TAG = DbUpdateManager.class.getSimpleName();

    private static long SEC_IN_TWO_DAYS = 60 * 60 * 24 * 2;
    private static long ONE_HOUR_WINDOW = 60 * 60;
    private static long MILLIS_IN_ONE_MINUT = 60;

    // TODO: 2/2/18 Inject image downloader

    public static void manageDbState(Context context, DataProvider provider) {

        //load default database if not loaded yet
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        boolean baseLoaded = preferences.getBoolean(PreferenceContract.BASE_CREATED, false);
        if (!baseLoaded) {
            if (Config.IMPORT_PREBUILT_DB) {
                baseLoaded = SetsLoader.importDbFromAsset(context, DatabaseContract.DB_NAME);
            } else {
                baseLoaded = true;
                SetsLoader.insertTestBase(provider, context);
            }
            preferences.edit().putBoolean(PreferenceContract.BASE_CREATED, baseLoaded).apply();
        }

        if (Config.EXPORT_DB) {
            SetsLoader.exportDbToFile(context, DatabaseContract.DB_NAME);
        }

        if (!Config.SCHEDULE_UPDATE) return;
        //check auth and download images and new sets
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Log.d(TAG, "manageDbState: " + user.toString());
            scheduleUpdateTasks(context);
        } else {
            FirebaseAuth.getInstance().signInAnonymously();
            FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() != null) {
                        scheduleUpdateTasks(context);
                        firebaseAuth.removeAuthStateListener(this);
                    }
                }
            });
        }
    }

    // Firebase downloads and update tasks
    private static void scheduleUpdateTasks(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!preferences.getBoolean(PreferenceContract.IMAGES_LOADED, false)) {
            new ImageDownloader().downloadImages();
        }

        GcmNetworkManager networkManager = GcmNetworkManager.getInstance(context);
        //run updating as soon as possible at first launch and after update
        int currentVersion = BuildConfig.VERSION_CODE;

        if (preferences.getInt(PreferenceContract.LAST_VERSION, 0) < currentVersion) {

            Task task = new OneoffTask.Builder()
                    .setService(SetsLoaderService.class)
                    .setExecutionWindow(0, 30)
                    .setTag(SetsLoaderService.TASK_CHECK_UPDATE_SETS)
                    .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                    .build();

            networkManager.schedule(task);
            preferences.edit().putInt(PreferenceContract.LAST_VERSION, currentVersion).apply();
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
