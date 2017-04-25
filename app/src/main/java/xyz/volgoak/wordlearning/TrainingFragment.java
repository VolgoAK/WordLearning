package xyz.volgoak.wordlearning;


import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import xyz.volgoak.wordlearning.databinding.FragmentTrainingBinding;
import xyz.volgoak.wordlearning.training_utils.Results;
import xyz.volgoak.wordlearning.training_utils.Training;
import xyz.volgoak.wordlearning.training_utils.TrainingFabric;
import xyz.volgoak.wordlearning.training_utils.TrainingWord;
import xyz.volgoak.wordlearning.utils.WordSpeaker;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingFragment extends Fragment {

    public static final String TAG = "TrainingFragment";
    public static final String TRAINING_TAG = "train_tag";
    public static final String ANSWERED = "answered";
    public static final String SAVED_BACKGROUNDS = "saved_backgrounds";

    public int mTrainingType;
    private boolean mAnswerSaved;

    private FragmentListener listener;

    private Training mTraining;
    private TrainingWord mTrainingWord;

    private FragmentTrainingBinding mBinding;

    public final ObservableField<String[]> mVarArray = new ObservableField<>();
    public final ObservableField<String> mWord = new ObservableField<>();
    public final ObservableField<Boolean> mAnswered = new ObservableField<>();

    private Drawable mDefaultBackground;
    private Drawable mWrongAnswerBackground;
    private Drawable mCorrectAnswerBackground;

    private WordSpeaker mSpeaker;

    public TrainingFragment() {
        // Required empty public constructor
    }

    public static TrainingFragment getWordTrainingFragment(int  trainingType){
        TrainingFragment fragment = new TrainingFragment();
        fragment.mTrainingType = trainingType;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_training, container, false);
        mBinding.setFragment(this);

        //we need word speaker only if we train word-translation
        if(mTrainingType == TrainingFabric.WORD_TRANSLATION) mSpeaker = new WordSpeaker(getContext());

        //load backgrounds for buttons
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mDefaultBackground = getResources().getDrawable(R.drawable.blue_button, getContext().getTheme());
            mWrongAnswerBackground = getResources().getDrawable(R.drawable.orange_button, getContext().getTheme());
            mCorrectAnswerBackground = getResources().getDrawable(R.drawable.green_button, getContext().getTheme());
        }else{
            mDefaultBackground = ResourcesCompat.getDrawable(getResources(), R.drawable.blue_button, null);
            mWrongAnswerBackground = ResourcesCompat.getDrawable(getResources(), R.drawable.orange_button, null);
            mCorrectAnswerBackground = ResourcesCompat.getDrawable(getResources(), R.drawable.green_button, null);
        }

        if(savedInstanceState != null){
            mTraining =(Training) savedInstanceState.getSerializable(TRAINING_TAG);
            mTrainingWord = mTraining.getCurrentWord();
            boolean answered = savedInstanceState.getBoolean(ANSWERED, false);
            mAnswered.set(answered);
        }else {
            TrainingFabric fabric = new TrainingFabric(getContext());
            mAnswered.set(false);
            mTraining = fabric.getTraining(mTrainingType);
            mTrainingWord = mTraining.getFirstWord();
        }
        listener = (FragmentListener)getActivity();

        return mBinding.getRoot();
    }

    @Override
    public void onStart(){
        super.onStart();
        //load first word at start time
        showWord();
    }

    public void showWord(){

        mVarArray.set(mTrainingWord.getVars());
        mWord.set(mTrainingWord.getWord());

        mBinding.btVar1Tf.setBackground(mDefaultBackground);
        mBinding.btVar2Tf.setBackground(mDefaultBackground);
        mBinding.btVar3Tf.setBackground(mDefaultBackground);
        mBinding.btVar4Tf.setBackground(mDefaultBackground);

        mBinding.notifyPropertyChanged(BR.fragment);

        pronounceWord();
    }

    public void nextWord(){
        mTrainingWord = mTraining.getNextWord();
        if(mTrainingWord == null){
            Results results = mTraining.getResults();
            listener.startResultsFragment(results);
            return;
        }
        mAnswered.set(false);
        showWord();
    }

    public void pronounceWord(){
        if(mSpeaker != null) mSpeaker.speakWord(mTrainingWord.getWord());
    }

    //checks is answer correct and sets background for button
    //depends on correctness
    public void checkAnswer(View view){
        Log.d(TAG, "checkAnswer: ");
        Button button = (Button) view;
        String tag = (String) button.getTag();
        int number = Integer.parseInt(tag);

        boolean correct = mTraining.checkAnswer(number);

        Drawable background = correct ? mCorrectAnswerBackground : mWrongAnswerBackground;

        button.setBackground(background);

        mAnswered.set(true);
        mBinding.notifyPropertyChanged(BR.fragment);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(TRAINING_TAG, mTraining );
        boolean answered = mAnswered.get();
        outState.putBoolean(ANSWERED, answered);
    }

    public int getTrainingType(){
        return mTrainingType;
    }
}
