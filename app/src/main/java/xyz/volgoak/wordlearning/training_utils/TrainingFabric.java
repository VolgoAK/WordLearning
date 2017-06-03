package xyz.volgoak.wordlearning.training_utils;


import android.database.Cursor;

import java.util.ArrayList;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;

import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_TRAINED_TW;
import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_TRAINED_WT;
import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_TRANSLATION;
import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_WORD;
import static xyz.volgoak.wordlearning.data.WordsDbAdapter.TRAINING_LIMIT;


/**
 * Created by Alexander Karachev on 07.05.2017.
 */
public abstract class TrainingFabric {

    public final static int WORD_TRANSLATION = 0;
    public final static int TRANSLATION_WORD = 1;

    public static Training getTraining(int trainingType, long setId){
        int wordColumn = 0;
        int variantsColumn = 0;
        String variantsColumnString = "";
        Cursor cursor = null;

        WordsDbAdapter dbAdapter = new WordsDbAdapter();

        if(trainingType == WORD_TRANSLATION){
            cursor = dbAdapter.fetchWordsByTrained(COLUMN_TRAINED_WT, 10, TRAINING_LIMIT, setId);
            wordColumn = cursor.getColumnIndex(COLUMN_WORD);
            variantsColumn = cursor.getColumnIndex(COLUMN_TRANSLATION);
            variantsColumnString = COLUMN_TRANSLATION;
        }else if(trainingType == TRANSLATION_WORD){
            cursor = dbAdapter.fetchWordsByTrained(COLUMN_TRAINED_TW, 10,TRAINING_LIMIT, setId);
            wordColumn = cursor.getColumnIndex(COLUMN_TRANSLATION);
            variantsColumn = cursor.getColumnIndex(COLUMN_WORD);
            variantsColumnString = COLUMN_WORD;
        }else throw new IllegalArgumentException("incorrect training type");

        if(!cursor.moveToFirst()){
            //no untrained words in a dictionary
            cursor.close();
            return null;
        }
        int idColumn = cursor.getColumnIndex(DatabaseContract.Words._ID);

        ArrayList<PlayWord> playWords = new ArrayList<>();
        cursor.moveToFirst();

        do{
            String word = cursor.getString(wordColumn);
            String translation = cursor.getString(variantsColumn);
            String[] vars = dbAdapter.getVariants(cursor.getInt(idColumn), variantsColumnString, setId);
            PlayWord playWord = new PlayWord(word, translation, vars, cursor.getInt(idColumn));
            playWords.add(playWord);
        }while (cursor.moveToNext());

        cursor.close();
        return new Training(playWords, trainingType);
    }
}
