package xyz.volgoak.wordlearning.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.SplashActivity;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.FirebaseContract;
import xyz.volgoak.wordlearning.data.StorageContract;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;
import xyz.volgoak.wordlearning.utils.SetsLoader;
import xyz.volgoak.wordlearning.utils.SetsUpdatingInfo;

import static xyz.volgoak.wordlearning.utils.SetsLoader.check;

/**
 * Created by Volgoak on 26.05.2017.
 */

public class SetsLoaderService extends GcmTaskService{

    public static final String TASK_CHECK_UPDATE_SETS = "check_update";
    public static final String TASK_LOAD_IMAGES = "load_images";
    public static final String TAG = SetsLoaderService.class.getSimpleName();
    public static final int NOTIFICATION_ID = 445566778;

    private File mSmallImagesDir;
    private File mTitleImagesDir;
    private StorageReference mSmallImagesReference;
    private StorageReference mTitleImagesReference;

    @Override
    public int onRunTask(TaskParams taskParams) {
//        Log.d(TAG, "onRunTask");
        switch(taskParams.getTag()){
            case TASK_CHECK_UPDATE_SETS:
                checkUpdate();
                return GcmNetworkManager.RESULT_SUCCESS;
            case TASK_LOAD_IMAGES :
                checkImages();
                return GcmNetworkManager.RESULT_SUCCESS;
            default: return GcmNetworkManager.RESULT_FAILURE;
        }
    }

    private void checkUpdate(){
        Log.d(TAG, "checkUpdate: ");
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInAnonymously();
//        Thread.currentThread().join();
        SetsUpdatingInfo info = checkForDbUpdate(this);
        Log.d(TAG, " sets loaded " + info.getSetsAdded() + "words loaded " + info.getWordsAdded());
        if(info.getWordsAdded() > 0) {

        }
    }

    /**
     * Loads new sets from firebase storage.
     * @param context
     * @return information about loaded sets
     */
    public SetsUpdatingInfo checkForDbUpdate(final Context context){
        final SetsUpdatingInfo info = new SetsUpdatingInfo();
        String indexFile = context.getString(R.string.sets_index_file_ru_en);
        StorageReference indexRef = FirebaseStorage.getInstance().getReference(indexFile);
        indexRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    info.addInfo(SetsLoader.check(bytes, context));
                    checkImages();
                    createUpdateNotification(info);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }
        });

        return info;
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

    private void checkImages(){
        Log.d(TAG, "checkImages()");
        checkOrCreateDirs();
        mSmallImagesReference = FirebaseStorage.getInstance().getReference(FirebaseContract.IMAGES_FOLDER);
        mTitleImagesReference = FirebaseStorage.getInstance().getReference(FirebaseContract.TITLE_IMAGES_FOLDER);

        WordsDbAdapter adapter = new WordsDbAdapter();
        Cursor setsCursor = adapter.fetchAllSets();
        int imageColumnIndex = setsCursor.getColumnIndex(DatabaseContract.Sets.COLUMN_IMAGE_URL);
        setsCursor.moveToFirst();
        do{
            String imageName = setsCursor.getString(imageColumnIndex);
            checkAndLoadImage(imageName);
        }while (setsCursor.moveToNext());
    }

    private void loadNewImages(ArrayList<String> images){
        Log.d(TAG, "loadNewImages: ");
        for(String name : images){
            checkAndLoadImage(name);
        }
    }

    private void checkAndLoadImage(String imageName){
        if(!isBigImageExists(imageName)){
            loadTitleImage( imageName);
        }

        if(!isSmallImageExists(imageName)){
            loadSmallImage(imageName);
        }
    }

    private boolean isSmallImageExists(String imageName){
        File directory = new File(getFilesDir(), StorageContract.IMAGES_W_50_FOLDER);
        File image = new File(directory, imageName);
        return image.exists();
    }

    private boolean isBigImageExists(String imageName){
        File directory = new File(getFilesDir(), StorageContract.IMAGES_W_400_FOLDER);
        File image = new File(directory, imageName);
        return image.exists();
    }

    private void loadSmallImage(final String imageName){
        Log.d(TAG, "loadSmallImage: " + imageName);
        StorageReference imageRef = mSmallImagesReference.child(imageName);
        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                File imageFile = new File(mSmallImagesDir, imageName);
                try {
                    Log.d(TAG, "onSuccess: image " + imageName);
                    FileOutputStream fous = new FileOutputStream(imageFile);
                    fous.write(bytes);
                    fous.close();
                }catch (FileNotFoundException ex){
                    // TODO: 22.09.2017 can it happen? What can I do if can?
                }catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    private void loadTitleImage(final String imageName){
        StorageReference imageRef = mTitleImagesReference.child(imageName);
        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                File imageFile = new File(mTitleImagesDir, imageName);
                try{
                    FileOutputStream fous = new FileOutputStream(imageFile);
                    fous.write(bytes);
                    fous.close();
                }catch (FileNotFoundException ex){
                    ex.printStackTrace();
                }catch (IOException ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    private void checkOrCreateDirs(){
        mSmallImagesDir = new File(getFilesDir(), StorageContract.IMAGES_W_50_FOLDER);
        if(!mSmallImagesDir.exists()) mSmallImagesDir.mkdirs();

        mTitleImagesDir = new File(getFilesDir(), StorageContract.IMAGES_W_400_FOLDER);
        if(!mTitleImagesDir.exists()) mTitleImagesDir.mkdirs();
    }
}
