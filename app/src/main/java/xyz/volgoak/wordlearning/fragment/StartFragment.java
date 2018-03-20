package xyz.volgoak.wordlearning.fragment;


import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import xyz.volgoak.wordlearning.BR;
import xyz.volgoak.wordlearning.FragmentListener;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.WordsApp;
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.databinding.FragmentStartBinding;
import xyz.volgoak.wordlearning.entities.DictionaryInfo;
import xyz.volgoak.wordlearning.model.WordsViewModel;
import xyz.volgoak.wordlearning.utils.AppearingAnimator;

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

    private boolean mAppearanceAnimated;

    public StartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        WordsApp.getsComponent().inject(this);
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_start, container, false);
        //run animation when views created
        ViewTreeObserver vto = mBinding.getRoot().getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (!mAppearanceAnimated) {
                    mAppearanceAnimated = true;
                    runAppearAnimation(false);
                }
            }
        });

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

        mAppearanceAnimated = false;
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

    private void runAppearAnimation(boolean disappearing) {
        float first = disappearing ? 1 : 0;
        float second = disappearing ? 0 : 1;
        ValueAnimator visibilityAnimator = ValueAnimator.ofFloat(first, second);
        visibilityAnimator.setInterpolator(new AccelerateInterpolator());
        visibilityAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                mBinding.cvInfoMain.setAlpha(f);
            }
        });


        AnimatorSet set = new AnimatorSet();
        set.setDuration(1500);

        set.play(visibilityAnimator)
                .with(AppearingAnimator.createAnimator(getActivity(), mBinding.cvTrainingMain,
                        AppearingAnimator.FROM_RIGHT, disappearing))
                .with(AppearingAnimator.createAnimator(getActivity(), mBinding.cvRedactorMain,
                        AppearingAnimator.FROM_LEFT, disappearing))
                .with(AppearingAnimator.createAnimator(getActivity(), mBinding.cvSetsMain,
                        AppearingAnimator.FROM_RIGHT, disappearing));

        set.start();
    }
}
