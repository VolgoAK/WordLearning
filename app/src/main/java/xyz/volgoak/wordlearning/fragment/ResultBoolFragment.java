package xyz.volgoak.wordlearning.fragment;


import android.content.Context;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.volgoak.wordlearning.FragmentListener;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.databinding.FragmentResultBoolBinding;
import xyz.volgoak.wordlearning.training_utils.Results;
import xyz.volgoak.wordlearning.training_utils.TrainingFabric;
import xyz.volgoak.wordlearning.utils.PreferenceContract;

/**
 * A simple {@link Fragment} subclass.
 */
public class ResultBoolFragment extends Fragment {

    public static final String EXTRA_RESULTS = "extra_results";

    private FragmentResultBoolBinding dataBinding;
    private Results results;
    private FragmentListener fragmentListener;

    public static ResultBoolFragment newInstance(Results results) {
        Bundle extras = new Bundle();
        extras.putSerializable(EXTRA_RESULTS, results);
        ResultBoolFragment fragment = new ResultBoolFragment();
        fragment.setArguments(extras);
        return fragment;
    }


    public ResultBoolFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_result_bool, container, false);
        Bundle extras = getArguments();
        if (extras != null) {
            results = (Results) extras.getSerializable(EXTRA_RESULTS);
        }
        return dataBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        String scores = getString(R.string.points_format, results.scores);
        dataBinding.tvScores.setText(scores);

        //check records
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        int hightScores = preferences.getInt(PreferenceContract.BOOL_RECORD, results.scores);

        if (results.scores >= hightScores) {
            dataBinding.tvYouGot.setText(R.string.new_record);
            preferences.edit().putInt(PreferenceContract.BOOL_RECORD, results.scores).apply();
            hightScores = results.scores;
        }

        dataBinding.tvRecord.setText(getString(R.string.record_format, hightScores));

        //bind listeners
        dataBinding.resultStartRedactor.setOnClickListener((v) -> fragmentListener.startDictionary());

        dataBinding.resultStartTw.setOnClickListener((v) ->
                fragmentListener.startTraining(TrainingFabric.TRANSLATION_WORD, results.setId));

        dataBinding.resultStartWt.setOnClickListener((v) ->
                fragmentListener.startTraining(TrainingFabric.WORD_TRANSLATION, results.setId));

        dataBinding.resultStartRw.setOnClickListener((v) ->
                fragmentListener.startTraining(TrainingFabric.BOOL_TRAINING, results.setId));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            fragmentListener = (FragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement FragmentListener");
        }
    }
}
