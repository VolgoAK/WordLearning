package xyz.volgoak.wordlearning.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import xyz.volgoak.wordlearning.entities.Link;
import xyz.volgoak.wordlearning.entities.Set;
import xyz.volgoak.wordlearning.entities.Theme;
import xyz.volgoak.wordlearning.entities.Word;

/**
 * Created by alex on 1/4/18.
 */

@Database(entities = {Word.class, Set.class, Link.class, Theme.class},
    version = 16, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase{
    public abstract WordDao wordDao();
}
