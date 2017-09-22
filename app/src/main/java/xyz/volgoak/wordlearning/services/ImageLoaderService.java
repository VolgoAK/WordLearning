package xyz.volgoak.wordlearning.services;

import android.database.Cursor;
import android.os.Environment;

import com.google.android.gms.common.api.Result;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.FirebaseContract;
import xyz.volgoak.wordlearning.data.StorageContract;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;

/**
 * Created by Volgoak on 03.09.2017.
 */

public class ImageLoaderService extends GcmTaskService {
    public static final String TASK_LOAD_ALL_IMAGES = "load_all_images";

    private File mSmallImagesDir;
    private File mTitleImagesDir;
    private StorageReference mSmallImagesReference;
    private StorageReference mTitleImagesReference;

    @Override
    public int onRunTask(TaskParams taskParams) {
        switch (taskParams.getTag()){
            case TASK_LOAD_ALL_IMAGES :
                checkImages();
                // TODO: 22.09.2017 add reschedule if catch some exception
                return GcmNetworkManager.RESULT_SUCCESS;
            default: return GcmNetworkManager.RESULT_FAILURE;
        }
    }

    private void checkImages(){
        checkOrCreateDirs();
        mSmallImagesReference = FirebaseStorage.getInstance().getReference(FirebaseContract.IMAGES_FOLDER);
        mTitleImagesReference = FirebaseStorage.getInstance().getReference(FirebaseContract.TITLE_IMAGES_FOLDER);

        WordsDbAdapter adapter = new WordsDbAdapter();
        Cursor setsCursor = adapter.fetchAllSets();
        int imageColumnIndex = setsCursor.getColumnIndex(DatabaseContract.Sets.COLUMN_IMAGE_URL);
        setsCursor.moveToFirst();
        do{
            String imageName = setsCursor.getString(imageColumnIndex);
            if(!isBigImageExists(imageName)){
                loadTitleImage( imageName);
            }

            if(!isSmallImageExists(imageName)){
                loadSmallImage(imageName);
            }
        }while (setsCursor.moveToNext());
    }

    private boolean isSmallImageExists(String imageName){
        File directory = new File(Environment.getExternalStorageDirectory(), StorageContract.IMAGES_W_50_FOLDER);
        File image = new File(directory, imageName);
        return image.exists();
    }

    private boolean isBigImageExists(String imageName){
        File directory = new File(Environment.getExternalStorageDirectory(), StorageContract.IMAGES_W_400_FOLDER);
        File image = new File(directory, imageName);
        return image.exists();
    }

    private void loadSmallImage(final String imageName){
        StorageReference imageRef = mSmallImagesReference.child(imageName);
        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                File imageFile = new File(mSmallImagesDir, imageName);
                try {
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
        mSmallImagesDir = new File(Environment.getExternalStorageDirectory(), StorageContract.IMAGES_W_50_FOLDER);
        if(!mSmallImagesDir.exists()) mSmallImagesDir.mkdirs();

        mTitleImagesDir = new File(Environment.getExternalStorageDirectory(), StorageContract.IMAGES_W_400_FOLDER);
        if(!mTitleImagesDir.exists()) mTitleImagesDir.mkdirs();
    }
}
