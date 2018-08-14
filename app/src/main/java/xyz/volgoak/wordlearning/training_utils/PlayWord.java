package xyz.volgoak.wordlearning.training_utils;

import com.attiladroid.data.entities.Word;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;



/**
 * Created by Alexander Karachev on 07.05.2017.
 */

/**
 * Keep information about word and provides variants
 * of translating.
 */
public class PlayWord implements Serializable {

    public static final String TAG = "PlayWord";

    private String word;
    private String translation;
    private String[] vars;
    private long id;

    public PlayWord(Word word) {
        this.word = word.getWord();
        this.translation = word.getTranslation();
        this.id = word.getId();
    }

    public PlayWord(String word, String translation, String[] vars, long id) {
        this.word = word;
        this.translation = translation;
        this.id = id;
        setVars(vars);
    }

    public String[] getVars() {
        return vars;
    }

    public boolean checkAnswer(int answerNum) {
//            Log.d(TAG, "checkAnswer: answerNum" + answerNum + " word at position " + vars[answerNum]);
        return vars[answerNum].equals(translation);
    }

    public boolean checkAnswer(String answer) {
        return translation.toLowerCase().equals(answer.toLowerCase());
    }

    public long getId() {
        return id;
    }

    public String getWord() {
        return word;
    }

    String getTranslation() {
        return translation;
    }

    void setVars(String[] vars) {
        //create array of vars which contains all variants and right translation
        ArrayList<String> variants = new ArrayList<>();
        Collections.addAll(variants, vars);
        variants.add(translation);
        Collections.shuffle(variants);
        this.vars = variants.toArray(new String[variants.size()]);
    }

    @Override
    public String toString() {
        StringBuilder representation = new StringBuilder("PlayWord: word ");
        representation.append(word).append(", translation ").append(translation);
        if (vars != null) {
            representation.append(", vars : ");
            for (String s : vars) {
                representation.append(s).append(",");
            }
        }
        return representation.toString();
    }
}
