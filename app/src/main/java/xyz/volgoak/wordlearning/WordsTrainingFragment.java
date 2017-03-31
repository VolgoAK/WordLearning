package xyz.volgoak.wordlearning;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class WordsTrainingFragment extends Fragment {

    private int trainingType;

    private FragmentListener listener;

    private Training training;
    private TrainingWord trainingWord;


    Button var1Button;
    Button var2Button;
    Button var3Button;
    Button var4Button;
    TextView wordText;

    public WordsTrainingFragment() {
        // Required empty public constructor
    }

    public static WordsTrainingFragment getWordTrainingFragment(int  trainingType){
        WordsTrainingFragment fragment = new WordsTrainingFragment();
        fragment.trainingType = trainingType;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        TrainingFabric fabric = new TrainingFabric(getContext());
        training = fabric.getTraining(trainingType);
        listener = (FragmentListener)getActivity();
        return inflater.inflate(R.layout.fragment_words_training, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        //noinspection NullableProblems
        Button nextButton = (Button) getView().findViewById(R.id.button_next);
         var1Button = (Button) getView().findViewById(R.id.button_var1);
         var2Button = (Button) getView().findViewById(R.id.button_var2);
         var3Button = (Button) getView().findViewById(R.id.button_var3);
         var4Button = (Button) getView().findViewById(R.id.button_var4);
         wordText = (TextView) getView().findViewById(R.id.textView_word);

        var1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(0, var1Button);
            }
        });

        var2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(1, var2Button);
            }
        });

        var3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(2, var3Button);
            }
        });

        var4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(3, var4Button);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextWord();
            }
        });
        //load first word at start time
        nextWord();
    }

    // TODO: 09.06.2016 when training has no word run the result fragment

    public void nextWord(){
        trainingWord = training.getNextWord();
        if(trainingWord == null){
            int[] results = training.getResults();
            listener.startResultsFragment(results[0], results[1]);
            return;
        }
        String[] vars = trainingWord.getVars();
        var1Button.setText(vars[0]);
        var2Button.setText(vars[1]);
        var3Button.setText(vars[2]);
        var4Button.setText(vars[3]);
        wordText.setText(trainingWord.getWord());

        Drawable backGround;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            backGround = getResources().getDrawable(R.drawable.blue_button, getContext().getTheme());
        }else backGround = ResourcesCompat.getDrawable(getResources(), R.drawable.blue_button, null);
        var1Button.setBackground(backGround);
        var2Button.setBackground(backGround);
        var3Button.setBackground(backGround);
        var4Button.setBackground(backGround);

        Button nextbutton = (Button)getView().findViewById(R.id.button_next);
        nextbutton.setVisibility(View.INVISIBLE);
    }

    //checks is answer correct and sets background for button
    private void checkAnswer(int number, Button button){
        boolean correct = training.checkAnswer(number);
        int backgroundId = correct ? R.drawable.green_button : R.drawable.orange_button;
        Drawable background;

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
           background =  getResources().getDrawable(backgroundId, getContext().getTheme());
        }else background = ResourcesCompat.getDrawable(getResources(), backgroundId, null);

        button.setBackground(background);

        Button nextButton = (Button)getView().findViewById(R.id.button_next);
        nextButton.setVisibility(View.VISIBLE);
    }

}
