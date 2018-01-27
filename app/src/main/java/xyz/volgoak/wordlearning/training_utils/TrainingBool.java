package xyz.volgoak.wordlearning.training_utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 1/27/18.
 */

public class TrainingBool extends Training {

    public static final String TAG = TrainingBool.class.getSimpleName();

    private int position = 0;
    private int scores = 0;

    public TrainingBool(ArrayList<PlayWord> playWords, int trainingType) {
        super(playWords, trainingType);
    }

    public List<PlayWord> getInitialWords() {
        if(playWords.size() >= 2) {
            return playWords.subList(0, 2);
        } else return null;
    }

    public PlayWord nextWord() {
        position++;
        if(playWords.size() >= position + 1) {
            return playWords.get(position + 1);
        } else return null;
    }

    public boolean checkAnswer(boolean correct) {
        Log.d(TAG, "checkAnswer: ");
        boolean answer = correct ? playWords.get(position).checkAnswer(0)
                : playWords.get(position).checkAnswer(1);
        if(answer) scores++;

        return answer;
    }

    public int getScores() {
        return scores;
    }
}
