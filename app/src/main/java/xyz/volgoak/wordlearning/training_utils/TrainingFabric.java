package xyz.volgoak.wordlearning.training_utils;


import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

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
    public static final int WORDS_LIMIT = 10;



    public static Training getTraining(int trainingType, long setId, DataProvider provider){

        String variantsColumnString = "";
        List<Word> wordList = provider.getTrainingWords(setId);



        GetWord wordGetter;
        GetWord variantsGetter;
        CompareField compareField;

        if(trainingType == WORD_TRANSLATION){
            wordGetter = Word::getWord;
            variantsGetter = Word::getTranslation;
            compareField = Word::getTrainedWt;

            Log.d("Fabric", "getTraining: words " + wordList.size());
        }else if(trainingType == TRANSLATION_WORD){
            wordGetter = Word::getTranslation;
            variantsGetter = Word::getWord;
            compareField = Word::getTrainedTw;
        }else throw new IllegalArgumentException("incorrect training type");

        if(wordList.size() == 0){
            //no untrained words in a dictionary
            return null;
        }

        ArrayList<PlayWord> playWords = new ArrayList<>();

        for(Word w : wordList) {
            String word = wordGetter.getString(w);
            String translation = variantsGetter.getString(w);

            List<Word> varList = provider.getVariants(w.getId(), 3);
            String[] vars = new String[3];
            for(int a = 0; a < 3; a++) {
                vars[a] = variantsGetter.getString(varList.get(a));
            }
            PlayWord playWord = new PlayWord(word, translation, vars, (int) w.getId());
            playWords.add(playWord);
            if(playWords.size() == WORDS_LIMIT) break;
        }

        if(playWords.size() == 0) return null;
        return new Training(playWords, trainingType);
    }

    interface GetWord{
        String getString(Word word);
    }

    interface CompareField {
        int getTrained(Word word);
    }
}
