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
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;

import static android.R.attr.name;
import static xyz.volgoak.wordlearning.R.string.sets;
import static xyz.volgoak.wordlearning.R.string.word;

/**
 * Created by Volgoak on 18.04.2017.
 */

public final class SetsLoader {

    public static final String TAG = "SetsLoader";
    public static final String DATA_SET_NODE = "Data_set";
    public static final String DATA_ID_ATTR = "data_id";
    public static final String DATA_SOURCE_ATTR = "source";
    public static final String SET_NODE = "Set";
    public static final String NAME_ATTR = "name";
    public static final String WORD_ATTR = "word";
    public static final String TRANSLATION_ATTR = "translation";
    public static final String DESCRIPTION_ATTR = "description";
    public static final String TRANSCRIPTION_ATTR = "transcription";
    public static final String IMAGE_ATTR = "image";

    public static final String LOADED_SETS_PREF = "loaded_sets";

    private SetsLoader(){
        throw new AssertionError();
    }

    public static void loadSets(File xmlFile){

    }

    public static void checkForDbUpdate(final Context context){
        // TODO: 21.05.2017 store reference in resources
        StorageReference indexRef = FirebaseStorage.getInstance().getReference("sets_index.xml");
            indexRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    try {
                        check(bytes, context);
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                }
            });

    }

    // TODO: 21.05.2017 add check and question to load new data
    private static void check(byte[] bytes, Context context) throws Exception{
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> loadedData = preferences.getStringSet(LOADED_SETS_PREF, new HashSet<String>());

        for(String added : loadedData){
            Log.d(TAG, "check: already loaded " + added);
        }

        Document doc = prepareDocument(bytes);
        NodeList sets = doc.getElementsByTagName(DATA_SET_NODE);
        for(int a = 0; a < sets.getLength(); a++){
            Node n = sets.item(a);

            String dataId = n.getAttributes().getNamedItem(DATA_ID_ATTR).getNodeValue();
            String setSource = n.getAttributes().getNamedItem(DATA_SOURCE_ATTR).getNodeValue();

            if(!loadedData.contains(dataId)){
                Log.d(TAG, "check: load data set " + dataId);
                loadDataByFileName(setSource, dataId, context);
            }
        }
    }

    private static void loadDataByFileName(String fileName, final String fileId, final Context context){
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(fileName);

        storageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    Document doc = prepareDocument(bytes);
                    if(insertSetsIntoDb(doc) > 0)
                        addSuccessPreference(fileId, context);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });
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
     * Loads words from xml files. File names hardcoded for now,
     * later I gonna add checking which sets already downloaded,
     * and possibility to load new sets from my server.
     * @param context Context for operations.
     * @return int num of added words.
     * */
    public static int loadStartBase(Context context){
        int addedWords = 0;
        try {
            InputStream inputStream = context.getAssets().open("nature.xml");
            addedWords += insertSetsIntoDb(prepareDocument(inputStream));
            inputStream = context.getAssets().open("things.xml");
            addedWords += insertSetsIntoDb(prepareDocument(inputStream));
            inputStream = context.getAssets().open("verbs.xml");
            addedWords += insertSetsIntoDb(prepareDocument(inputStream));
            inputStream = context.getAssets().open("new_base.xml");
            addedWords += insertSetsIntoDb(prepareDocument(inputStream));
        }catch(IOException ex){
            ex.printStackTrace();
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return addedWords;
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

    private static int insertSetsIntoDb(Document document){
        int addedSetsCount = 0;
        int addedWordsCount = 0;

        WordsDbAdapter dbAdapter = new WordsDbAdapter();

            Element rootElement = document.getDocumentElement();
            rootElement.normalize();

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
                            Node node = setNode.getAttributes().getNamedItem(TRANSCRIPTION_ATTR);
                            if(node != null)
                            transcription = setNode.getAttributes()
                                    .getNamedItem(TRANSCRIPTION_ATTR).getNodeValue().trim();

                            ContentValues values = new ContentValues();
                            values.put(DatabaseContract.Words.COLUMN_WORD, word);
                            values.put(DatabaseContract.Words.COLUMN_TRANSLATION, translation);
                            values.put(DatabaseContract.Words.COLUMN_TRANSCRIPTION, transcription);

                            valuesList.add(values);
                        }
                    }

                    String setName = setNode.getAttributes().getNamedItem(NAME_ATTR).getNodeValue();
                    String description = setNode.getAttributes().getNamedItem(DESCRIPTION_ATTR).getNodeValue();
                    String image = setNode.getAttributes().getNamedItem(IMAGE_ATTR).getNodeValue();
                    int wordsInSet = valuesList.size();

                    ContentValues setValues = new ContentValues();
                    setValues.put(DatabaseContract.Sets.COLUMN_NAME, setName);
                    setValues.put(DatabaseContract.Sets.COLUMN_DESCRIPTION, description);
                    setValues.put(DatabaseContract.Sets.COLUMN_NUM_OF_WORDS, wordsInSet);
                    setValues.put(DatabaseContract.Sets.COLUMN_IMAGE_URL, image);

                    long setId = dbAdapter.insertSet(setValues);
                    addedSetsCount++;


                    for(ContentValues values : valuesList){
                        values.put(DatabaseContract.Words.COLUMN_SET_ID, setId);
                        dbAdapter.insertWord(values);
                        addedWordsCount++;
                    }
                }
            }

            Log.d(TAG, "added sets " + addedSetsCount);
            Log.d(TAG, "added words " + addedWordsCount);
            return addedWordsCount;
    }

    private static String capitalize(String string){
        String s = string.substring(0, 1).toUpperCase();
        String s2 = s + string.substring(1);
        return s2;
    }
}
