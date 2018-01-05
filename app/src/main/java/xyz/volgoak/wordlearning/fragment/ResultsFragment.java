package xyz.volgoak.wordlearning.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import xyz.volgoak.wordlearning.FragmentListener;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;
import xyz.volgoak.wordlearning.training_utils.Results;
import xyz.volgoak.wordlearning.training_utils.TrainingFabric;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

public class ResultsFragment extends Fragment {

    public static final String TAG = "ResultFragment";

    private Results mResults;
    private FragmentListener mListener;

    public ResultsFragment() {
        // Required empty public constructor
    }

    public static ResultsFragment getResultFragment(Results results){
        ResultsFragment fragment = new ResultsFragment();
        fragment.mResults = results;
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof FragmentListener){
            mListener = (FragmentListener) context;
        }else throw new RuntimeException(context.toString() + " must implement FragmentListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart(){
        super.onStart();

        //mListener.setActionBarTitle(getString(R.string.results));

        String results = getString(R.string.correct_answers) + "  " + mResults.correctAnswers + "/" + mResults.wordCount;
        TextView resultTv = (TextView)getView().findViewById(R.id.tv_result_resfrag);
        resultTv.setText(results);

        String opinion;
        double percentage = mResults.correctAnswers * 1.0/ mResults.wordCount;
//        Log.d(TAG, "onStart: percentage " + percentage);

        if(percentage == 1){
            opinion = getString(R.string.perfect_result);
        }else if( percentage >= 0.8){
            opinion = getString(R.string.good_result);
        }else if(percentage >= 0.4){
            opinion = getString(R.string.bad_result);
        }else opinion = getString(R.string.disaster_result);

        TextView opinionTv = (TextView) getView().findViewById(R.id.tv_result_opinion);
        opinionTv.setText(opinion);

        Button redactorButton  = (Button)getView().findViewById(R.id.result_start_redactor);
        redactorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.startDictionary();
            }
        });

        Button startWTbutton = (Button) getView().findViewById(R.id.result_start_wt);
        startWTbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
               mListener.startTraining(TrainingFabric.WORD_TRANSLATION, mResults.setId);
            }
        });

        Button startTWbutton = (Button) getView().findViewById(R.id.result_start_tw);
        startTWbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.startTraining(TrainingFabric.TRANSLATION_WORD, mResults.setId);
            }
        });

        updateWordsStatus();
    }

    private void updateWordsStatus(){
        WordsDbAdapter adapter = new WordsDbAdapter();
        for(Long id : mResults.idsForUpdate){
            adapter.changeTrainedStatus(id, WordsDbAdapter.INCREASE, mResults.trainedType);
        }
    }

}