package xyz.volgoak.wordlearning;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;

import xyz.volgoak.wordlearning.utils.PreferenceContract;

/**
 * Created by alex on 2/2/18.
 */

public class AppRater {
    private final static String APP_TITLE = "App Name";// App Name
    private final static String APP_PNAME = "xyz.volgoak.wordlearning";// Package Name

    private final static int DAYS_UNTIL_PROMPT = 3;//Min number of days
    private final static int DAYS_UNTIL_REMIND = 3;
    private final static int LAUNCHES_UNTIL_PROMPT = 5;//Min number of launches

    public static void app_launched(Context mContext) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (prefs.getBoolean(PreferenceContract.DONT_SHOW_RATE_DIALOG, false)) {
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launch_count = prefs.getLong(PreferenceContract.LAUNCH_COUNT, 0) + 1;
        editor.putLong(PreferenceContract.LAUNCH_COUNT, launch_count);

        // Get date of first launch
        long date_firstLaunch = prefs.getLong(PreferenceContract.DATE_FIRST_LAUNCH, 0);
        if (date_firstLaunch == 0) {
            date_firstLaunch = System.currentTimeMillis();
            editor.putLong(PreferenceContract.DATE_FIRST_LAUNCH, date_firstLaunch);
        }

        long rateLastRemind = prefs.getLong(PreferenceContract.LAST_RATE_SHOW_TIME, 0);

        // Wait at least n days before opening
        if (launch_count >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= date_firstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
                if (System.currentTimeMillis() >= rateLastRemind + (DAYS_UNTIL_REMIND * 24 * 60 * 60 * 1000)) {
                    showRateDialog(mContext);
                }
            }
        }

        editor.apply();
    }

    public static void showRateDialog(final Context context) {

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.rate_us);
        builder.setMessage(R.string.rate_us_summary);

        builder.setNeutralButton(R.string.never, (dialog, which) -> {
            editor.putBoolean(PreferenceContract.DONT_SHOW_RATE_DIALOG, true).apply();
            dialog.dismiss();
        });

        builder.setNegativeButton(R.string.remind_me_later, (dialog, which) -> {
            editor.putLong(PreferenceContract.LAST_RATE_SHOW_TIME, System.currentTimeMillis()).apply();
            dialog.dismiss();
        });

        builder.setPositiveButton(android.R.string.ok, ((dialog, which) -> {
            editor.putBoolean(PreferenceContract.DONT_SHOW_RATE_DIALOG, true);
            rateApp(context);
            dialog.dismiss();
        }));

        builder.show();
    }

    public static void rateApp(Context context) {
        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + APP_PNAME)));
        } catch (ActivityNotFoundException ex) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + APP_PNAME)));
        }
    }
}
