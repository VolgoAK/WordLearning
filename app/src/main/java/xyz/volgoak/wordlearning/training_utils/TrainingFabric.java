package xyz.volgoak.wordlearning.training_utils;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import xyz.volgoak.wordlearning.WordsApp;
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.entities.Word;


import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_TRAINED_TW;
import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_TRAINED_WT;



/**
 * Created by Alexander Karachev on 07.05.2017.
 */
public abstract class TrainingFabric {

    public final static int WORD_TRANSLATION = 0;
    public final static int TRANSLATION_WORD = 1;
    public static final int TRAINING_LIMIT = 4;



    public static Training getTraining(int trainingType, long setId, DataProvider provider){

        String variantsColumnString = "";
        List<Word> wordList = null;



        GetWord wordGetter;
        GetWord variantsGetter;

        if(trainingType == WORD_TRANSLATION){
            wordList = provider.getWordsByTrained("0", 10, 1);
            wordGetter = Word::getWord;
            variantsGetter = Word::getTranslation;
            variantsColumnString = DatabaseContract.Words.COLUMN_TRANSLATION;
            Log.d("Fabric", "getTraining: words " + wordList.size());
        }else if(trainingType == TRANSLATION_WORD){
            wordList = provider.getWordsByTrained(COLUMN_TRAINED_TW, 10,TRAINING_LIMIT);
            wordGetter = Word::getTranslation;
            variantsGetter = Word::getWord;
            variantsColumnString = DatabaseContract.Words.COLUMN_WORD;
        }else throw new IllegalArgumentException("incorrect training type");

        if(wordList.size() == 0){
            //no untrained words in a dictionary
            return null;
        }


        ArrayList<PlayWord> playWords = new ArrayList<>();

        for(Word w : wordList) {
            String word = wordGetter.getString(w);
            String translation = variantsGetter.getString(w);
            // TODO: 1/7/18 variants
            String[] vars = new String[]{"One", "Two", "Three"};
            PlayWord playWord = new PlayWord(word, translation, vars, (int) w.getId());
            playWords.add(playWord);
        }

        return new Training(playWords, trainingType);
    }

    interface GetWord{
        String getString(Word word);
    }
}
