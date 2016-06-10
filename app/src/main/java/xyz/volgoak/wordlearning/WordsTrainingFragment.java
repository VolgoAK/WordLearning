package xyz.volgoak.wordlearning;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class WordsTrainingFragment extends Fragment {

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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        TrainingFabric fabric = new TrainingFabric(getContext());
        // TODO: 09.06.2016 implement possibility for run diffirent kind of trainings
        training = fabric.getTraining(TrainingFabric.WORD_TRANSLATION);
        listener = (FragmentListener)getActivity();
        return inflater.inflate(R.layout.fragment_words_training, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        Button nextButton = (Button) getView().findViewById(R.id.button_next);
         var1Button = (Button) getView().findViewById(R.id.button_var1);
         var2Button = (Button) getView().findViewById(R.id.button_var2);
         var3Button = (Button) getView().findViewById(R.id.button_var3);
         var4Button = (Button) getView().findViewById(R.id.button_var4);
         wordText = (TextView) getView().findViewById(R.id.textView_word);

        var1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(0);
            }
        });

        var2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(1);
            }
        });

        var3Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(2);
            }
        });

        var4Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(3);
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextWord();
            }
        });
    }

    // TODO: 09.06.2016 when training has no word run the result fragment

    public void nextWord(){
        trainingWord = training.getNextWord();
        if(trainingWord == null){
            listener.startResultsFragment();
            return;
        }
        String[] vars = trainingWord.getVars();
        var1Button.setText(vars[0]);
        var2Button.setText(vars[1]);
        var3Button.setText(vars[2]);
        var4Button.setText(vars[3]);
        wordText.setText(trainingWord.getWord());
    }

    public void checkAnswer(int number){
        // TODO: 09.06.2016 button must turn green if answer is correct and red when it's wrong
        boolean correct = training.checkAnswer(number);
        if(correct){
            wordText.setBackgroundColor(Color.argb(66, 66, 66, 55));
        }else{
            wordText.setBackgroundColor(Color.argb(77, 22, 22, 22));
        }
    }

}
