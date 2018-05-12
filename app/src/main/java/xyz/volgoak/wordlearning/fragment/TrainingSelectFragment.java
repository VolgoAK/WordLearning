package xyz.volgoak.wordlearning.fragment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.volgoak.wordlearning.FragmentListener;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.databinding.FragmentTrainingSelectBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingSelectFragment extends Fragment {

    private FragmentListener fragmentListener;
    private FragmentTrainingSelectBinding dataBinding;

    public TrainingSelectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_training_select, container, false);
        return dataBinding.getRoot();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListener) {
            fragmentListener = (FragmentListener) context;
        } else throw new RuntimeException(context.toString() + " must implement FragmentListener");
    }

    @Override
    public void onStart() {
        super.onStart();
        dataBinding.setListener(fragmentListener);
    }
}
