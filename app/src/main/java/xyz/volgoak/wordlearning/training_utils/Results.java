package xyz.volgoak.wordlearning.training_utils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

public class Results implements Serializable{
    public enum ResultType{
        NO_WORDS, SUCCESS
    }

    public Results(ResultType type){
        resultType = type;
    }

    public ResultType resultType;
    public ArrayList<Long> idsForUpdate;
    public int trainedType;
    public long setId = -1;
    public int wordCount;
    public int correctAnswers;
    public int scores;
}
