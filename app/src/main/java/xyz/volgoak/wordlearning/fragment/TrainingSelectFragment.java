package xyz.volgoak.wordlearning.fragment;


import android.animation.AnimatorSet;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import xyz.volgoak.wordlearning.FragmentListener;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.databinding.FragmentTrainingSelectBinding;
import xyz.volgoak.wordlearning.utils.AppearingAnimator;

/**
 * A simple {@link Fragment} subclass.
 */
public class TrainingSelectFragment extends Fragment {

    private FragmentListener fragmentListener;
    private FragmentTrainingSelectBinding dataBinding;

    private boolean appearanceAnimated;

    public TrainingSelectFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_training_select, container, false);
        ViewTreeObserver vto = dataBinding.getRoot().getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!appearanceAnimated) {
                    appearanceAnimated = true;
                    runAnimation(false);
                }
            }
        });
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

    private void runAnimation(boolean disappearing) {
        AnimatorSet set = new AnimatorSet();
        set.setDuration(1500);

        set.play(AppearingAnimator.createAnimator(getActivity(), dataBinding.cvTrainingWt,
                AppearingAnimator.FROM_RIGHT, disappearing))
                .with(AppearingAnimator.createAnimator(getActivity(), dataBinding.cvTrainingTw,
                        AppearingAnimator.FROM_LEFT, disappearing))
                .with(AppearingAnimator.createAnimator(getActivity(), dataBinding.cvTrainingBool,
                        AppearingAnimator.FROM_RIGHT, disappearing));

        set.start();
    }
}
