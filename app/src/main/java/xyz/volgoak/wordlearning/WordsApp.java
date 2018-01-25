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
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import xyz.volgoak.wordlearning.dagger.AppModule;
import xyz.volgoak.wordlearning.dagger.DaggerDbComponent;
import xyz.volgoak.wordlearning.dagger.DbComponent;
import xyz.volgoak.wordlearning.dagger.DbModule;
import xyz.volgoak.wordlearning.dagger.DownloaderModule;
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.services.SetsLoaderService;
import xyz.volgoak.wordlearning.update.DbUpdateManager;
import xyz.volgoak.wordlearning.update.ImageDownloader;
import xyz.volgoak.wordlearning.update.SetsLoader;
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

    private static WordsApp sInstance;
    private static DbComponent sComponent;

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
        sComponent = DaggerDbComponent.builder().dbModule(new DbModule(this))
                .appModule(new AppModule(this)).downloaderModule(new DownloaderModule()).build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sComponent.inject(this);

        FirebaseAuth.getInstance().signInAnonymously();
        DbUpdateManager.manageDbState(this, dataProvider);
    }

    @Override
    public void onTerminate() {
        WordSpeaker.close();
        super.onTerminate();
    }
}
