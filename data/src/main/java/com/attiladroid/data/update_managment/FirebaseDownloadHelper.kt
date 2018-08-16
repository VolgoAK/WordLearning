package com.attiladroid.data.update_managment

import android.content.Context
import android.support.v7.preference.PreferenceManager
import com.attiladroid.data.Config
import com.attiladroid.data.DataProvider
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.storage.FirebaseStorage
import org.json.JSONObject
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


/**
 * Created by Volgoak on 21.10.2017.
 */

class FirebaseDownloadHelper(private val mDataProvider: DataProvider) {
    private val lock = java.lang.Object()
    private val checkLock = java.lang.Object()

    private val info = SetsUpdatingInfo()

    private var tasks: MutableList<Task<ByteArray>>? = null

    @Throws(Exception::class)
    fun check(bytes: ByteArray, context: Context): SetsUpdatingInfo? {
        tasks = ArrayList()

        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val loadedData = preferences.getStringSet(SetsLoader.LOADED_SETS_PREF, HashSet())

        val updateIndex = String(bytes)
        val `object` = JSONObject(updateIndex)
        val updatesArray = `object`.getJSONArray("updates")

        if (updatesArray.length() == 0) return null

        for (a in 0 until updatesArray.length()) {
            val updateFile = updatesArray.getJSONObject(a)

            val dataId = updateFile.getString(SetsLoader.DATA_ID_ATTR)
            val setSource = updateFile.getString(SetsLoader.DATA_SOURCE_ATTR)

            if (!loadedData!!.contains(dataId)) {
                info.addInfo(loadDataByFileName(setSource, dataId, context))
            }
        }

        //unblock thread when all tasks are finished
        val allFinishedTask = Tasks.whenAll(tasks!!)
        allFinishedTask.addOnSuccessListener {
            synchronized(checkLock) {
                checkLock.notify()
            }
        }

        //wait till all tasks are finished
        synchronized(checkLock) {
            checkLock.wait()
        }

        return info
    }

    /**
     * Loads new sets from firebase storage.
     *
     * @param context
     * @return information about loaded sets
     */
    fun checkForDbUpdate(context: Context): SetsUpdatingInfo {
        //don't run tasks in the main thread
        val executor = ThreadPoolExecutor(3, 5, 20, TimeUnit.SECONDS, ArrayBlockingQueue(3))

        val indexRef = FirebaseStorage.getInstance().getReference(Config.FIREBASE_UPDATE_INDEX)
        val t = indexRef.getBytes(java.lang.Long.MAX_VALUE)

        t.addOnSuccessListener(executor, OnSuccessListener { bytes ->
            try {
                check(bytes, context)
            } catch (ex: Exception) {
                ex.printStackTrace()
            } finally {
                synchronized(lock) {
                    lock.notify()
                }
            }
        })


        try {
            synchronized(lock) {
                lock.wait()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return info
    }

    private fun loadDataByFileName(fileName: String, fileId: String, context: Context): SetsUpdatingInfo {
        val storageReference = FirebaseStorage.getInstance().getReference(fileName)
        val task = storageReference.getBytes(java.lang.Long.MAX_VALUE)
        tasks!!.add(task)
        task.addOnSuccessListener { bytes ->
            try {
                val setInfo = SetsLoader.createAndInsertDictionary(mDataProvider, String(bytes))

                if (setInfo.wordsAdded > 0) {
                    val preferences = PreferenceManager.getDefaultSharedPreferences(context)
                    val loaded = preferences.getStringSet(SetsLoader.LOADED_SETS_PREF, HashSet())
                    loaded!!.add(fileId)
                    preferences.edit().putStringSet(SetsLoader.LOADED_SETS_PREF, loaded).apply()
                    info.addInfo(setInfo)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        return info
    }


}
