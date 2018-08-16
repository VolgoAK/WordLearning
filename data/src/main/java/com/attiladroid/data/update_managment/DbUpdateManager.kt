package com.attiladroid.data.update_managment

import android.content.Context
import android.preference.PreferenceManager
import com.attiladroid.data.BuildConfig

import com.attiladroid.data.Config
import com.attiladroid.data.DataProvider
import com.attiladroid.data.DataContract
import com.google.android.gms.gcm.GcmNetworkManager
import com.google.android.gms.gcm.OneoffTask
import com.google.android.gms.gcm.PeriodicTask
import com.google.android.gms.gcm.Task
import com.google.firebase.auth.FirebaseAuth

import com.attiladroid.data.DataContract.Preference

/**
 * Created by alex on 1/26/18.
 */

object DbUpdateManager {

    val TAG = DbUpdateManager::class.java.simpleName

    private val SEC_IN_TWO_DAYS = (60 * 60 * 24 * 2).toLong()
    private val ONE_HOUR_WINDOW = (60 * 60).toLong()
    private val MILLIS_IN_ONE_MINUT: Long = 60


    fun manageDbState(context: Context, provider: DataProvider) {

        //load default database if not loaded yet
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        var baseLoaded = preferences.getBoolean(Preference.BASE_CREATED, false)
        if (!baseLoaded) {
            if (Config.IMPORT_PREBUILT_DB) {
                baseLoaded = SetsLoader.importDbFromAsset(context, DataContract.DB_NAME)
            } else if (Config.IMPORT_JSON_DB) {
                baseLoaded = true
                SetsLoader.insertTestBase(provider, context)
            }

            preferences.edit().putBoolean(Preference.BASE_CREATED, baseLoaded).apply()
        }

        if (Config.EXPORT_DB) {
            SetsLoader.exportDbToFile(context, DataContract.DB_NAME)
        }

        if (!Config.SCHEDULE_UPDATE) return
        //check auth and download images and new sets
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            scheduleUpdateTasks(context)
        } else {
            FirebaseAuth.getInstance().signInAnonymously()
            FirebaseAuth.getInstance().addAuthStateListener(object : FirebaseAuth.AuthStateListener {
                override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                    if (firebaseAuth.currentUser != null) {
                        scheduleUpdateTasks(context)
                        firebaseAuth.removeAuthStateListener(this)
                    }
                }
            })
        }
    }

    // Firebase downloads and update tasks
    private fun scheduleUpdateTasks(context: Context) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        if (!preferences.getBoolean(Preference.IMAGES_LOADED, false)) {
            ImageDownloader(context).downloadAllImages()
        }

        val networkManager = GcmNetworkManager.getInstance(context)
        //run updating as soon as possible at first launch and after update
        val currentVersion = BuildConfig.VERSION_CODE

        if (preferences.getInt(Preference.LAST_VERSION, 0) < currentVersion) {

            val task = OneoffTask.Builder()
                    .setService(SetsLoaderService::class.java)
                    .setExecutionWindow(0, 30)
                    .setTag(SetsLoaderService.TASK_CHECK_UPDATE_SETS)
                    .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                    .build()

            networkManager.schedule(task)
            preferences.edit().putInt(Preference.LAST_VERSION, currentVersion).apply()
        } else {

            //create task for updating db with 2 days period
            val task = PeriodicTask.Builder()
                    .setService(SetsLoaderService::class.java)
                    .setTag(SetsLoaderService.TASK_CHECK_UPDATE_SETS)
                    .setPeriod(SEC_IN_TWO_DAYS)
                    .setFlex(ONE_HOUR_WINDOW)
                    .setUpdateCurrent(false)
                    .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
                    .build()

            networkManager.schedule(task)
        }
    }
}
