package xyz.volgoak.wordlearning.training_utils;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

public class TrainingWord {
    private String word;
    private String[] vars;

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
