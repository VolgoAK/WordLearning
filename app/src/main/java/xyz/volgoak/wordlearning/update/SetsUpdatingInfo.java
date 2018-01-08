package xyz.volgoak.wordlearning.update;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

/**
 * Simple dataholder class for keep information
 * about updating database
 */
public class SetsUpdatingInfo {
    private int setsAdded;
    private int wordsAdded;
    private boolean isUpdatingSuccess;

    private SetsUpdatingInfo mInfo;

    public int getSetsAdded() {
        int allAdded = setsAdded;
        if (mInfo != null) {
            allAdded += mInfo.getSetsAdded();
        }
        return allAdded;
    }

    public int getWordsAdded() {
        int allAdded = wordsAdded;
        if (mInfo != null) {
            allAdded += mInfo.getWordsAdded();
        }
        return allAdded;
    }

    public void incrementSetsAdded() {
        setsAdded++;
    }

    public void incrementWordsAdded() {
        wordsAdded++;
    }

    public void addInfo(SetsUpdatingInfo info) {
        if (mInfo == null) {
            mInfo = info;
        } else {
            mInfo.addInfo(info);
        }
    }

    public boolean isUpdatingSuccess() {
        return isUpdatingSuccess;
    }

    public void setUpdatingSuccess(boolean updatingSuccess) {
        isUpdatingSuccess = updatingSuccess;
    }
}
