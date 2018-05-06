package xyz.volgoak.wordlearning.utils;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import timber.log.Timber;

/**
 * Created by alex on 3/9/18.
 */

public class ReleaseTree extends Timber.Tree {
    @Override
    protected void log(int priority, String tag, String message, Throwable t) {

        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return;
        }

        Crashlytics.log(priority, tag, message);

        if (t != null) {
            Crashlytics.logException(t);
        }
    }
}
