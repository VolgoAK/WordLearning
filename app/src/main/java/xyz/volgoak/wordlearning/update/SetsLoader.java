package xyz.volgoak.wordlearning.update;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.attiladroid.data.DataProvider;
import com.attiladroid.data.DatabaseContract;
import com.attiladroid.data.entities.Dictionary;
import com.attiladroid.data.entities.Link;
import com.attiladroid.data.entities.Theme;
import com.attiladroid.data.entities.Word;
import com.attiladroid.data.entities.Set;
import com.crashlytics.android.Crashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.List;




/**
 * Created by Alexander Karachev on 07.05.2017.
 */

/**
 * This class is using only for creating sqlite database on debug devices.
 * It isn' well optimized  and shouldn't be.
 * !!!Run this shit only on debug devices and never use it in production!!!
 */
public final class SetsLoader {

    public static final String TAG = "SetsLoader";
    public static final String DATA_ID_ATTR = "data_id";
    public static final String DATA_SOURCE_ATTR = "source";

    public static final String LOADED_SETS_PREF = "loaded_sets";

    private SetsLoader() {
        throw new AssertionError();
    }

    public static void insertTestBase(DataProvider dataProvider, Context context) {

        dataProvider.insertWord(new Word("Hello", "Привет"));
        dataProvider.insertWord(new Word("Name", "Имя"));
        dataProvider.insertWord(new Word("Human", "Человек"));
        dataProvider.insertWord(new Word("He", "Он"));
        dataProvider.insertWord(new Word("She", "Она"));
        dataProvider.insertWord(new Word("Where", "Где"));
        dataProvider.insertWord(new Word("When", "Когда"));
        dataProvider.insertWord(new Word("Why", "Почему"));
        dataProvider.insertWord(new Word("Who", "Кто"));
        dataProvider.insertWord(new Word("What", "Что"));
        dataProvider.insertWord(new Word("Time", "Время"));
        dataProvider.insertWord(new Word("Country", "Страна"));

        try {
            InputStream inputStream = context.getAssets().open("my.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);

            String json = new String(buffer);
            createAndInsertDictionary(dataProvider, json);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static SetsUpdatingInfo createAndInsertDictionary(DataProvider provider, String dictionaryString) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        gsonBuilder.disableHtmlEscaping();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();

        Dictionary dictionary = gson.fromJson(dictionaryString, Dictionary.class);

        return insertSetsIntoDb(provider, dictionary);
    }

    public static SetsUpdatingInfo insertSetsIntoDb(DataProvider provider, Dictionary dictionary) {
        SetsUpdatingInfo info = new SetsUpdatingInfo();
        List<Theme> themes = dictionary.getThemes();

        if (themes != null && themes.size() != 0) {
            Theme[] themesArray = new Theme[themes.size()];
            provider.insertThemes(themes.toArray(themesArray));
        }

        List<Set> sets = dictionary.getSets();
        for (Set set : sets) {
            set.setVisibitity(DatabaseContract.Sets.VISIBLE);

            long setId = provider.insertSet(set);
            info.onSetAdded(setId);
            List<Word> words = set.getWords();

            for (Word word : words) {
                info.incrementWordsAdded();
                Word dictionaryWord = provider.getWord(word.getWord(), word.getTranslation());
                long wordId;
                if (dictionaryWord != null) {
                    wordId = dictionaryWord.getId();
                } else {
                    word.setTranslation(capitalize(word.getTranslation()));
                    word.setWord(capitalize(word.getWord()));
                    wordId = provider.insertWord(word);
                }
                Link link = new Link();
                link.setWordId(wordId);
                link.setIdOfSet(setId);
                provider.insertLink(link);
            }
        }

        return info;
    }

    private static String capitalize(String string) {
        String s = string.substring(0, 1).toUpperCase();
        String s2 = s + string.substring(1);
        return s2;
    }

    public static void exportDbToFile(Context context, String dbName) {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String currentDBPath = context.getDatabasePath(dbName).toString();
        String backupDBPath = dbName;
        File currentDB = new File(currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(context, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean importDbFromAsset(Context context, String dbName) {
        InputStream dbIs = null;
        FileOutputStream dbOus = null;
        File dbFileTarget = context.getDatabasePath(dbName);
        File data = Environment.getDataDirectory();
        try {
            dbFileTarget.getParentFile().mkdirs();
            dbOus = new FileOutputStream(dbFileTarget);
            dbIs = context.getAssets().open(dbName);

            byte[] buffer = new byte[1024];
            int read = dbIs.read(buffer);

            while (read != -1) {
                dbOus.write(buffer);
                read = dbIs.read(buffer);
            }

            dbIs.close();
            dbOus.flush();
            dbOus.close();

        } catch (IOException ex) {
            ex.printStackTrace();
            Crashlytics.logException(ex);
        }

        return dbFileTarget.exists();
    }
}
