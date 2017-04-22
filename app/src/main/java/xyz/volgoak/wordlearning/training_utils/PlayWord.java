package xyz.volgoak.wordlearning.training_utils;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Keep information about word and provides variants
 * of translating.
 */
public class PlayWord implements Serializable{

        public static final String TAG = "PlayWord";

        private String word;
        private String translation;
        private String[] vars;
        private long id;

        public PlayWord(String word, String translation, String[] vars, int id){
            this.word = word;
            this.translation = translation;
            this.id = id;
            //create array of vars which contains all variants and right translation
            ArrayList<String> variants = new ArrayList<>();
            Collections.addAll(variants, vars);
            variants.add(translation);
            Collections.shuffle(variants);
            this.vars = variants.toArray(new String[variants.size()]);
        }

        public String[] getVars(){
            return vars;
        }

        public boolean checkAnswer(int answerNum){
            Log.d(TAG, "checkAnswer: answerNum" + answerNum + " word at position " + vars[answerNum]);
            return vars[answerNum].equals(translation);
        }

        public boolean checkAnswer(String answer){
            return translation.equals(answer);
        }

        public long getId() {
            return id;
        }

         public String getWord() {
            return word;
         }

}
