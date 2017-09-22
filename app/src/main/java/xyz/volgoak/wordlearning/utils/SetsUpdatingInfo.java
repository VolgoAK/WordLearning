package xyz.volgoak.wordlearning.utils;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

/**
 * Simple dataholder class for keep information
 * about updating database
 */
public class SetsUpdatingInfo{
    private int setsAdded;
    private int wordsAdded;
    private boolean isUpdatingSuccess;

    public int getSetsAdded() {
        return setsAdded;
    }

    public int getWordsAdded() {
        return wordsAdded;
    }

    public void incrementSetsAdded(){
        setsAdded++;
    }

    public void incrementWordsAdded(){
        wordsAdded++;
    }

    public void addInfo(SetsUpdatingInfo info){
        setsAdded += info.getSetsAdded();
        wordsAdded += info.getWordsAdded();
    }

    public boolean isUpdatingSuccess() {
        return isUpdatingSuccess;
    }

    public void setUpdatingSuccess(boolean updatingSuccess) {
        isUpdatingSuccess = updatingSuccess;
    }
}
