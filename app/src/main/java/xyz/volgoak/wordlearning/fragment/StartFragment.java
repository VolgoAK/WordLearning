package xyz.volgoak.wordlearning.fragment;


import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
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

import xyz.volgoak.wordlearning.BR;
import xyz.volgoak.wordlearning.FragmentListener;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.WordsApp;
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.data.DictionaryInfo;
import xyz.volgoak.wordlearning.databinding.FragmentStartBinding;
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

    @Inject
    DataProvider mDataProvider;

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
                Log.d(TAG, "onGlobalLayout: createAnimator");
                if (!mAppearanceAnimated) {
                    mAppearanceAnimated = true;
                    runAppearAnimation();
                }
            }
        });
        return mBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        mBinding.setListener(mListener);
        mBinding.notifyPropertyChanged(BR._all);
        mListener.setActionBarTitle(getString(R.string.app_name));

        //load dictionary info

        DictionaryInfo info = mDataProvider.getDictionaryInfo();
        mBinding.tvWordsDicStartF.setText(getString(R.string.words_in_dictionary, info.getWordsInDictionary()));
        mBinding.tvWordsLearnedStartF.setText(getString(R.string.words_learned, info.getLearnedWords(), info.getAllWords()));

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

    private void runAppearAnimation() {
        float pathFromLeft = mBinding.cvTransWordMain.getX() + mBinding.cvTransWordMain.getWidth();
        /*ValueAnimator animator = ValueAnimator.ofFloat(pathFromLeft, 0);
        animator.setInterpolator(new MetallBounceInterpoltor());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float f = (float) animation.getAnimatedValue();
                mBinding.cvWordTransMain.setTranslationX(f);
                mBinding.cvTransWordMain.setTranslationX(-f);
                mBinding.cvRedactorMain.setTranslationX(f);
                mBinding.cvSetsMain.setTranslationX(-f);
            }
        });*/

        ValueAnimator visibilityAnimator = ValueAnimator.ofFloat(0, 1);
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
                .with(AppearingAnimator.createAnimator(getActivity(), mBinding.cvWordTransMain,
                        AppearingAnimator.FROM_LEFT, false))
                .with(AppearingAnimator.createAnimator(getActivity(), mBinding.cvTransWordMain,
                        AppearingAnimator.FROM_RIGHT, false))
                .with(AppearingAnimator.createAnimator(getActivity(), mBinding.cvRedactorMain,
                        AppearingAnimator.FROM_LEFT, false))
                .with(AppearingAnimator.createAnimator(getActivity(), mBinding.cvSetsMain,
                        AppearingAnimator.FROM_RIGHT, false));

        set.start();
    }
}
