package xyz.volgoak.wordlearning;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.WindowCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Toast;

import xyz.volgoak.wordlearning.databinding.FragmentTrainingBinding;
import xyz.volgoak.wordlearning.training_utils.Results;
import xyz.volgoak.wordlearning.training_utils.Training;
import xyz.volgoak.wordlearning.training_utils.TrainingFabric;
import xyz.volgoak.wordlearning.training_utils.TrainingWord;
import xyz.volgoak.wordlearning.utils.MetallBounceInterpoltor;
import xyz.volgoak.wordlearning.utils.WordSpeaker;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingFragment extends Fragment {

    public static final String TAG = TrainingFragment.class.getSimpleName();
    public static final String TRAINING_TAG = "train_tag";
    public static final String ANSWERED = "answered";
    public static final String SAVED_BACKGROUNDS = "saved_backgrounds";

    public int mTrainingType;
    public long mSetId;

    private ResultReceiver mResultReceiver;

    private Training mTraining;
    private TrainingWord mTrainingWord;

    private FragmentTrainingBinding mBinding;

    public final ObservableField<String[]> mVarArray = new ObservableField<>();
    public final ObservableField<String> mWord = new ObservableField<>();
    public final ObservableBoolean mAnswered = new ObservableBoolean();

    private Drawable mDefaultBackground;
    private Drawable mUnavailableBackground;
    private Drawable mWrongAnswerBackground;
    private Drawable mCorrectAnswerBackground;

    //animations fields
    private boolean mIsAnimated = false;
    private float mNextButtonPath = 0f;

    public TrainingFragment() {
        // Required empty public constructor
    }

    public static TrainingFragment getWordTrainingFragment(int  trainingType, long setId){
        TrainingFragment fragment = new TrainingFragment();
        fragment.mTrainingType = trainingType;
        fragment.mSetId = setId;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_training, container, false);
        mBinding.setFragment(this);

        mDefaultBackground = ContextCompat.getDrawable(getContext(), R.drawable.blue_button);
        mCorrectAnswerBackground = ContextCompat.getDrawable(getContext(), R.drawable.green_button);
        mWrongAnswerBackground = ContextCompat.getDrawable(getContext(), R.drawable.orange_button);
        mUnavailableBackground = ContextCompat.getDrawable(getContext(), R.drawable.blue_button_unavailable);

        if(savedInstanceState != null){
            mTraining =(Training) savedInstanceState.getSerializable(TRAINING_TAG);
            mTrainingWord = mTraining.getCurrentWord();
            boolean answered = savedInstanceState.getBoolean(ANSWERED, false);
            mAnswered.set(answered);
            if(answered){
                hideShowNextButton(true);
            }
        }else {
            mAnswered.set(false);
            mTraining = TrainingFabric.getTraining(mTrainingType, mSetId);
            //if training is null, we have to go to the dictionary
            if(mTraining != null) {
                mTrainingWord = mTraining.getFirstWord();
            }
        }

        mResultReceiver = (ResultReceiver) getActivity();

        return mBinding.getRoot();
    }

    @Override
    public void onStart(){
        super.onStart();
        //finish training and go to the dictionary if there is nothing to train
        if(mTraining == null){
            goToDictionary();
            return;
        }
        int titleId = mTrainingType == TrainingFabric.TRANSLATION_WORD ? R.string.translation_word : R.string.word_translation;
        getActivity().setTitle(getString(titleId));

        //max progress 100 percent
        mBinding.progressTf.setMax(100);
        //load first word at start time
        showWord();

        //createAnimator buttons appearance
        mIsAnimated = false;
        ViewTreeObserver vto = mBinding.getRoot().getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(!mIsAnimated){
                    mIsAnimated = true;
                    startAppearenceAnim();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    private void startAppearenceAnim(){
        Log.d(TAG, "startAppearenceAnim: ");
        float path = mBinding.btVar1Tf.getWidth() + mBinding.btVar1Tf.getX();
        ValueAnimator animator  = ValueAnimator.ofFloat(path, 0);
        animator.setInterpolator(new MetallBounceInterpoltor());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                mBinding.btVar1Tf.setTranslationX(-f);
                mBinding.btVar2Tf.setTranslationX(f);
                mBinding.btVar3Tf.setTranslationX(-f);
                mBinding.btVar4Tf.setTranslationX(f);
            }
        });

        ValueAnimator visibilityAnimator = ValueAnimator.ofFloat(0, 1);
        visibilityAnimator.setInterpolator(new LinearInterpolator());
        visibilityAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                mBinding.progressTf.setAlpha(f);
                mBinding.ibtSoundTf.setAlpha(f);
                mBinding.ivTf.setAlpha(f);
                mBinding.tvWordTf.setAlpha(f);
            }
        });

        AnimatorSet set = new AnimatorSet();
        set.setDuration(1200);
        set.play(animator).with(visibilityAnimator);
        set.start();
    }

    public void showWord(){

        mVarArray.set(mTrainingWord.getVars());
        mWord.set(mTrainingWord.getWord());

        mBinding.btVar1Tf.setBackground(mDefaultBackground);
        mBinding.btVar2Tf.setBackground(mDefaultBackground);
        mBinding.btVar3Tf.setBackground(mDefaultBackground);
        mBinding.btVar4Tf.setBackground(mDefaultBackground);

//        mBinding.progressTf.setProgress(mTraining.getProgressInPercents());
        float newProgress = mTraining.getProgressInPercents();
        if(newProgress > mBinding.progressTf.getProgress()){
            ObjectAnimator animator = ObjectAnimator.ofFloat(mBinding.progressTf, "progress",
                    mBinding.progressTf.getProgress(), newProgress);
            animator.start();
        }

        //hide the next button
        hideShowNextButton(false);

        pronounceWord();
    }

    public void nextWord(){
        mTrainingWord = mTraining.getNextWord();
        if(mTrainingWord == null){
            Results results = mTraining.getResults();
            results.setId = mSetId;
            mResultReceiver.showResults(results);
            return;
        }
        mAnswered.set(false);

        showWord();
    }

    public void pronounceWord(){
//        Log.d(TAG, "pronounceWord: ");
        if(mTrainingType == TrainingFabric.WORD_TRANSLATION) WordSpeaker.speakWord(mTrainingWord.getWord());
    }

    //checks is answer correct and sets background for button
    //depends on correctness
    public void checkAnswer(View view){
        mBinding.btVar1Tf.setBackground(mUnavailableBackground);
        mBinding.btVar2Tf.setBackground(mUnavailableBackground);
        mBinding.btVar3Tf.setBackground(mUnavailableBackground);
        mBinding.btVar4Tf.setBackground(mUnavailableBackground);
//        Log.d(TAG, "checkAnswer: ");
        Button button = (Button) view;
        String tag = (String) button.getTag();
        int number = Integer.parseInt(tag);

        boolean correct = mTraining.checkAnswer(number);

        Drawable background = correct ? mCorrectAnswerBackground : mWrongAnswerBackground;
        TransitionDrawable td = new TransitionDrawable(new Drawable[]{mDefaultBackground, background});

        button.setBackground(td);
        td.startTransition(500);

        mAnswered.set(true);

        hideShowNextButton(true);
    }

    private void hideShowNextButton(boolean show){
        //show a next button
        if(mNextButtonPath == 0f) {
            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            mNextButtonPath = metrics.widthPixels - mBinding.btNextTf.getY();
            mBinding.btNextTf.setVisibility(View.VISIBLE);
        }
        ObjectAnimator animator;
        if(show){
            animator = ObjectAnimator.ofFloat(mBinding.btNextTf, "TranslationX", mNextButtonPath, 0);
        }else animator = ObjectAnimator.ofFloat(mBinding.btNextTf, "TranslationX", 0, mNextButtonPath);

        animator.setInterpolator(new MetallBounceInterpoltor());
        animator.setDuration(800);
        animator.start();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(TRAINING_TAG, mTraining );
        boolean answered = mAnswered.get();
        outState.putBoolean(ANSWERED, answered);
    }



    @Override
    public void onDestroy() {
        WordSpeaker.close();
        super.onDestroy();
    }

    private void goToDictionary(){
        Toast.makeText(getContext(), getString(R.string.all_words_studied_message), Toast.LENGTH_LONG).show();
        //mListener.startSets();
        Results results = new Results(Results.ResultType.NO_WORDS);
        mResultReceiver.showResults(results);
    }

    public int getTrainingType(){
        return mTrainingType;
    }

    interface ResultReceiver{
        void showResults(Results results);
    }
}
