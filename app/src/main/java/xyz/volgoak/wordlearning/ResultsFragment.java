package xyz.volgoak.wordlearning;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class ResultsFragment extends Fragment {

    private int rightAnswers;
    private int wrongAnswers;

    public ResultsFragment() {
        // Required empty public constructor
    }

    public static ResultsFragment getResultFragment(int rightAnswers, int wrongAnswers){
        ResultsFragment fragment = new ResultsFragment();
        fragment.rightAnswers = rightAnswers;
        fragment.wrongAnswers = wrongAnswers;

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        TextView correctText = (TextView) getView().findViewById(R.id.result_text_correct);
        correctText.setText(Integer.toString(rightAnswers));
        TextView wrongText = (TextView) getView().findViewById(R.id.result_text_wrong);
        wrongText.setText(Integer.toString(wrongAnswers));
    }

}
