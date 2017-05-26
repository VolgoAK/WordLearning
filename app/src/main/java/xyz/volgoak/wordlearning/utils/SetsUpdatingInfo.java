package xyz.volgoak.wordlearning.utils;

/**
 * Created by Volgoak on 26.05.2017.
 */

/**
 * Simple dataholder class for keep information
 * about updating database
 */
public class SetsUpdatingInfo{
    private int setsAdded;
    private int wordsAdded;

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

    public void infoSum(SetsUpdatingInfo info){
        setsAdded += info.getSetsAdded();
        wordsAdded += info.getWordsAdded();
    }
}
