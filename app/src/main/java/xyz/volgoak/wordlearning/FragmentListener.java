package xyz.volgoak.wordlearning;

/**
 * Created by 777 on 10.06.2016.
 */
interface FragmentListener {
    void startTrainingWTFragment();
    void startTrainingTWFragment();
    void startRedactorFragment();
    void startResultsFragment(int correctAnswers, int wrongAnswers);
}
