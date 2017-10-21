package xyz.volgoak.wordlearning.utils;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;


/**
 * Created by Alexander Karachev on 07.05.2017.
 */

public final class SetsLoader {

    public static final String TAG = "SetsLoader";
    public static final String DATA_SET_NODE = "Data_set";
    public static final String DATA_ID_ATTR = "data_id";
    public static final String DATA_SOURCE_ATTR = "source";
    public static final String SET_NODE = "Set";
    public static final String NAME_ATTR = "name";
    public static final String IMAGE_ATTR = "image";
    public static final String THEME_ATTR = "theme";

    public static final String WORD_ATTR = "word";
    public static final String TRANSLATION_ATTR = "translation";
    public static final String DESCRIPTION_ATTR = "description";
    public static final String TRANSCRIPTION_ATTR = "transcription";

    public static final String THEME_NODE = "Theme";
    public static final String THEME_NAME_ATTR = "name";
    public static final String THEME_CODE_ATTR = "code";

    public static final String LOADED_SETS_PREF = "loaded_sets";

    private SetsLoader(){
        throw new AssertionError();
    }

    public static void loadSets(File xmlFile){

    }




    public static SetsUpdatingInfo check(byte[] bytes, Context context) throws Exception{
        SetsUpdatingInfo info = new SetsUpdatingInfo();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> loadedData = preferences.getStringSet(LOADED_SETS_PREF, new HashSet<String>());

        for(String added : loadedData){
//            Log.d(TAG, "check: already loaded " + added);
        }

        Document doc = prepareDocument(bytes);
        NodeList sets = doc.getElementsByTagName(DATA_SET_NODE);
        for(int a = 0; a < sets.getLength(); a++){
            Node n = sets.item(a);

            String dataId = n.getAttributes().getNamedItem(DATA_ID_ATTR).getNodeValue();
            String setSource = n.getAttributes().getNamedItem(DATA_SOURCE_ATTR).getNodeValue();

            if(!loadedData.contains(dataId)){
                Log.d(TAG, "check: load data set " + dataId);
                info.addInfo(loadDataByFileName(setSource, dataId, context));
            }
        }

        return info;
    }

