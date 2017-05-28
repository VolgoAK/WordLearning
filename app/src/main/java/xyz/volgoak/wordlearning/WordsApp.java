package xyz.volgoak.wordlearning;

import android.app.Application;
import android.content.Context;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.google.firebase.auth.FirebaseAuth;

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

    public WordsApp(){
        sInstance = this;
    }

    public static Context getContext(){
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInAnonymously();

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
    }


}
