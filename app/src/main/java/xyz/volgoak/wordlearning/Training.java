package xyz.volgoak.wordlearning;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by 777 on 08.06.2016.
 */

// TODO: 28.01.2017 move this class into training package
public class Training {

    private ArrayList<PlayWord> playWords;
    private int currentPosition = 0;
    private int tries = 0;
    private int score = 0;
    private PlayWord currentWord;
    private WordUpdater updater;
    private int trainingType;

    private boolean accessible = false;

    public Training(ArrayList<PlayWord> playWords, WordUpdater updater, int trainingType){
        this.playWords = playWords;
        this.updater = updater;
        this.trainingType = trainingType;
    }

    public TrainingWord getNextWord(){
        if(currentPosition >= playWords.size()){
            return null;
        }
        currentWord = playWords.get(currentPosition);
        TrainingWord tw = new TrainingWord(currentWord.getWord(), currentWord.getVars());

        tries++;
        accessible = true;
        return tw;
    }

    public int[] getResults(){
        int[] results = new int[2];
        results[0] = score;
        results[1] = playWords.size() - score;

        return results;
    }

    public boolean checkAnswer(String answer){
        boolean correctness = currentWord.checkAnswer(answer);
        answerOperations(correctness);
        return  correctness;
    }

    public boolean checkAnswer(int answerNum){
        boolean correctness = currentWord.checkAnswer(answerNum);
        answerOperations(correctness);
        return  correctness;
    }

    private void answerOperations(boolean correctness){
        if(!accessible) return;
        if(correctness){
            updater.updateWord(currentWord.getId(), trainingType);
            currentPosition++;
            Log.d("Training : ", "current position " + currentPosition);
            if(tries <= playWords.size()) score++;
        }else{
            playWords.add(currentWord);
            playWords.remove(currentPosition);
        }
        accessible = false;
    }

    public int getTrainingType() {
        return trainingType;
    }

    public interface WordUpdater{
        void updateWord(int id, int trainedType);
    }
}
