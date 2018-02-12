package xyz.volgoak.wordlearning.training_utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by alex on 1/27/18.
 */

public class TrainingBool extends Training {

    public static final String TAG = TrainingBool.class.getSimpleName();

    public static final int ONE_STAR = 2;
    public static final int TWO_STAR = 4;
    public static final int THREE_STAR = 8;

    private int position = 0;
    private int scores = 0;
    private int rightInLine = 0;


    public TrainingBool(List<PlayWord> playWords, int trainingType) {
        super(playWords, trainingType);
        for (PlayWord pw : playWords) {
            Log.d(TAG, "TrainingBool: " + pw.getWord());
        }
    }

    public List<PlayWord> getInitialWords() {
        if (playWords.size() >= position + 1) {
            return playWords.subList(position, position + 2);
        } else return null;
    }

    /**
     * Returns a word which will be next in a training.
     * Warning, this word need for place holder view. Method check answer
     * will return answer for previous word. Kind of stupid, yes?
     * @return PlayWord which will be next in a training
     */
    public PlayWord nextWord() {
        position++;
        if (playWords.size() <= position + 2) {
            Log.d(TAG, "nextWord: add new words " + position);
            List<PlayWord> nextWords = new ArrayList<>(playWords);
            Collections.shuffle(nextWords);
            playWords.addAll(nextWords);
            Log.d(TAG, "nextWord: new list size " + playWords.size());
        }

        return playWords.get(position + 1);
    }

    public boolean checkAnswer(boolean correct) {
        Log.d(TAG, "checkAnswer: ");
        boolean answer = correct ? playWords.get(position).checkAnswer(0)
                : playWords.get(position).checkAnswer(1);
        if (answer) {
            rightInLine++;
            scores += scoresCounter();
        } else rightInLine = 0;

        return answer;
    }

    public int scoresCounter() {
        if (rightInLine >= THREE_STAR) return 10;
        if (rightInLine >= TWO_STAR) return 5;
        if (rightInLine >= ONE_STAR) return 3;
        return 1;
    }

    public int getStars() {
        if (rightInLine >= THREE_STAR) return 3;
        if (rightInLine >= TWO_STAR) return 2;
        if (rightInLine >= ONE_STAR) return 1;
        return 0;
    }

    @Override
    public Results getResults() {
        Results results = super.getResults();
        results.scores = scores;
        return results;
    }

    public int getScores() {
        return scores;
    }
}
