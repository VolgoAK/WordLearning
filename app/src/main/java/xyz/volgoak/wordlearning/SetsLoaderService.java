package xyz.volgoak.wordlearning;

import android.app.NotificationManager;

import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.google.firebase.auth.FirebaseAuth;

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
        Log.d(TAG, "onRunTask");
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
        Log.d(TAG, " sets loaded " + info.getSetsAdded() + "words loaded " + info.getWordsAdded());
        // TODO: 26.05.2017 create notification which launch activity
        createUpdateNotification(info);
    }

    private void createUpdateNotification(SetsUpdatingInfo info){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Db updated")
                .setContentText("Loaded " + info.getSetsAdded() + " sets added")
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher);

        NotificationManager manager =(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());
    }
}
