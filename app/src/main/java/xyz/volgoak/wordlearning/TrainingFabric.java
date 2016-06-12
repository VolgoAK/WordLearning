package xyz.volgoak.wordlearning;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import static xyz.volgoak.wordlearning.WordsSqlHelper.*;

/**
 * Created by 777 on 08.06.2016.
 */
public class TrainingFabric implements Training.WordUpdater{

    public final static String WORD_TRANSLATION = "wt";
    public final static String TRANSLATION_WORD = "tw";

    private WordsDbAdapter dbAdapter;
    private Cursor cursor;

    public TrainingFabric(Context context){
        dbAdapter = new WordsDbAdapter(context);
    }

    //it is first - hard version of fabric method
    //make it better in the future
    //also make  the method get training throws exception
    //if training type param is incorrect
    public Training getTraining(String trainingType){
        int wordColumn = 0;
        int variantsColumn = 0;

        if(trainingType.equals(WORD_TRANSLATION)){
            cursor = dbAdapter.fetchWardsByTrained(COLUMN_TRAINED_WT);
            wordColumn = cursor.getColumnIndex(COLUMN_WORD);
            variantsColumn = cursor.getColumnIndex(COLUMN_TRANSLATION);
        }else if(trainingType.equals(TRANSLATION_WORD)){
            cursor = dbAdapter.fetchWardsByTrained(COLUMN_TRAINED_TW);
            wordColumn = cursor.getColumnIndex(COLUMN_TRANSLATION);
            variantsColumn = cursor.getColumnIndex(COLUMN_WORD);
        }
        cursor.moveToFirst();
        int idColumn = cursor.getColumnIndex(COLUMN_ID);

        ArrayList<String> variants = new ArrayList<>();
        ArrayList<String> words = new ArrayList<>();
        ArrayList<String> translations = new ArrayList<>();
        //fill lists with words and variants
        for(int n = 0; n < 20; n++){
            variants.add(cursor.getString(variantsColumn));
            if(n<10) {
                words.add(cursor.getString(wordColumn));
                translations.add(cursor.getString(variantsColumn));
            }
            if(!cursor.moveToNext()) break;
        }

        ArrayList<PlayWord> playWords = new ArrayList<>();
        cursor.moveToFirst();
        for(int n = 0; n < words.size(); n++){
            String word = words.get(n);
            String translation = translations.get(n);
            //String[] vars = new String[3];
            List<String>vars = new ArrayList<>();
            //now vars can provide to equals word
            //make each var difrent
            for(int m = 0; m < 3;){
                int randomPosition = (int)(Math.random() * variants.size());
                String var = variants.get(randomPosition);
                if(!translation.equals(var) && !vars.contains(var)){
                    vars.add(var);
                    m++;
                }
            }
            String[] varArray = vars.toArray(new String[vars.size()]);
            PlayWord playWord = new PlayWord(word, translation, varArray, cursor.getInt(idColumn));
           // Log.d("Train fab", word + ", " + translation);
            playWords.add(playWord);
            cursor.moveToNext();
        }

        return new Training(playWords, this, trainingType);
    }



    public void updateWord(int id, String trainingType){
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
