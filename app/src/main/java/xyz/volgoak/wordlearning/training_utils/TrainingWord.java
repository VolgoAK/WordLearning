package xyz.volgoak.wordlearning.training_utils;

/**
 * Created by 777 on 09.06.2016.
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
