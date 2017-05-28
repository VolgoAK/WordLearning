package xyz.volgoak.wordlearning;

import xyz.volgoak.wordlearning.training_utils.Results;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */
public interface FragmentListener {
    void startTraining(int type);
    void startTraining(int type, long setId);
    void startDictionary();
    void startSets();
    void setActionBarTitle(String title);
}
