package xyz.volgoak.wordlearning;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by 777 on 08.06.2016.
 */
public class PlayWord {

        private String word;
        private String translation;
        private List<String> vars;
        private int id;

        public PlayWord(String word, String translation, String[] vars, int id){
            this.word = word;
            this.translation = translation;
            this.vars = new ArrayList<>();
            this.id = id;
            Collections.addAll(this.vars, vars);
            this.vars.add(translation);
            Collections.shuffle(this.vars);
        }

        public String[] getVars(){
            String[] varStrings = vars.toArray(new String[vars.size()]);
            return varStrings;
        }

        public boolean checkAnswer(int answerNum){
            return vars.get(answerNum).equals(translation);
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

    public String getTranslation() {
        return translation;
    }
}
