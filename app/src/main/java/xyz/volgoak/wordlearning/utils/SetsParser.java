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
    public static final String WORD_NODE = "Word";
    public static final String WORD_ATTR = "word";
    public static final String TRANSLATION_ATTR = "translation";
    public static final String DESCRIPTION_ATTR = "description";
    public static final String WORD_COUNT_ATTR = "words_count";

    private SetsParser(){
        throw new AssertionError();
    }

    public static void loadSets(File xmlFile){

    }

    public static int loadStartBase(Context context){
        int addedWords = 0;
        try {
            InputStream inputStream = context.getAssets().open("start_base.xml");
            addedWords = insertSetsIntoDb(inputStream, context);
        }catch(IOException ex){
            ex.printStackTrace();
        }
        return addedWords;
    }

    private static int insertSetsIntoDb(InputStream inputStream, Context context){
        int addedSetsCount = 0;
        int addedWordsCount = 0;

        WordsDbAdapter dbAdapter = new WordsDbAdapter(context);

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
                    String setName = setNode.getAttributes().getNamedItem(NAME_ATTR).getNodeValue();
                    String description = setNode.getAttributes().getNamedItem(DESCRIPTION_ATTR).getNodeValue();
                    int wordsInSet = Integer.parseInt(setNode.getAttributes().getNamedItem(WORD_COUNT_ATTR).getNodeValue());

                    ContentValues setValues = new ContentValues();
                    setValues.put(DatabaseContract.Sets.COLUMN_NAME, setName);
                    setValues.put(DatabaseContract.Sets.COLUMN_DESCRIPTION, description);
                    setValues.put(DatabaseContract.Sets.COLUMN_NUM_OF_WORDS, wordsInSet);

                    long setId = dbAdapter.insertSet(setValues);
                    addedSetsCount++;

                    //parse and insert words
                    NodeList wordsList = setNode.getChildNodes();
                    for(int b = 0; b < wordsList.getLength(); b++){
                        Node wordNode = wordsList.item(b);
                        if(wordNode.getNodeType() == Node.ELEMENT_NODE){
                            String word = wordNode.getAttributes().getNamedItem(WORD_ATTR).getNodeValue();
                            String translation = wordNode.getAttributes().getNamedItem(TRANSLATION_ATTR).getNodeValue();

                            dbAdapter.insertWord(word, translation, setId);
                            addedWordsCount++;
                        }
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
}
