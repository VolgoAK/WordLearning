package xyz.volgoak.wordlearning.utils.animation;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * Created by Volgoak on 25.09.2017.
 */

public class AppearingAnimator {

    public static final int FROM_LEFT = 1;
    public static final int FROM_RIGHT = 2;

    public static ObjectAnimator createAnimator(Activity a, View v, int side, boolean disappearing) {
        DisplayMetrics metrics = new DisplayMetrics();
        a.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float width = metrics.widthPixels;
        float path = 0f;
        if (side == FROM_LEFT) {
            path = -v.getWidth() - v.getY();
        } else if (side == FROM_RIGHT) {
            path = width - v.getX() + v.getWidth();
        }

        //why reverse does not work?
        float from = disappearing ? 0 : path;
        float to = disappearing ? path : 0;

        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "TranslationX", from, to);
        animator.setInterpolator(new MetallBounceInterpoltor());
        if (disappearing) animator.reverse();
        return animator;
    }

    public static float getPathToEndOfScreen(Activity a, View v, int side) {
        DisplayMetrics metrics = new DisplayMetrics();
        a.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        float width = metrics.widthPixels;
        float path = 0f;
        if (side == FROM_LEFT) {
            path = -v.getWidth() - v.getY();
        } else if (side == FROM_RIGHT) {
            path = width - v.getX() + v.getWidth();
        }

        return path;
    }
}
