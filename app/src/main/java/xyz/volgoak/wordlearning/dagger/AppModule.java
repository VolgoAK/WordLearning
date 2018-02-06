package xyz.volgoak.wordlearning.dagger;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import xyz.volgoak.wordlearning.WordsApp;
import xyz.volgoak.wordlearning.utils.SoundsManager;

/**
 * Created by alex on 1/8/18.
 */

@Module
public class AppModule {

    Application application;
    SoundsManager soundsManager;

    public AppModule(WordsApp app) {
        application = app;
    }

    @Provides
    @Singleton
    public Context getApplicationContext() {
        return application;
    }

    @Provides
    @Singleton
    public SoundsManager getSoundsManager() {
        if (soundsManager == null) soundsManager = new SoundsManager();
        return soundsManager;
    }
}
