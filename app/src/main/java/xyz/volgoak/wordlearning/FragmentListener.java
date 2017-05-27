package xyz.volgoak.wordlearning;

import xyz.volgoak.wordlearning.training_utils.Results;

/**
 * Created by 777 on 10.06.2016.
 */
public interface FragmentListener {
    void startTraining(int type);
    void startTraining(int type, long setId);
    void startDictionary();
    void startSets();
    void setActionBarTitle(String title);
}
