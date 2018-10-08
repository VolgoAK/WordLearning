package xyz.volgoak.wordlearning.screens.training.helpers;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

public class TrainingWord {
    public String word;
    public String[] vars;

    public TrainingWord(String word, String[] vars){
        this.word = word;
        this.vars = vars;
    }

    public String getWord() {
        return word;
    }

    public String[] getVars() {
        return vars;
    }
}
