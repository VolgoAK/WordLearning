package xyz.volgoak.wordlearning.training_utils;

import android.content.Context;
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
 * Created by 777 on 08.06.2016.
 */
public class TrainingFabric {

    public final static int WORD_TRANSLATION = 0;
    public final static int TRANSLATION_WORD = 1;

    private WordsDbAdapter mDbAdapter;

    public TrainingFabric(Context context){
        mDbAdapter = new WordsDbAdapter(context);
    }

    public Training getTraining(int trainingType, long setId){
        int wordColumn = 0;
        int variantsColumn = 0;
        String variantsColumnString = "";
        Cursor cursor = null;

        if(trainingType == WORD_TRANSLATION){
            cursor = mDbAdapter.fetchWordsByTrained(COLUMN_TRAINED_WT, 10, TRAINING_LIMIT, setId);
            wordColumn = cursor.getColumnIndex(COLUMN_WORD);
            variantsColumn = cursor.getColumnIndex(COLUMN_TRANSLATION);
            variantsColumnString = COLUMN_TRANSLATION;
        }else if(trainingType == TRANSLATION_WORD){
            cursor = mDbAdapter.fetchWordsByTrained(COLUMN_TRAINED_TW, 10,TRAINING_LIMIT, setId);
            wordColumn = cursor.getColumnIndex(COLUMN_TRANSLATION);
            variantsColumn = cursor.getColumnIndex(COLUMN_WORD);
            variantsColumnString = COLUMN_WORD;
        }
        if(!cursor.moveToFirst()){
            //no untrained words in a dictionary
            return null;
        }
        int idColumn = cursor.getColumnIndex(DatabaseContract.Words._ID);

        ArrayList<PlayWord> playWords = new ArrayList<>();
        int cursorCount = cursor.getCount();

        for(int a = 0; a < cursorCount; a++){
            String word = cursor.getString(wordColumn);
            String translation = cursor.getString(variantsColumn);
            String[] vars = mDbAdapter.getVariants(cursor.getInt(idColumn), variantsColumnString);
            PlayWord playWord = new PlayWord(word, translation, vars, cursor.getInt(idColumn));
            playWords.add(playWord);
            cursor.moveToNext();
        }
        return new Training(playWords, trainingType);
    }
}
