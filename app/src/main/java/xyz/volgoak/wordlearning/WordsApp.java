package xyz.volgoak.wordlearning;

import android.app.Application;
import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import timber.log.Timber;
import xyz.volgoak.wordlearning.dagger.AppModule;
import xyz.volgoak.wordlearning.dagger.DaggerDbComponent;
import xyz.volgoak.wordlearning.dagger.DbComponent;
import xyz.volgoak.wordlearning.dagger.DbModule;
import xyz.volgoak.wordlearning.dagger.DownloaderModule;
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.update.DbUpdateManager;
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
                .appModule(new AppModule(this)).downloaderModule(new DownloaderModule()).build();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sComponent.inject(this);

        FirebaseAuth.getInstance().signInAnonymously();
        DbUpdateManager.manageDbState(this, dataProvider);

        if(BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());
    }

    @Override
    public void onTerminate() {
        WordSpeaker.close();
        super.onTerminate();
    }
}
