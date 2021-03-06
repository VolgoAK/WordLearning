package xyz.volgoak.wordlearning.screens.training.helpers;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Alexander Karachev on 07.05.2017.
 */

public class Training implements Serializable{

    public static final String TAG = "Training";

    protected List<PlayWord> playWords;
    private int currentPosition = 0;
    private int tries = 0;
    private int score = 0;
    private List<Long> mIdsForUpdate = new ArrayList<>();
    private PlayWord currentWord;
    //private WordUpdater updater;
    private int trainingType;

    private boolean accessible = false;

    Training(List<PlayWord> playWords, int trainingType){
        this.playWords = playWords;
        this.trainingType = trainingType;
    }

    public TrainingWord getFirstWord(){
        currentPosition = 0;
        currentWord = playWords.get(currentPosition);
        accessible = true;
        return new TrainingWord(currentWord.getWord(), currentWord.getVars());
    }

    public TrainingWord getNextWord(){
        if(currentPosition >= playWords.size()){
            return null;
        }
        currentWord = playWords.get(currentPosition);
        TrainingWord tw = new TrainingWord(currentWord.getWord(), currentWord.getVars());
        accessible = true;
        return tw;
    }

    public TrainingWord getCurrentWord(){
        TrainingWord tw = new TrainingWord(currentWord.getWord(), currentWord.getVars());
        return tw;
    }

    public Results getResults(){
        Results results = new Results(Results.ResultType.SUCCESS);
        results.correctAnswers = score;
        results.wordCount = playWords.size();
        results.idsForUpdate = mIdsForUpdate;
        results.trainedType = trainingType;

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

        tries++;

        if(correctness){
            //updater.updateWord(currentWord.getId(), trainingType);
            mIdsForUpdate.add(currentWord.getId());
            currentPosition++;
//            Log.d("Training : ", "current position " + currentPosition);
            if(tries <= playWords.size()){
//                Log.d("Training", "answerOperations: score + ");
                score++;
            }
        }else{
            playWords.add(currentWord);
            playWords.remove(currentPosition);
        }

        accessible = false;
    }

    public int getTrainingType() {
        return trainingType;
    }

    @SuppressLint("DefaultLocale")
    public String getProgressString() {
        return String.format("%d/%d", currentPosition, playWords.size());
    }

    public float getProgressInPercents(){
        float progress = (float)((currentPosition + 0.0)/playWords.size()) * 100;
//        Log.d(TAG, "getProgressInPercents: progress" + progress);
        return progress;
    }
}
