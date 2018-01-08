package xyz.volgoak.wordlearning.update;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.data.FirebaseContract;
import xyz.volgoak.wordlearning.data.StorageContract;

import static xyz.volgoak.wordlearning.update.SetsLoader.DATA_ID_ATTR;
import static xyz.volgoak.wordlearning.update.SetsLoader.DATA_SET_NODE;
import static xyz.volgoak.wordlearning.update.SetsLoader.DATA_SOURCE_ATTR;
import static xyz.volgoak.wordlearning.update.SetsLoader.LOADED_SETS_PREF;


/**
 * Created by Volgoak on 21.10.2017.
 */

public class FirebaseDownloadHelper {

    public static final String TAG = FirebaseDownloadHelper.class.getSimpleName();
    private final Object lock = new Object();
    private final Object checkLock = new Object();
    private Context mContext;

    private List<Task<byte[]>> tasks;

    @Inject
    DataProvider mDataProvider;

    public FirebaseDownloadHelper(Context context) {
        mContext = context;
        WordsApp.getsComponent().inject(this);
    }

    public SetsUpdatingInfo check(byte[] bytes, Context context) throws Exception {
        tasks = new ArrayList<>();
        SetsUpdatingInfo info = new SetsUpdatingInfo();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> loadedData = preferences.getStringSet(LOADED_SETS_PREF, new HashSet<String>());


        Document doc = SetsLoader.prepareDocument(bytes);
        NodeList sets = doc.getElementsByTagName(DATA_SET_NODE);
        for (int a = 0; a < sets.getLength(); a++) {
            Node n = sets.item(a);

            String dataId = n.getAttributes().getNamedItem(DATA_ID_ATTR).getNodeValue();
            String setSource = n.getAttributes().getNamedItem(DATA_SOURCE_ATTR).getNodeValue();

            if (!loadedData.contains(dataId)) {
                Log.d(TAG, "check: load data set " + dataId);
                info.addInfo(loadDataByFileName(setSource, dataId, context));
            }
        }

        //unblock thread when all tasks are finished
        Task<Void> allFinishedTask = Tasks.whenAll(tasks);
        allFinishedTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                synchronized (checkLock) {
                    Log.d(TAG, "onSuccess: check() finished");
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
        final SetsUpdatingInfo info = new SetsUpdatingInfo();
        String indexFile = context.getString(R.string.sets_index_file_ru_en);

        //don't run tasks in the main thread
        Executor executor = new ThreadPoolExecutor(3, 5, 20, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(3));

        StorageReference indexRef = FirebaseStorage.getInstance().getReference(indexFile);
        Task<byte[]> t = indexRef.getBytes(Long.MAX_VALUE);

        t.addOnSuccessListener(executor, new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    info.addInfo(check(bytes, context));
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

        Log.d(TAG, "checkForDbUpdate: result words " + info.getWordsAdded() + " sets " + info.getSetsAdded());

        return info;
    }

    private SetsUpdatingInfo loadDataByFileName(String fileName, final String fileId, final Context context) {
        final SetsUpdatingInfo info = new SetsUpdatingInfo();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(fileName);
        Task<byte[]> task = storageReference.getBytes(Long.MAX_VALUE);
        tasks.add(task);
        /*task.addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {

                    Document doc = SetsLoader.prepareDocument(bytes);
                    SetsUpdatingInfo setInfo = SetsLoader.insertSetsIntoDb(doc, adapter);
                    Log.d(TAG, "onSuccess: load set task");
                    if (setInfo.getWordsAdded() > 0) {
                        SetsLoader.addSuccessPreference(fileId, context);
                        info.addInfo(setInfo);
                        adapter.endTransaction(true);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    adapter.endTransaction(false);
                }
            }
        });*/

        // TODO: 19.10.2017 load image when new set loaded

        return info;
    }




}
