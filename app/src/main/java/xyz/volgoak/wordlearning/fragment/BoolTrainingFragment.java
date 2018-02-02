package xyz.volgoak.wordlearning.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

import xyz.volgoak.wordlearning.IntegerEvent;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.SwipeHolder;
import xyz.volgoak.wordlearning.WordsApp;
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.databinding.FragmentBoolTrainingBinding;
import xyz.volgoak.wordlearning.training_utils.PlayWord;
import xyz.volgoak.wordlearning.training_utils.TrainingBool;
import xyz.volgoak.wordlearning.training_utils.TrainingFabric;

/**
 * A simple {@link Fragment} subclass.
 */
public class BoolTrainingFragment extends Fragment implements SwipeHolder.SwipeListener {

    public static final String TAG = BoolTrainingFragment.class.getSimpleName();
    public static final String SAVED_TRAINING = "saved_training";
    public static final String SAVED_TIME = "saved_time";
    public static final String EXTRA_SET_ID = "extra_set_id";

    @Inject
    DataProvider dataProvider;
    private FragmentBoolTrainingBinding dataBinding;
    private TrainingBool trainingBool;

    private int timer = 60;
    private boolean paused = false;

    private TrainingFragment.ResultReceiver resultReceiver;

    private SoundPool soundPool;
    private int wrongSound;
    private int correctSound;

    public static BoolTrainingFragment newInstance(long setId) {

        Bundle args = new Bundle();
        args.putLong(EXTRA_SET_ID, setId);

        BoolTrainingFragment fragment = new BoolTrainingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public BoolTrainingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        WordsApp.getsComponent().inject(this);
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bool_training, container, false);

        if (savedInstanceState != null) {
            trainingBool = (TrainingBool) savedInstanceState.getSerializable(SAVED_TRAINING);
            timer = savedInstanceState.getInt(SAVED_TIME);
        } else {
            long id = getArguments().getLong(EXTRA_SET_ID, -1);
            trainingBool = TrainingFabric.getBoolTraining(id, dataProvider);
        }

        for (PlayWord pw : trainingBool.getInitialWords()) {
            SwipeHolder holder = new SwipeHolder(pw);
            holder.setSwipeListener(this);
            dataBinding.swipeView.addView(holder);
        }

        dataBinding.btBoolRight.setOnClickListener((v) -> checkAnswer(true));
        dataBinding.btBoolWrong.setOnClickListener((v) -> checkAnswer(false));

        dataBinding.tvPointsBool.setText(String.valueOf(trainingBool.getScores()));

        return dataBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        resultReceiver = (TrainingFragment.ResultReceiver) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder().setMaxStreams(2).build();
        } else {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 1);
        }

        correctSound = soundPool.load(getContext(), R.raw.correct, 1);
        wrongSound = soundPool.load(getContext(), R.raw.wrong, 1);
    }

    @Override
    public void onResume() {
        super.onResume();

        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (timer <= 0) {
                    finishTraining();
                    return;
                }
                if (!paused) {
                    int sec = timer % 60;
                    int min = timer / 60;
                    String timeString = String.format("%02d:%02d", min, sec);
                    if (timer <= 5) {
                        dataBinding.tvTimeBool.setTextColor(ContextCompat.getColor(getContext(), R.color.red));
                    }
                    dataBinding.tvTimeBool.setText(timeString);
                    timer--;
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        soundPool.release();
        EventBus.getDefault().removeStickyEvent(IntegerEvent.class);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVED_TRAINING, trainingBool);
        outState.putInt(SAVED_TIME, timer);
    }

    @Override
    public void onSwipe(boolean answer) {
        if (trainingBool != null) {
            boolean correct = trainingBool.checkAnswer(answer);
            manageAnswer(correct);
            Log.d(TAG, "onSwipe: answer " + correct);
            PlayWord playWord = trainingBool.nextWord();
            if (playWord == null) {
                finishTraining();
                return;
            }
            SwipeHolder swipeHolder = new SwipeHolder(playWord);
            swipeHolder.setSwipeListener(this);
            dataBinding.swipeView.addView(swipeHolder);
        }
    }

    private void checkAnswer(boolean answer) {
        dataBinding.swipeView.doSwipe(answer);
    }

    private void manageAnswer(boolean correct) {
        EventBus.getDefault().postSticky(new IntegerEvent(trainingBool.getStars()));

        int sound = correct ? correctSound : wrongSound;
        soundPool.play(sound, 1, 1, 1, 0, 1);
        dataBinding.tvPointsBool.setText(String.valueOf(trainingBool.getScores()));

        if (correct) animateScores();
    }

    private void animateScores() {
        dataBinding.tvScoresPlusBool.setVisibility(View.VISIBLE);
        dataBinding.tvScoresPlusBool.setText("+" + trainingBool.scoresCounter());

        ObjectAnimator positionAnimator = ObjectAnimator.ofFloat(dataBinding.tvScoresPlusBool,
                "translationY", 0, -100);
        ObjectAnimator sizeAnimator = ObjectAnimator.ofFloat(dataBinding.tvScoresPlusBool,
                "scaleX", 0, 2);
        ObjectAnimator size3Animator = ObjectAnimator.ofFloat(dataBinding.tvScoresPlusBool,
                "scaleY", 0, 2);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.play(positionAnimator).with(sizeAnimator).with(size3Animator);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                dataBinding.tvScoresPlusBool.setVisibility(View.INVISIBLE);
            }
        });
        animatorSet.start();
    }


    public void finishTraining() {
        resultReceiver.showResults(trainingBool.getResults());
    }
}
