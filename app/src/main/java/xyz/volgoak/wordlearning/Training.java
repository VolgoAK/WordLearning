package xyz.volgoak.wordlearning;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by 777 on 08.06.2016.
 */
public class Training {
    private ArrayList<PlayWord> playWords;
    private int currentPosition = 0;
    private int tries = 0;
    private int score = 0;
    private PlayWord currentWord;
    private WordUpdater updater;
    private String trainingType;

    public Training(ArrayList<PlayWord> playWords, WordUpdater updater, String trainingType){
        this.playWords = playWords;
        this.updater = updater;
        this.trainingType = trainingType;
    }

    public TrainingWord getNextWord(){
        currentWord = playWords.get(currentPosition);
        Log.d("Training " , currentWord.getWord() + " " + currentWord.getTranslation());
        TrainingWord tw = new TrainingWord(currentWord.getWord(), currentWord.getVars());
        Log.d("Training : ", "next word pos " + currentPosition);
        tries++;
        return tw;
    }

    public int getResults(){
        return 0;
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
        if(correctness){
            updater.updateWord(currentWord.getId());
            currentPosition++;
            Log.d("Training : ", "current position " + currentPosition);
            if(tries < playWords.size()) score++;
        }else{
            playWords.add(currentWord);
            playWords.remove(currentPosition);
        }
    }

    public String getTrainingType() {
        return trainingType;
    }

    public interface WordUpdater{
        void updateWord(int id);
    }
}
