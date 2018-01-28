package xyz.volgoak.wordlearning.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import javax.inject.Inject;

import xyz.volgoak.wordlearning.FragmentListener;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.WordsApp;
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.databinding.FragmentRedactorBinding;
import xyz.volgoak.wordlearning.databinding.FragmentResultsBinding;
import xyz.volgoak.wordlearning.entities.Word;
import xyz.volgoak.wordlearning.training_utils.Results;
import xyz.volgoak.wordlearning.training_utils.Training;
import xyz.volgoak.wordlearning.training_utils.TrainingFabric;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

public class ResultsFragment extends Fragment {

    public static final String TAG = "ResultFragment";

    private Results mResults;
    private FragmentListener mListener;
    @Inject
    DataProvider mProvider;

    private FragmentResultsBinding mDataBinding;

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
        WordsApp.getsComponent().inject(this);
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_results, container, false);
        return mDataBinding.getRoot();
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

        manageFeedBack();

        mDataBinding.resultStartRedactor.setOnClickListener((v) -> mListener.startDictionary());

        mDataBinding.resultStartTw.setOnClickListener(
                (v) -> mListener.startTraining(TrainingFabric.WORD_TRANSLATION, mResults.setId));

        mDataBinding.resultStartWt.setOnClickListener(
                (v) -> mListener.startTraining(TrainingFabric.TRANSLATION_WORD, mResults.setId));


        updateWordsStatus();
    }

    private void manageFeedBack() {
        String results = getString(R.string.correct_answers) + "  " + mResults.correctAnswers + "/" + mResults.wordCount;
        mDataBinding.tvResultResfrag.setText(results);

        String opinion;
        double percentage = mResults.correctAnswers * 1.0/ mResults.wordCount;

        if(percentage == 1){
            opinion = getString(R.string.perfect_result);
        }else if( percentage >= 0.8){
            opinion = getString(R.string.good_result);
        }else if(percentage >= 0.4){
            opinion = getString(R.string.bad_result);
        }else opinion = getString(R.string.disaster_result);

        mDataBinding.tvResultOpinion.setText(opinion);
    }

    private void updateWordsStatus(){
        if(mResults.idsForUpdate.size() == 0) return;

        Updater updater;
        if(mResults.trainedType == TrainingFabric.TRANSLATION_WORD) {
            updater = (w) -> w.setTrainedTw(w.getTrainedTw() + 1);
        }else if (mResults.trainedType == TrainingFabric.WORD_TRANSLATION) {
            updater = (w) -> w.setTrainedWt(w.getTrainedWt() + 1);
        } else return;

        Word[] words = new Word[mResults.idsForUpdate.size()];
        for(int i = 0; i < mResults.idsForUpdate.size(); i++) {
            Word word = mProvider.getWordById(mResults.idsForUpdate.get(i));
            updater.updateWord(word);
            words[i] = word;
        }

        mProvider.updateWords(words);
    }

    interface Updater {
        void updateWord(Word word);
    }
}
