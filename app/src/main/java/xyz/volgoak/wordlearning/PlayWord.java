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

        public PlayWord(String word, String translation, String[] vars){
            this.word = word;
            this.translation = translation;
            this.vars = new ArrayList<>();
            Collections.addAll(this.vars, vars);
            this.vars.add(word);
            Collections.shuffle(this.vars);
        }

        public String[] getVars(){
            String[] varStrings = vars.toArray(new String[vars.size()]);
            return varStrings;
        }

        public boolean checkAnswer(int answerNum){
            return vars.get(answerNum).equals(word);
        }

        public boolean checkAnswer(String answer){
            return word.equals(answer);
        }
}
