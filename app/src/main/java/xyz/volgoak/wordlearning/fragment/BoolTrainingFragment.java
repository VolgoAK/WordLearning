package xyz.volgoak.wordlearning.fragment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

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

    @Inject
    DataProvider dataProvider;
    private FragmentBoolTrainingBinding dataBinding;
    private TrainingBool trainingBool;

    private int timer = 60;
    private boolean paused = false;

    private int drawableStar = R.drawable.ic_star_24dp;
    private int drawableNoStar = R.drawable.ic_star_border_24dp;

    private TrainingFragment.ResultReceiver resultReceiver;

    public BoolTrainingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        WordsApp.getsComponent().inject(this);
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bool_training, container, false);

        // TODO: 1/27/18 pass set id for training
        trainingBool = TrainingFabric.getBoolTraining(-1, dataProvider);

        for (PlayWord pw : trainingBool.getInitialWords()) {
            SwipeHolder holder = new SwipeHolder(getContext(), pw);
            holder.setSwipeListener(this);
            dataBinding.swipeView.addView(holder);
        }

        dataBinding.btBoolRight.setOnClickListener((v) -> checkAnswer(true));
        dataBinding.btBoolWrong.setOnClickListener((v) -> checkAnswer(false));

        return dataBinding.getRoot();
    }

    @Override
    public void onSwipe(boolean answer) {
        if (trainingBool != null) {
            boolean correct = trainingBool.checkAnswer(answer);
            Log.d(TAG, "onSwipe: answer " + correct);
            PlayWord playWord = trainingBool.nextWord();
            if(playWord == null) {
                finishTraining();
                return;
            }
            SwipeHolder swipeHolder = new SwipeHolder(getContext(), playWord);
            swipeHolder.setSwipeListener(this);
            dataBinding.swipeView.addView(swipeHolder);
            dataBinding.tvPointsBool.setText(getString(R.string.scores_format, trainingBool.getScores()));
            manageStars();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        resultReceiver = (TrainingFragment.ResultReceiver) context;
    }

    private void checkAnswer(boolean answer) {
        dataBinding.swipeView.doSwipe(answer);
    }

    private void manageStars() {
        int stars = trainingBool.getStars();
        dataBinding.ivStarOneBool.setImageResource(stars >= 1 ? drawableStar : drawableNoStar);
        dataBinding.ivStarTwoBool.setImageResource(stars >= 2 ? drawableStar : drawableNoStar);
        dataBinding.ivStarThreeBool.setImageResource(stars >= 3 ? drawableStar : drawableNoStar);
    }

    @Override
    public void onResume() {
        super.onResume();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(timer <= 0) {
                    finishTraining();
                    return;
                }
                if (!paused) {
                    int sec = timer % 60;
                    int min = timer / 60;
                    String timeString = String.format("%02d:%02d", min, sec);
                    dataBinding.tvTimeBool.setText(timeString);
                    timer--;
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }

    public void finishTraining() {
        resultReceiver.showResults(trainingBool.getResults());
    }
}
