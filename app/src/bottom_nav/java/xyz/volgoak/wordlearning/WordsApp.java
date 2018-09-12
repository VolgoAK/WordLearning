package xyz.volgoak.wordlearning;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;

import com.attiladroid.data.DataProvider;
import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;
import xyz.volgoak.wordlearning.dagger.AppModule;


import xyz.volgoak.wordlearning.dagger.DaggerDbComponent;
import xyz.volgoak.wordlearning.dagger.DbComponent;
import xyz.volgoak.wordlearning.dagger.DbModule;
import com.attiladroid.data.update_managment.DbUpdateManager;
import xyz.volgoak.wordlearning.admob.AdsManager;
import xyz.volgoak.wordlearning.utils.ReleaseTree;
import xyz.volgoak.wordlearning.utils.WordSpeaker;

public class WordsApp extends Application {
    public static final String TAG = WordsApp.class.getSimpleName();

    private static WordsApp sInstance;
    private static DbComponent sComponent;

    @Inject
    DataProvider dataProvider;

    public WordsApp() {
        sInstance = this;
        initComponent();
        //check gitdd
    }

    public static Context getContext() {
        return sInstance;
    }

    public static DbComponent getsComponent() {
        return sComponent;
    }

    private void initComponent() {
        sComponent = DaggerDbComponent.builder().dbModule(new DbModule(this))
                .appModule(new AppModule(this)).build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sComponent.inject(this);

        FirebaseAuth.getInstance().signInAnonymously();

        AsyncTask.execute(() -> DbUpdateManager.INSTANCE.manageDbState(this, dataProvider));


        if(BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ReleaseTree());
            Fabric.with(this, new Crashlytics());
            FirebaseAnalytics.getInstance(this).setAnalyticsCollectionEnabled(true);
        }

       /* Bundle bundle = new Bundle();
        bundle.putString("Test", "Test is disabling work");
        bundle.putBoolean("Release", BuildConfig.DEBUG);
        FirebaseAnalytics.getInstance(this).logEvent("Test", bundle);
        */

        AdsManager.INSTANCE.initAds(this);
    }

    @Override
    public void onTerminate() {
        WordSpeaker.close();
        super.onTerminate();
    }
}
