package xyz.volgoak.wordlearning.screens.training.helpers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
    public List<Long> idsForUpdate;
    public int trainedType;
    public long setId = -1;
    public int wordCount;
    public int correctAnswers;
    public int scores;
}
