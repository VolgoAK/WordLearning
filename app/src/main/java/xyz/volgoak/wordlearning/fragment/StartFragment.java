package xyz.volgoak.wordlearning.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.volgoak.wordlearning.BR;
import xyz.volgoak.wordlearning.FragmentListener;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.WordsApp;
import xyz.volgoak.wordlearning.databinding.FragmentStartBinding;
import xyz.volgoak.wordlearning.model.WordsViewModel;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends Fragment {

    public static final String TAG = StartFragment.class.getSimpleName();

    private FragmentListener mListener;
    private FragmentStartBinding mBinding;

    private WordsViewModel viewModel;


    public StartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        WordsApp.getsComponent().inject(this);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_start, container, false);

        viewModel = ViewModelProviders.of(getActivity()).get(WordsViewModel.class);
        viewModel.getDictionaryInfo().observe(this, info -> {
            mBinding.tvWordsDicStartF.setText(getString(R.string.words_in_dictionary,
                    info.getWordsInDictionary()));
            mBinding.tvWordsLearnedStartF.setText(getString(R.string.words_learned,
                    info.getLearnedWords(), info.getAllWords()));
        });
        return mBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        mBinding.setListener(mListener);
        mBinding.notifyPropertyChanged(BR._all);
        mListener.setActionBarTitle(getString(R.string.app_name));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            mListener = (FragmentListener) context;
        } else throw new RuntimeException(context.toString() + " must implement FragmentListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
