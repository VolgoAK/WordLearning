package xyz.volgoak.wordlearning;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import xyz.volgoak.wordlearning.data.WordsDbAdapter;
import xyz.volgoak.wordlearning.training_utils.Results;


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
        mListener = (FragmentListener) getActivity();
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();

        mListener.setActionBarTitle(getString(R.string.results));

        String results = getString(R.string.correct_answers) + "  " + mResults.correctAnswers + "/" + mResults.wordCount;
        TextView resultTv = (TextView)getView().findViewById(R.id.tv_result_resfrag);
        resultTv.setText(results);

        String opinion;
        double percentage = mResults.correctAnswers * 1.0/ mResults.wordCount;
        Log.d(TAG, "onStart: percentage " + percentage);

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
                mListener.startRedactorFragment();
            }
        });

        Button startWTbutton = (Button) getView().findViewById(R.id.result_start_wt);
        startWTbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // TODO: 04.05.2017 change listener and make two choices
               // mListener.startTrainingWT();
            }
        });

        updateWordsStatus();
    }

    private void updateWordsStatus(){
        WordsDbAdapter adapter = new WordsDbAdapter(getContext());
        for(Long id : mResults.idsForUpdate){
            adapter.changeTrainedStatus(id, WordsDbAdapter.INCREASE, mResults.trainedType);
        }
        adapter.close();
    }

}
