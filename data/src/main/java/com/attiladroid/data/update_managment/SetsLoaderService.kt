package com.attiladroid.data.update_managment

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.attiladroid.data.DataProvider
import com.attiladroid.data.R
import com.google.android.gms.gcm.GcmNetworkManager
import com.google.android.gms.gcm.GcmTaskService
import com.google.android.gms.gcm.TaskParams
import com.google.firebase.auth.FirebaseAuth


/**
 * Created by Volgoak on 26.05.2017.
 */

class SetsLoaderService : GcmTaskService() {

    companion object {
        const val TASK_CHECK_UPDATE_SETS = "check_update"
        const val TASK_LOAD_IMAGES = "load_images"
        const val TAG = "SetsLoaderService"
        const val NOTIFICATION_ID = 445566778
    }

    //todo change bitmap
    private val bitmapForNotification: Bitmap
        get() {
            val res = this.resources
            return BitmapFactory.decodeResource(res, R.drawable.ic_stat_name)
        }

    override fun onRunTask(taskParams: TaskParams): Int {
        Log.d("SERVICE", "Start")
        when (taskParams.tag) {
            TASK_CHECK_UPDATE_SETS -> {
                checkUpdate()
                return GcmNetworkManager.RESULT_SUCCESS
            }
            TASK_LOAD_IMAGES ->
                return GcmNetworkManager.RESULT_SUCCESS
            else -> return GcmNetworkManager.RESULT_FAILURE
        }
    }

    private fun checkUpdate() {
        val auth = FirebaseAuth.getInstance()
        auth.signInAnonymously()

        //todo manage data provider
        val downloadHelper = FirebaseDownloadHelper(DataProvider.newInstance(this))
        val info = downloadHelper.checkForDbUpdate(this)

        // TODO: 2/6/18 Load images after set downloaded
        if (info.wordsAdded > 0) {
            createUpdateNotification(info)
        }
    }

    private fun createUpdateNotification(info: SetsUpdatingInfo) {
        val builder = NotificationCompat.Builder(this)
        builder.setContentTitle(getString(R.string.new_sets_loaded))
                .setContentText(getString(R.string.num_of_words_loaded, info.wordsAdded))
                .setAutoCancel(true)
                .setLargeIcon(bitmapForNotification)
                .setContentIntent(createPendingIntent())
                .setSmallIcon(R.drawable.ic_stat_name)

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, builder.build())
    }

    private fun createPendingIntent(): PendingIntent {
        val intent = applicationContext.packageManager.getLaunchIntentForPackage(applicationContext.packageName)
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT)
    }
}
