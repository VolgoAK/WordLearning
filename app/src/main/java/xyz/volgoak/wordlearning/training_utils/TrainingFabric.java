package xyz.volgoak.wordlearning.training_utils;


import java.util.ArrayList;
import java.util.List;

import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.entities.Word;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;

import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_TRAINED_TW;
import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_TRAINED_WT;
import static xyz.volgoak.wordlearning.data.WordsDbAdapter.TRAINING_LIMIT;


/**
 * Created by Alexander Karachev on 07.05.2017.
 */
public abstract class TrainingFabric {

    public final static int WORD_TRANSLATION = 0;
    public final static int TRANSLATION_WORD = 1;


    public static Training getTraining(int trainingType, long setId){

        String variantsColumnString = "";
        List<Word> wordList = null;

        WordsDbAdapter dbAdapter = new WordsDbAdapter();

        GetWord wordGetter;
        GetWord variantsGetter;

        if(trainingType == WORD_TRANSLATION){
            wordList = dbAdapter.fetchWordsByTrained(COLUMN_TRAINED_WT, 10, TRAINING_LIMIT, setId);
            wordGetter = Word::getWord;
            variantsGetter = Word::getTranslation;
            variantsColumnString = DatabaseContract.Words.COLUMN_TRANSLATION;
        }else if(trainingType == TRANSLATION_WORD){
            wordList = dbAdapter.fetchWordsByTrained(COLUMN_TRAINED_TW, 10,TRAINING_LIMIT, setId);
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
            String[] vars = dbAdapter.getVariants((int)w.getId(), variantsColumnString, setId);
            PlayWord playWord = new PlayWord(word, translation, vars, (int) w.getId());
            playWords.add(playWord);
        }

        return new Training(playWords, trainingType);
    }

    interface GetWord{
        String getString(Word word);
    }
}
