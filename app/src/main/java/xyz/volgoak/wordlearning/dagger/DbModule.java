package xyz.volgoak.wordlearning.dagger;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.attiladroid.data.DataProvider;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;



/**
 * Created by alex on 1/5/18.
 */

@Module
public class DbModule {

    private Application application;

    public DbModule(Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public DataProvider getDataProvider() {
        return DataProvider.Companion.newInstance(application);
    }
}
