package xyz.volgoak.wordlearning.training_utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Volgoak on 23.04.2017.
 */

public class Results implements Serializable{
    public ArrayList<Long> idsForUpdate;
    public String trainedType;
    public int wordCount;
    public int correctAnswers;
}
