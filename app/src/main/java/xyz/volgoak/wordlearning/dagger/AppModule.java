package xyz.volgoak.wordlearning.dagger;

import android.app.Application;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import xyz.volgoak.wordlearning.WordsApp;

/**
 * Created by alex on 1/8/18.
 */

@Module
public class AppModule {

    Application application;

    public AppModule(WordsApp app) {
        application = app;
    }

    @Provides
    @Singleton
    public Context getApplicationContext() {
        return application;
    }
}
