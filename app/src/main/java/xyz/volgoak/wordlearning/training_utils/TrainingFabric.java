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
    public static final int BOOL_TRAINING = 3;

    public static final int TRAINING_LIMIT = 4;
    public static final int WORDS_LIMIT = 10;


    public static Training getSimpleTraining(int trainingType, long setId, DataProvider provider){

        String variantsColumnString = "";
        List<Word> wordList = provider.getTrainingWords(setId);

        GetWord wordGetter;
        GetWord variantsGetter;
        Comparator<Word> trainedComparator;

        if(trainingType == WORD_TRANSLATION){
            wordGetter = Word::getWord;
            variantsGetter = Word::getTranslation;
            trainedComparator = (w1, w2) -> Integer.compare(w1.getTrainedWt(), w2.getTrainedWt());

            Log.d("Fabric", "getSimpleTraining: words " + wordList.size());
        }else if(trainingType == TRANSLATION_WORD){
            wordGetter = Word::getTranslation;
            variantsGetter = Word::getWord;
            trainedComparator = (w1, w2) -> Integer.compare(w1.getTrainedTw(), w2.getTrainedTw());
        }else throw new IllegalArgumentException("incorrect training type");

        if(wordList.size() == 0){
            //no untrained words in a dictionary
            return null;
        }

        Collections.shuffle(wordList);
        Collections.sort(wordList, trainedComparator);

        ArrayList<PlayWord> playWords = new ArrayList<>();

        for(Word w : wordList) {
            String word = wordGetter.getString(w);
            String translation = variantsGetter.getString(w);

            List<Word> varList = provider.getVariants(w.getId(), 3, setId);
            String[] vars = new String[3];
            for(int a = 0; a < 3; a++) {
                vars[a] = variantsGetter.getString(varList.get(a));
            }
            PlayWord playWord = new PlayWord(word, translation, vars, w.getId());
            playWords.add(playWord);
            if(playWords.size() == WORDS_LIMIT) break;
        }

        if(playWords.size() == 0) return null;
        return new Training(playWords, trainingType);
    }

    public static TrainingBool getBoolTraining(long setId, DataProvider provider) {
        List<Word> words = provider.getTrainingWords(setId);
        ArrayList<PlayWord> playWords = new ArrayList<>();
        Collections.shuffle(words);
        for(Word w : words) {
            List<Word> vars = provider.getVariants(w.getId(), 1, setId);
            PlayWord pw = new PlayWord(w.getWord(), w.getTranslation(), new String[] {vars.get(0).getTranslation()}, w.getId());
            playWords.add(pw);
        }
        return new TrainingBool(playWords, BOOL_TRAINING);
    }

    interface GetWord{
        String getString(Word word);
    }
}
