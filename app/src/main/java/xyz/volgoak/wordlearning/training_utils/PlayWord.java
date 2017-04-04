package xyz.volgoak.wordlearning.training_utils;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Keep information about word and provides variants
 * of translating.
 */
public class PlayWord {

        public static final String TAG = "PlayWord";

        private String word;
        private String translation;
        private String[] vars;
        private int id;

        public PlayWord(String word, String translation, String[] vars, int id){
            this.word = word;
            this.translation = translation;
            this.id = id;
            //create array of vars which contains all variants and right translation
            this.vars = Arrays.copyOf(vars, vars.length + 1);
            this.vars[this.vars.length - 1] = translation;
        }

        public String[] getVars(){
            //create list from array and snuffle it before returning
            ArrayList<String> variants = new ArrayList<>();
            Collections.addAll(variants, vars);
            Collections.shuffle(variants);
            vars = variants.toArray(new String[variants.size()]);
            return vars;
        }

        public boolean checkAnswer(int answerNum){
            Log.d(TAG, "checkAnswer: answerNum" + answerNum + " word at position " + vars[answerNum]);
            return vars[answerNum].equals(translation);
        }

        public boolean checkAnswer(String answer){
            return translation.equals(answer);
        }

        public int getId() {
            return id;
        }

         public String getWord() {
            return word;
         }

}
