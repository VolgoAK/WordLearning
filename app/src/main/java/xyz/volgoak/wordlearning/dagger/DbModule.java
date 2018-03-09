package xyz.volgoak.wordlearning.dagger;

import android.app.Application;
import android.arch.persistence.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import xyz.volgoak.wordlearning.data.AppDatabase;
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.InfoDao;
import xyz.volgoak.wordlearning.data.LinkDao;
import xyz.volgoak.wordlearning.data.SetsDao;
import xyz.volgoak.wordlearning.data.ThemeDao;
import xyz.volgoak.wordlearning.data.WordDao;

/**
 * Created by alex on 1/5/18.
 */

@Module
public class DbModule {

    private AppDatabase appDatabase;

    public DbModule(Application application) {
        appDatabase = Room.databaseBuilder(application, AppDatabase.class, DatabaseContract.DB_NAME)
                .build();
    }

    @Provides
    @Singleton
    public InfoDao getInfoDao() {
        return appDatabase.getInfoDao();
    }

    @Provides
    @Singleton
    public LinkDao getLinkDao() {
        return appDatabase.getLinkDao();
    }

    @Provides
    @Singleton
    public SetsDao getSetsDao() {
        return appDatabase.getSetsDao();
    }

    @Provides
    @Singleton
    public ThemeDao getThemeDao() {
        return appDatabase.getThemeDao();
    }

    @Provides
    @Singleton
    public WordDao getWordDao() {
        return appDatabase.getWordDao();
    }

    @Provides
    @Singleton
    public DataProvider getDataProvider(InfoDao infoDao, LinkDao linkDao, SetsDao setsDao,
                                        ThemeDao themeDao, WordDao wordDao) {
        return new DataProvider(infoDao, linkDao, setsDao, themeDao, wordDao);
    }
}
