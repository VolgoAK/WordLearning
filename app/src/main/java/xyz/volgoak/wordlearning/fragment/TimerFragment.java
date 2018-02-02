package xyz.volgoak.wordlearning.fragment;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;

import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.utils.AppearingAnimator;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends Fragment {

    public static final String TAG = TimerFragment.class.getSimpleName();

    private int time = 3;

    private Button btOne;
    private Button btTwo;

    private boolean paused;
    private boolean timerStarted;

    private TimerListener timerListener;

    private SoundPool soundPool;
    private int timeSound;

    public TimerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_timer, container, false);
        btOne = root.findViewById(R.id.bt_one_timer);
        btTwo = root.findViewById(R.id.bt_two_timer);
        ViewTreeObserver observer = root.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(() -> {
            if (!timerStarted) runTimerBounce();
            timerStarted = true;
        });
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder().setMaxStreams(2).build();
        } else {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 1);
        }

        timeSound = soundPool.load(getContext(), R.raw.tick, 1);
    }

    @Override
    public void onStop() {
        super.onStop();
        soundPool.release();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        paused = true;
    }

    private void runTimerBounce() {

        btTwo.setText(String.valueOf(time));
        soundPool.play(timeSound, 0.8f, 0.8f, 1, 0, 1);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (paused) return;

                soundPool.play(timeSound, 0.8f, 0.8f, 1, 0, 1);

                btTwo.setAlpha(1.0f);

                btTwo.setText(String.valueOf(time));
                btOne.setText(String.valueOf(time - 1));
                if (time == 1) {
                    btOne.setText(R.string.go);
                    btOne.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.green_circle));
                }

                float path = AppearingAnimator.getPathToEndOfScreen(getActivity(), btTwo, AppearingAnimator.FROM_LEFT);

                ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
                animator.addUpdateListener((updatedAnimation) -> {
                    float percent = (float) updatedAnimation.getAnimatedValue();
                    float x = path * percent;
                    btTwo.setTranslationX(x);
                    btTwo.setTranslationY((float) (-Math.sqrt(Math.abs(x * 6)) * 4));
                    btTwo.setAlpha(1f - percent);
                    btTwo.setRotation(720 * percent);
                });

                animator.setInterpolator(new AccelerateInterpolator());
                animator.setDuration(200);
                animator.start();

                time--;

                if (time < 1) {
                    handler.postDelayed(() -> timerListener.onTimerFinished(), 1000);
                } else {
                    handler.postDelayed(this, 1000);
                }
            }
        }, 1000);
    }

    private void runRightToLeft() {
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (paused) return;

                soundPool.play(timeSound, 0.8f, 0.8f, 1, 0, 1);

                Button in = (time % 2 == 0) ? btTwo : btOne;
                Button out = (time % 2 == 0) ? btOne : btTwo;

                in.setAlpha(1.0f);
//                out.setAlpha(1.0f);

                in.setText("" + time);
                if (time == 0) {
                    in.setText(R.string.go);
                    in.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.green_circle));
                }

                ObjectAnimator animatorIn = AppearingAnimator
                        .createAnimator(getActivity(), in, AppearingAnimator.FROM_RIGHT, false);
                ObjectAnimator animatorOut = AppearingAnimator
                        .createAnimator(getActivity(), out, AppearingAnimator.FROM_LEFT, true);
                ObjectAnimator animatorOutFade = ObjectAnimator.ofFloat(out, "alpha", 1, 0);

                AnimatorSet set = new AnimatorSet();
                set.play(animatorIn).with(animatorOut).with(animatorOutFade);
                set.setInterpolator(new AccelerateInterpolator());
                set.setDuration(500);
                set.start();

                time--;

                if (time < 0) {
                    handler.postDelayed(() -> timerListener.onTimerFinished(), 1100);
                } else {
                    handler.postDelayed(this, 1100);
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TimerListener) {
            timerListener = (TimerListener) context;
        } else throw new RuntimeException(context + " must implement TimerListener.");
    }

    public interface TimerListener {
        void onTimerFinished();
    }
}
