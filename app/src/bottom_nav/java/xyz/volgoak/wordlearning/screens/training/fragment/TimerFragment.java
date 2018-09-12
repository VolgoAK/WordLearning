package xyz.volgoak.wordlearning.screens.training.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;

import javax.inject.Inject;

import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.WordsApp;
import xyz.volgoak.wordlearning.utils.SoundsManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class TimerFragment extends Fragment {

    public static final String TAG = TimerFragment.class.getSimpleName();

    private int time = 3;

    private Button btOne;

    private boolean paused;
    private boolean timerStarted;

    private TimerListener timerListener;

    @Inject
    SoundsManager soundsManager;

    public TimerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        WordsApp.getsComponent().inject(this);
        View root = inflater.inflate(R.layout.fragment_timer, container, false);

        btOne = root.findViewById(R.id.bt_one_timer);


        ViewTreeObserver observer = root.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(() -> {
            if (!timerStarted) runTimerBounce();
            timerStarted = true;
        });

        return root;
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
        btOne.setText(String.valueOf(time));

//        soundsManager.play(SoundsManager.Sound.TICK_SOUND);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (paused) return;

                soundsManager.play(SoundsManager.Sound.TICK_SOUND);

                btOne.setText(String.valueOf(time - 1));
                if (time == 1) {
                    btOne.setText(R.string.go);
                    btOne.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.green_circle));
                }
                time--;

                if (time < 1) {
                    handler.postDelayed(() -> {
                        if(TimerFragment.this.isResumed()) timerListener.onTimerFinished();
                    }, 1000);
                } else {
                    handler.postDelayed(this, 1000);
                }
            }
        }, 800);
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
