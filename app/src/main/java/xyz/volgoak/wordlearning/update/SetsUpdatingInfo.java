package xyz.volgoak.wordlearning.update;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

import java.util.HashSet;
import java.util.Set;

/**
 * Simple dataholder class for keep information
 * about updating database
 */
public class SetsUpdatingInfo {
    private int wordsAdded;
    private boolean isUpdatingSuccess;
    private Set<Long> setIds;

    public int getSetsAdded() {
        return setIds == null ? 0 : setIds.size();
    }

    public int getWordsAdded() {
        return wordsAdded;
    }

    public void onSetAdded(long setId) {
        if(setIds == null) {
            synchronized (this) {
                if(setIds == null) {
                    setIds = new HashSet<>();
                }
            }
        }

        setIds.add(setId);
    }

    public void incrementWordsAdded() {
        wordsAdded++;
    }

    public  void addInfo(SetsUpdatingInfo info) {
        wordsAdded += info.wordsAdded;
        if(info.setIds != null) {
            if(setIds != null) {
                setIds.addAll(info.setIds);
            } else {
                setIds = info.setIds;
            }
        }
    }

    public boolean isUpdatingSuccess() {
        return isUpdatingSuccess;
    }

    public void setUpdatingSuccess(boolean updatingSuccess) {
        isUpdatingSuccess = updatingSuccess;
    }
}