    private static SetsUpdatingInfo loadDataByFileName(String fileName, final String fileId, final Context context){
        final SetsUpdatingInfo info = new SetsUpdatingInfo();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(fileName);
        final WordsDbAdapter adapter = new WordsDbAdapter();
        storageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    adapter.beginTransaction();
                    Document doc = prepareDocument(bytes);
                    SetsUpdatingInfo setInfo = insertSetsIntoDb(doc, adapter);
                    if(setInfo.getWordsAdded() > 0){
                        addSuccessPreference(fileId, context);
                        info.addInfo(setInfo);
                        adapter.endTransaction(true);
                    }
                }catch (Exception ex){
                    ex.printStackTrace();
                }finally {
                    adapter.endTransaction(false);
                }
            }
        });

        // TODO: 19.10.2017 load image when new set loaded

        return info;
    }

    /**
     * Insert id of successfully loaded data into shared preferences
     * @param fileId id of data set
     * @param context context for access to sharedPreferences
     */
    private static synchronized void addSuccessPreference(String fileId, Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> loadedSet = preferences.getStringSet(LOADED_SETS_PREF, new HashSet<String>());
        loadedSet.add(fileId);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(LOADED_SETS_PREF, loadedSet).apply();
    }

    /**
     * Loads words from local xml files.
     * @param context Context for operations.
     * @return int num of added words.
     * */
    public static SetsUpdatingInfo loadStartBase(Context context){
        SetsUpdatingInfo info = new SetsUpdatingInfo();
        WordsDbAdapter dbAdapter = new WordsDbAdapter();
        try {
            dbAdapter.beginTransaction();
            // TODO: 22.09.2017 move this to other method, or load from xml file
            dbAdapter.insertWord("Hello", "Привет");
            dbAdapter.insertWord("Name", "Имя");
            dbAdapter.insertWord("Human", "Человек");
            dbAdapter.insertWord("He", "Он");
            dbAdapter.insertWord("She", "Она");
            dbAdapter.insertWord("Where", "Где");
            dbAdapter.insertWord("When", "Когда");
            dbAdapter.insertWord("Why", "Почему");
            dbAdapter.insertWord("Who", "Кто");
            dbAdapter.insertWord("What", "Что");
            dbAdapter.insertWord("Time", "Время");
            dbAdapter.insertWord("Country", "Страна");
            InputStream inputStream = context.getAssets().open("start_base.xml");
            info.addInfo( insertSetsIntoDb(prepareDocument(inputStream), dbAdapter));
            dbAdapter.endTransaction(true);
            info.setUpdatingSuccess(true);
        }catch(IOException ex){
            ex.printStackTrace();
            info.setUpdatingSuccess(false);
        }catch(Exception ex){
            ex.printStackTrace();
        }finally {
            dbAdapter.endTransaction(false);
        }
        return info;
    }

    private static Document prepareDocument(byte[] bytes)
            throws SAXException, IOException, ParserConfigurationException{
        return prepareDocument(new ByteArrayInputStream(bytes));
    }

    private static Document prepareDocument(InputStream is)
            throws SAXException, IOException, ParserConfigurationException{
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(is);

        return doc;
    }

    private static SetsUpdatingInfo insertSetsIntoDb(Document document, WordsDbAdapter dbAdapter){
        SetsUpdatingInfo info = new SetsUpdatingInfo();

            Element rootElement = document.getDocumentElement();
            rootElement.normalize();

            //parse themes
            NodeList themeList = rootElement.getElementsByTagName(THEME_NODE);
            for(int a = 0; a < themeList.getLength(); a++){
                Node themeNode = themeList.item(a);
                String name = themeNode.getAttributes().getNamedItem(THEME_NAME_ATTR).getNodeValue();
                String codeString = themeNode.getAttributes().getNamedItem(THEME_CODE_ATTR).getNodeValue();
                int code = Integer.parseInt(codeString);

                ContentValues themeValues = new ContentValues();
                themeValues.put(DatabaseContract.Themes.COLUMN_NAME, name);
                themeValues.put(DatabaseContract.Themes.COLUMN_CODE, code);
                dbAdapter.insertTheme(themeValues);
            }

            NodeList setsList = rootElement.getElementsByTagName(SET_NODE);
            Log.d(TAG, "insertSetsIntoDb: sets in doc" + setsList.getLength());

            //parse sets and insert them into database
            for(int a = 0; a < setsList.getLength(); a++){
                Node setNode = setsList.item(a);
                if(setNode.getNodeType() == Node.ELEMENT_NODE){

                    List<ContentValues> valuesList = new LinkedList<>();
                    //parse words and save into map
                    NodeList wordsList = setNode.getChildNodes();
                    for(int b = 0; b < wordsList.getLength(); b++){
                        Node wordNode = wordsList.item(b);
                        if(wordNode.getNodeType() == Node.ELEMENT_NODE){
                            String word = capitalize(wordNode.getAttributes()
                                    .getNamedItem(WORD_ATTR).getNodeValue().trim());
                            String translation = capitalize(wordNode.getAttributes()
                                    .getNamedItem(TRANSLATION_ATTR).getNodeValue().trim());

                            //transcription could be empty for some words
                            String transcription = "";
                            Node node = wordNode.getAttributes().getNamedItem(TRANSCRIPTION_ATTR);
                            if(node != null)
                            transcription = node.getNodeValue();


                            ContentValues values = new ContentValues();
                            values.put(DatabaseContract.Words.COLUMN_WORD, word);
                            values.put(DatabaseContract.Words.COLUMN_TRANSLATION, translation);
                            values.put(DatabaseContract.Words.COLUMN_TRANSCRIPTION, transcription);

                            valuesList.add(values);
                        }
                    }

                    //parse set fields
                    String setName = setNode.getAttributes().getNamedItem(NAME_ATTR).getNodeValue();
                    String description = setNode.getAttributes().getNamedItem(DESCRIPTION_ATTR).getNodeValue();
                    String image = setNode.getAttributes().getNamedItem(IMAGE_ATTR).getNodeValue();
                    int wordsInSet = valuesList.size();

                    String theme = null;
                    Node themeNode = setNode.getAttributes().getNamedItem(THEME_ATTR);
                    if(themeNode != null)
                        theme = themeNode.getNodeValue();

                    ContentValues setValues = new ContentValues();
                    setValues.put(DatabaseContract.Sets.COLUMN_NAME, setName);
                    setValues.put(DatabaseContract.Sets.COLUMN_DESCRIPTION, description);
                    setValues.put(DatabaseContract.Sets.COLUMN_NUM_OF_WORDS, wordsInSet);
                    setValues.put(DatabaseContract.Sets.COLUMN_IMAGE_URL, image);
                    if(theme != null) setValues.put(DatabaseContract.Sets.COLUMN_THEME_CODE, Integer.parseInt(theme));

                    long setId = dbAdapter.insertSet(setValues);
                    info.incrementSetsAdded();

                    //insert words from values list
                    for(ContentValues values : valuesList){

                        dbAdapter.insertLink(values, setId);
                        info.incrementWordsAdded();
                    }
                }
            }

            Log.d(TAG, "added sets " + info.getSetsAdded());
            Log.d(TAG, "added words " + info.getWordsAdded());
            return info;
    }

    private static String capitalize(String string){
        String s = string.substring(0, 1).toUpperCase();
        String s2 = s + string.substring(1);
        return s2;
    }
}
