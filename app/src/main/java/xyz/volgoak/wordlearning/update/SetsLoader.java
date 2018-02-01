package xyz.volgoak.wordlearning.update;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.entities.Dictionary;
import xyz.volgoak.wordlearning.entities.Link;
import xyz.volgoak.wordlearning.entities.Set;
import xyz.volgoak.wordlearning.entities.Theme;
import xyz.volgoak.wordlearning.entities.Word;


/**
 * Created by Alexander Karachev on 07.05.2017.
 */

public final class SetsLoader {

    public static final String TAG = "SetsLoader";
    public static final String DATA_SET_NODE = "Data_set";
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
            int syze = inputStream.available();
            byte[] buffer = new byte[syze];
            inputStream.read(buffer);

            String json = new String(buffer);

            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.excludeFieldsWithoutExposeAnnotation();
            gsonBuilder.disableHtmlEscaping();
            gsonBuilder.setPrettyPrinting();
            Gson gson = gsonBuilder.create();

            Dictionary dictionary = gson.fromJson(json, Dictionary.class);

            insertSetsIntoDb(dataProvider, dictionary);

        }catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    static Document prepareDocument(byte[] bytes)
            throws SAXException, IOException, ParserConfigurationException {
        return prepareDocument(new ByteArrayInputStream(bytes));
    }

    static Document prepareDocument(InputStream is)
            throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(is);

        return doc;
    }

    static SetsUpdatingInfo insertSetsIntoDb(DataProvider provider, Dictionary dictionary) {
        List<Theme> themes = dictionary.getThemes();
        Theme[] themesArray = new Theme[themes.size()];
        provider.insertThemes(themes.toArray(themesArray));

        List<Set> sets = dictionary.getSets();
        for(Set set : sets) {
            set.setVisibitity(DatabaseContract.Sets.VISIBLE);

            // TODO: 1/7/18 manage links
            long setId = provider.insertSet(set);
            List<Word> words = set.getWords();

            for(Word word : words) {
                Word dictionaryWord = provider.getWord(word.getWord(), word.getTranslation());
                long wordId;
                if(dictionaryWord != null) {
                    wordId = dictionaryWord.getId();
                } else {
                    wordId = provider.insertWord(word);
                }
                Link link = new Link();
                link.setWordId(wordId);
                link.setIdOfSet(setId);
                provider.insertLink(link);
            }
        }

        return new SetsUpdatingInfo();
    }

    private static String capitalize(String string) {
        String s = string.substring(0, 1).toUpperCase();
        String s2 = s + string.substring(1);
        return s2;
    }

    public static void exportDbToFile(Context context, String dbName) {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source=null;
        FileChannel destination=null;
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
        } catch(IOException e) {
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
        }

        return dbFileTarget.exists();
    }
}
