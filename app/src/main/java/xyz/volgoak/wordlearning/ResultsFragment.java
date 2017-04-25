package xyz.volgoak.wordlearning;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import xyz.volgoak.wordlearning.data.WordsDbAdapter;
import xyz.volgoak.wordlearning.training_utils.Results;


public class ResultsFragment extends Fragment {

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

        TextView correctText = (TextView) getView().findViewById(R.id.result_text_correct);
        correctText.setText(Integer.toString(mResults.correctAnswers));
        TextView wrongText = (TextView) getView().findViewById(R.id.result_text_wrong);
        wrongText.setText(Integer.toString(mResults.wordCount));

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
                mListener.startTrainingWTFragment();
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
