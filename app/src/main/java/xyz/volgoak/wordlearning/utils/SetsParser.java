package xyz.volgoak.wordlearning.utils;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;

/**
 * Created by Volgoak on 18.04.2017.
 */

public final class SetsParser {

    public static final String TAG = "SetsParser";

    public static final String SET_NODE = "Set";
    public static final String NAME_ATTR = "name";
    public static final String WORD_ATTR = "word";
    public static final String TRANSLATION_ATTR = "translation";
    public static final String DESCRIPTION_ATTR = "description";
    public static final String IMAGE_ATTR = "image";

    private SetsParser(){
        throw new AssertionError();
    }

    public static void loadSets(File xmlFile){

    }

    public static int loadStartBase(Context context){
        int addedWords = 0;
        try {
            InputStream inputStream = context.getAssets().open("nature.xml");
            addedWords += insertSetsIntoDb(inputStream);
            inputStream = context.getAssets().open("things.xml");
            addedWords += insertSetsIntoDb(inputStream);
            inputStream = context.getAssets().open("verbs.xml");
            addedWords += insertSetsIntoDb(inputStream);
        }catch(IOException ex){
            ex.printStackTrace();
        }
        return addedWords;
    }

    private static int insertSetsIntoDb(InputStream inputStream){
        int addedSetsCount = 0;
        int addedWordsCount = 0;

        WordsDbAdapter dbAdapter = new WordsDbAdapter();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            Document document = builder.parse(inputStream);

            Element rootElement = document.getDocumentElement();
            rootElement.normalize();

            NodeList setsList = rootElement.getElementsByTagName(SET_NODE);
            Log.d(TAG, "insertSetsIntoDb: sets in doc" + setsList.getLength());

            //parse sets and insert them into database
            for(int a = 0; a < setsList.getLength(); a++){
                Node setNode = setsList.item(a);
                if(setNode.getNodeType() == Node.ELEMENT_NODE){

                    Map<String, String> wordsMap = new HashMap<>();
                    //parse words and save into map
                    NodeList wordsList = setNode.getChildNodes();
                    for(int b = 0; b < wordsList.getLength(); b++){
                        Node wordNode = wordsList.item(b);
                        if(wordNode.getNodeType() == Node.ELEMENT_NODE){
                            String word = capitalize(wordNode.getAttributes().getNamedItem(WORD_ATTR).getNodeValue());
                            String translation = capitalize(wordNode.getAttributes().getNamedItem(TRANSLATION_ATTR).getNodeValue());

                            wordsMap.put(word, translation);
                        }
                    }

                    String setName = setNode.getAttributes().getNamedItem(NAME_ATTR).getNodeValue();
                    String description = setNode.getAttributes().getNamedItem(DESCRIPTION_ATTR).getNodeValue();
                    String image = setNode.getAttributes().getNamedItem(IMAGE_ATTR).getNodeValue();
                    int wordsInSet = wordsMap.size();

                    ContentValues setValues = new ContentValues();
                    setValues.put(DatabaseContract.Sets.COLUMN_NAME, setName);
                    setValues.put(DatabaseContract.Sets.COLUMN_DESCRIPTION, description);
                    setValues.put(DatabaseContract.Sets.COLUMN_NUM_OF_WORDS, wordsInSet);
                    setValues.put(DatabaseContract.Sets.COLUMN_IMAGE_URL, image);

                    long setId = dbAdapter.insertSet(setValues);
                    addedSetsCount++;

                    Set<String> wordsSet = wordsMap.keySet();
                    for(String word : wordsSet){
                        dbAdapter.insertWord(word, wordsMap.get(word), setId, DatabaseContract.Words.OUT_OF_DICTIONARY);
                        addedWordsCount++;
                    }
                }
            }

            Log.d(TAG, "added sets " + addedSetsCount);
            Log.d(TAG, "added words " + addedWordsCount);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return addedWordsCount;
    }

    private static String capitalize(String string){
        String s = string.substring(0, 1).toUpperCase();
        String s2 = s + string.substring(1);
        return s2;
    }
}
