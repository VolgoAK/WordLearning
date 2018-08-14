package xyz.volgoak.wordlearning.update;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.attiladroid.data.DataProvider;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.WordsApp;

import static xyz.volgoak.wordlearning.update.SetsLoader.DATA_ID_ATTR;
import static xyz.volgoak.wordlearning.update.SetsLoader.DATA_SOURCE_ATTR;
import static xyz.volgoak.wordlearning.update.SetsLoader.LOADED_SETS_PREF;


/**
 * Created by Volgoak on 21.10.2017.
 */

public class FirebaseDownloadHelper {

    public static final String TAG = FirebaseDownloadHelper.class.getSimpleName();
    private final Object lock = new Object();
    private final Object checkLock = new Object();

    private SetsUpdatingInfo info = new SetsUpdatingInfo();

    private List<Task<byte[]>> tasks;

    @Inject
    DataProvider mDataProvider;

    public FirebaseDownloadHelper() {
        WordsApp.getsComponent().inject(this);
    }

    public SetsUpdatingInfo check(byte[] bytes, Context context) throws Exception {
        tasks = new ArrayList<>();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> loadedData = preferences.getStringSet(LOADED_SETS_PREF, new HashSet<String>());

        String updateIndex = new String(bytes);
        JSONObject object = new JSONObject(updateIndex);
        JSONArray updatesArray = object.getJSONArray("updates");

        if(updatesArray.length() == 0) return null;

        for (int a = 0; a < updatesArray.length(); a++) {
            JSONObject updateFile = updatesArray.getJSONObject(a);

            String dataId = updateFile.getString(DATA_ID_ATTR);
            String setSource = updateFile.getString(DATA_SOURCE_ATTR);

            if (!loadedData.contains(dataId)) {
                info.addInfo(loadDataByFileName(setSource, dataId, context));
            }
        }

        //unblock thread when all tasks are finished
        Task<Void> allFinishedTask = Tasks.whenAll(tasks);
        allFinishedTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                synchronized (checkLock) {
                    checkLock.notify();
                }
            }
        });

        //wait till all tasks are finished
        synchronized (checkLock) {
            checkLock.wait();
        }

        return info;
    }

    /**
     * Loads new sets from firebase storage.
     *
     * @param context
     * @return information about loaded sets
     */
    public SetsUpdatingInfo checkForDbUpdate(final Context context) {
        String indexFile = context.getString(R.string.sets_index_file_ru_en);

        //don't run tasks in the main thread
        Executor executor = new ThreadPoolExecutor(3, 5, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3));

        StorageReference indexRef = FirebaseStorage.getInstance().getReference(indexFile);
        Task<byte[]> t = indexRef.getBytes(Long.MAX_VALUE);

        t.addOnSuccessListener(executor, new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    check(bytes, context);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    synchronized (lock) {
                        lock.notify();
                    }
                }
            }
        });


        try {
            synchronized (lock) {
                lock.wait();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return info;
    }

    private SetsUpdatingInfo loadDataByFileName(String fileName, final String fileId, final Context context) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(fileName);
        Task<byte[]> task = storageReference.getBytes(Long.MAX_VALUE);
        tasks.add(task);
        task.addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    SetsUpdatingInfo setInfo = SetsLoader.createAndInsertDictionary(mDataProvider, new String(bytes));

                    if (setInfo.getWordsAdded() > 0) {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                        Set<String> loaded = preferences.getStringSet(LOADED_SETS_PREF, new HashSet<>());
                        loaded.add(fileId);
                        preferences.edit().putStringSet(LOADED_SETS_PREF, loaded).apply();
                        info.addInfo(setInfo);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Crashlytics.logException(ex);
                }
            }
        });

        return info;
    }


}
