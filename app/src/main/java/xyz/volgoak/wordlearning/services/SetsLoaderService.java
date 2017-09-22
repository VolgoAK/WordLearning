package xyz.volgoak.wordlearning.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.google.firebase.auth.FirebaseAuth;

import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.SplashActivity;
import xyz.volgoak.wordlearning.utils.SetsLoader;
import xyz.volgoak.wordlearning.utils.SetsUpdatingInfo;

/**
 * Created by Volgoak on 26.05.2017.
 */

public class SetsLoaderService extends GcmTaskService{

    public static final String TASK_CHECK_UPDATE = "check_update";
    public static final String TAG = SetsLoaderService.class.getSimpleName();
    public static final int NOTIFICATION_ID = 445566778;

    @Override
    public int onRunTask(TaskParams taskParams) {
//        Log.d(TAG, "onRunTask");
        switch(taskParams.getTag()){
            case TASK_CHECK_UPDATE :
                checkUpdate();
                return GcmNetworkManager.RESULT_SUCCESS;
            default: return GcmNetworkManager.RESULT_FAILURE;
        }
    }

    private void checkUpdate(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInAnonymously();

        SetsUpdatingInfo info = SetsLoader.checkForDbUpdate(this);
//        Log.d(TAG, " sets loaded " + info.getSetsAdded() + "words loaded " + info.getWordsAdded());
        if(info.getWordsAdded() > 0)
        createUpdateNotification(info);
    }

    private void createUpdateNotification(SetsUpdatingInfo info){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(getString(R.string.new_sets_loaded))
                .setContentText(getString(R.string.num_of_words_loaded, info.getWordsAdded()))
                .setAutoCancel(true)
                .setLargeIcon(getBitmapForNotification())
                .setContentIntent(createPendingIntent())
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager manager =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
    }

    private PendingIntent createPendingIntent(){
        Intent intent = new Intent(this, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private Bitmap getBitmapForNotification(){
        Resources res = this.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, R.mipmap.ic_launcher);
        return bitmap;
    }
}
