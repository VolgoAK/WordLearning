package xyz.volgoak.wordlearning.training_utils;


import android.support.annotation.NonNull;
import android.util.Log;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import java.util.Queue;

import io.reactivex.Single;
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.entities.Word;
import xyz.volgoak.wordlearning.utils.Optional;


/**
 * Created by Alexander Karachev on 07.05.2017.
 */
public abstract class TrainingFabric {

    public final static int WORD_TRANSLATION = 0;
    public final static int TRANSLATION_WORD = 1;
    public static final int BOOL_TRAINING = 3;


    public static Single<Optional<Training>> getSimpleTraining(int trainingType, long setId,
                                                               int wordsLimit, DataProvider provider) {
        return Single.create(subscriber ->
                subscriber.onSuccess(createSimpleTraining(trainingType, setId, wordsLimit, provider)));
    }

    public static Single<TrainingBool> getBoolTrainingRx(long setId, DataProvider provider) {
        return Single.create(subscriber -> subscriber.onSuccess(createBoolTraining(setId, provider)));
    }

    private static @NonNull Optional<Training> createSimpleTraining(int trainingType, long setId,
                                                                    int wordsLimit, DataProvider provider) {
        List<Word> wordList = provider.getTrainingWords(setId);

        GetWord wordGetter;
        GetWord variantsGetter;
        Comparator<Word> trainedComparator;

        if (trainingType == WORD_TRANSLATION) {
            wordGetter = Word::getWord;
            variantsGetter = Word::getTranslation;
            trainedComparator = (w1, w2) -> Integer.compare(w1.getTrainedWt(), w2.getTrainedWt());

        } else if (trainingType == TRANSLATION_WORD) {
            wordGetter = Word::getTranslation;
            variantsGetter = Word::getWord;
            trainedComparator = (w1, w2) -> Integer.compare(w1.getTrainedTw(), w2.getTrainedTw());
        } else throw new IllegalArgumentException("incorrect training type");

        if (wordList.size() == 0) {
            //no untrained words in a dictionary
            return new Optional<>(null);
        }

        Collections.shuffle(wordList);
        Collections.sort(wordList, trainedComparator);

        ArrayList<PlayWord> playWords = new ArrayList<>();

        for (Word w : wordList) {
            String word = wordGetter.getString(w);
            String translation = variantsGetter.getString(w);

            List<Word> varList = provider.getVariants(w.getId(), 3, setId);
            String[] vars = new String[3];
            for (int a = 0; a < 3; a++) {
                vars[a] = variantsGetter.getString(varList.get(a));
            }
            PlayWord playWord = new PlayWord(word, translation, vars, w.getId());
            playWords.add(playWord);
            if (playWords.size() == wordsLimit) break;
        }

        if (playWords.size() == 0) return new Optional<>(null);
        return new Optional<>(new Training(playWords, trainingType));
    }

    private static TrainingBool createBoolTraining(long setId, DataProvider provider) {
        List<Word> words = provider.getTrainingWords(setId);

        Queue<String> variants = Stream.of(words).map(Word::getTranslation)
                .collect(Collectors.toCollection(LinkedList::new));
        Collections.shuffle(words);

        List<PlayWord> playWords = Stream.of(words).map(PlayWord::new)
                .map(p -> {
                    p.setVars(new String[] {variants.poll()});
                    return p;})
                .toList();
        
        Collections.shuffle(playWords);

        return new TrainingBool(playWords, BOOL_TRAINING);
    }

    interface GetWord {
        String getString(Word word);
    }
}
