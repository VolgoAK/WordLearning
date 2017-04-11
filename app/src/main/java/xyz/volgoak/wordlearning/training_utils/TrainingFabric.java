package xyz.volgoak.wordlearning.training_utils;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;
import xyz.volgoak.wordlearning.training_utils.PlayWord;
import xyz.volgoak.wordlearning.training_utils.Training;

import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_TRAINED_TW;
import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_TRAINED_WT;
import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_TRANSLATION;
import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_WORD;


/**
 * Created by 777 on 08.06.2016.
 */
public class TrainingFabric implements Training.WordUpdater{

    public final static int WORD_TRANSLATION = 0;
    public final static int TRANSLATION_WORD = 1;

    private WordsDbAdapter dbAdapter;

    public TrainingFabric(Context context){
        dbAdapter = new WordsDbAdapter(context);
    }

    @NonNull
    public Training getTraining(int trainingType){
        int wordColumn = 0;
        int variantsColumn = 0;
        String variantsColumnString = "";
        Cursor cursor = null;

        if(trainingType == WORD_TRANSLATION){
            cursor = dbAdapter.fetchWordsByTrained(COLUMN_TRAINED_WT);
            wordColumn = cursor.getColumnIndex(COLUMN_WORD);
            variantsColumn = cursor.getColumnIndex(COLUMN_TRANSLATION);
            variantsColumnString = COLUMN_TRANSLATION;
        }else if(trainingType == TRANSLATION_WORD){
            cursor = dbAdapter.fetchWordsByTrained(COLUMN_TRAINED_TW);
            wordColumn = cursor.getColumnIndex(COLUMN_TRANSLATION);
            variantsColumn = cursor.getColumnIndex(COLUMN_WORD);
            variantsColumnString = COLUMN_WORD;
        }
        cursor.moveToFirst();
        int idColumn = cursor.getColumnIndex(DatabaseContract.Words._ID);

        ArrayList<PlayWord> playWords = new ArrayList<>();
        int cursorCount = cursor.getCount();

        for(int a = 0; a < cursorCount; a++){
            String word = cursor.getString(wordColumn);
            String translation = cursor.getString(variantsColumn);
            String[] vars = dbAdapter.getVariants(cursor.getInt(idColumn), variantsColumnString);
            PlayWord playWord = new PlayWord(word, translation, vars, cursor.getInt(idColumn));
            playWords.add(playWord);
            cursor.moveToNext();
        }
        return new Training(playWords, this, trainingType);
    }



    public void updateWord(int id, int trainingType){
        String trainedType = "";
        switch (trainingType){
            case WORD_TRANSLATION :
                trainedType = COLUMN_TRAINED_WT;
                break;
            case TRANSLATION_WORD :
                trainedType = COLUMN_TRAINED_TW;
                break;
        }

        dbAdapter.changeTrainedStatus(id, WordsDbAdapter.INCREASE, trainedType);
    }
}
