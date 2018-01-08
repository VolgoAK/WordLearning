package xyz.volgoak.wordlearning.update;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import xyz.volgoak.wordlearning.WordsApp;
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.data.FirebaseContract;
import xyz.volgoak.wordlearning.data.StorageContract;
import xyz.volgoak.wordlearning.entities.Set;

/**
 * Created by alex on 1/8/18.
 */

public class ImageDownloader {

    public static final String TAG = ImageDownloader.class.getSimpleName();
    public static final int MAX_TASKS_LIMIT = 30;
    public static final int RESUME_TASKS_LIMIT = 10;

    private StorageReference mSmallImagesReference;
    private StorageReference mTitleImagesReference;

    private File mSmallImagesDir;
    private File mTitleImagesDir;

    private boolean inProgress = false;

    private volatile int queuedTasks = 0;

    @Inject
    Context mContext;

    @Inject
    DataProvider mDataProvider;

    public ImageDownloader() {
        WordsApp.getsComponent().inject(this);
    }

    private synchronized void checkAndLoadImage(String imageName) {
        if (!isBigImageExists(imageName)) {
            queuedTasks++;
            loadTitleImage(imageName);
        }

        if (!isSmallImageExists(imageName)) {
            queuedTasks++;
            loadSmallImage(imageName);
        }

        // TODO: 1/9/18 move to loop
        if(queuedTasks > MAX_TASKS_LIMIT) {
            Log.d(TAG, "checkAndLoadImage: limit reached, wait");
            while (queuedTasks > RESUME_TASKS_LIMIT) {

                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            Log.d(TAG, "checkAndLoadImage: resume loading");
        }
    }

    private void loadNewImages(ArrayList<String> images) {
        Log.d(TAG, "loadNewImages: ");
        for (String name : images) {
            checkAndLoadImage(name);
        }
    }

    private void checkOrCreateDirs() {
        mSmallImagesDir = new File(mContext.getFilesDir(), StorageContract.IMAGES_W_50_FOLDER);
        if (!mSmallImagesDir.exists()) mSmallImagesDir.mkdirs();

        mTitleImagesDir = new File(mContext.getFilesDir(), StorageContract.IMAGES_W_400_FOLDER);
        if (!mTitleImagesDir.exists()) mTitleImagesDir.mkdirs();
    }

    public synchronized void checkImagesAsynk() {
        Log.d(TAG, "checkImagesAsynk: " + this.toString());
        if(inProgress) return;
        inProgress = true;
        new Thread(() -> {
            checkImages();
            inProgress = false;
        }).start();
    }

    public void checkImages() {
        Log.d(TAG, "checkImages()");
        checkOrCreateDirs();
        mSmallImagesReference = FirebaseStorage.getInstance().getReference(FirebaseContract.IMAGES_FOLDER);
        mTitleImagesReference = FirebaseStorage.getInstance().getReference(FirebaseContract.TITLE_IMAGES_FOLDER);

        List<Set> sets = mDataProvider.getAllSets();
        for(xyz.volgoak.wordlearning.entities.Set set : sets) {
            checkAndLoadImage(set.getImageUrl());
        }
    }

    private boolean isSmallImageExists(String imageName) {
        File directory = new File(mContext.getFilesDir(), StorageContract.IMAGES_W_50_FOLDER);
        File image = new File(directory, imageName);
        return image.exists();
    }

    private boolean isBigImageExists(String imageName) {
        File directory = new File(mContext.getFilesDir(), StorageContract.IMAGES_W_400_FOLDER);
        File image = new File(directory, imageName);
        return image.exists();
    }

    private void loadSmallImage(final String imageName) {
//        Log.d(TAG, "loadSmallImage: " + imageName);
        StorageReference imageRef = mSmallImagesReference.child(imageName);
        //test
//          Task task = imageRef.getBytes(Long.MAX_VALUE);
//        task.addOnSuccessListener()
        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                File imageFile = new File(mSmallImagesDir, imageName);
                try {
//                    Log.d(TAG, "onSuccess: image " + imageName);
                    FileOutputStream fous = new FileOutputStream(imageFile);
                    fous.write(bytes);
                    fous.close();
                } catch (FileNotFoundException ex) {
                    // TODO: 22.09.2017 can it happen? What can I do if can?
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                queuedTasks--;
            }
        });
    }

    private void loadTitleImage(final String imageName) {
        StorageReference imageRef = mTitleImagesReference.child(imageName);
        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                File imageFile = new File(mTitleImagesDir, imageName);
                try {
                    FileOutputStream fous = new FileOutputStream(imageFile);
                    fous.write(bytes);
                    fous.close();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                queuedTasks--;
                Log.d(TAG, "onSuccess: notify thread");
            }
        });
    }
}
