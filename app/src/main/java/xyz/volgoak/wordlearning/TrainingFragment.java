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


/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingFragment extends Fragment {

    public static final String TAG = "TrainingFragment";

    private int trainingType;

    private FragmentListener listener;

    private Training training;
    private TrainingWord trainingWord;

    private FragmentTrainingBinding mBinding;

    public final ObservableField<String[]> mVarArray = new ObservableField<>();
    public final ObservableField<String> mWord = new ObservableField<>();



    public TrainingFragment() {
        // Required empty public constructor
    }

    public static TrainingFragment getWordTrainingFragment(int  trainingType){
        TrainingFragment fragment = new TrainingFragment();
        fragment.trainingType = trainingType;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_training, container, false);
        mBinding.setFragment(this);

        TrainingFabric fabric = new TrainingFabric(getContext());
        training = fabric.getTraining(trainingType);
        listener = (FragmentListener)getActivity();

        return mBinding.getRoot();
    }

    @Override
    public void onStart(){
        super.onStart();
        mBinding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextWord();
            }
        });
        //load first word at start time
        nextWord();
    }

    public void nextWord(){
        trainingWord = training.getNextWord();
        if(trainingWord == null){
            int[] results = training.getResults();
            listener.startResultsFragment(results[0], results[1]);
            return;
        }

        mVarArray.set(trainingWord.getVars());
        mWord.set(trainingWord.getWord());
        mBinding.notifyPropertyChanged(BR.fragment);

        //change buttons background to default
        Drawable backGround;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            backGround = getResources().getDrawable(R.drawable.blue_button, getContext().getTheme());
        }else backGround = ResourcesCompat.getDrawable(getResources(), R.drawable.blue_button, null);

        mBinding.buttonVar1.setBackground(backGround);
        mBinding.buttonVar2.setBackground(backGround);
        mBinding.buttonVar3.setBackground(backGround);
        mBinding.buttonVar4.setBackground(backGround);

        mBinding.buttonNext.setVisibility(View.INVISIBLE);
    }

    //checks is answer correct and sets background for button
    //depends on correctness
    public void checkAnswer(View view){
        Log.d(TAG, "checkAnswer: ");
        Button button = (Button) view;
        String tag = (String) button.getTag();
        int number = Integer.parseInt(tag);

        boolean correct = training.checkAnswer(number);
        int backgroundId = correct ? R.drawable.green_button : R.drawable.orange_button;
        Drawable background;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
           background =  getResources().getDrawable(backgroundId, getContext().getTheme());
        }else background = ResourcesCompat.getDrawable(getResources(), backgroundId, null);

        button.setBackground(background);

        //show nextButton
        mBinding.buttonNext.setVisibility(View.VISIBLE);
    }

}
