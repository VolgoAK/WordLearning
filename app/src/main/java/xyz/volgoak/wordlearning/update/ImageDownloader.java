package xyz.volgoak.wordlearning.update;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.lingala.zip4j.core.ZipFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import xyz.volgoak.wordlearning.WordsApp;
import xyz.volgoak.wordlearning.data.FirebaseContract;
import xyz.volgoak.wordlearning.data.StorageContract;
import xyz.volgoak.wordlearning.utils.PreferenceContract;

/**
 * Created by alex on 1/8/18.
 */

public class ImageDownloader {

    public static final String TAG = ImageDownloader.class.getSimpleName();

    private File mImagesDir;

    @Inject
    Context mContext;

    public ImageDownloader() {
        WordsApp.getsComponent().inject(this);
    }

    public void downloadImages() {
        checkOrCreateDirs();
        File photosFile = new File(mImagesDir, StorageContract.PHOTOS_ARCHIVE);
        StorageReference imagesReference = FirebaseStorage.getInstance().getReference(FirebaseContract.IMAGES_FOLDER);
        StorageReference archiveReference = imagesReference.child(StorageContract.PHOTOS_ARCHIVE);
        archiveReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(photosFile);
                    fileOutputStream.write(bytes);
                    fileOutputStream.close();

                    ZipFile zipFile = new ZipFile(photosFile);
                    zipFile.extractAll(mImagesDir.getAbsolutePath());
                    Log.d(TAG, "onSuccess: downloaded");
                    PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                            .putBoolean(PreferenceContract.IMAGES_LOADED, true).apply();


                } catch (Exception ex) {
                    ex.printStackTrace();
                    Crashlytics.logException(ex);
                }
            }
        });
    }

    private void checkOrCreateDirs() {
        mImagesDir = new File(mContext.getFilesDir(), StorageContract.IMAGES_FOLDER);
        if (!mImagesDir.exists()) mImagesDir.mkdirs();
    }

    public static boolean isImageExist(Context context, String imageName) {
        File directory = new File(context.getFilesDir(), StorageContract.IMAGES_FOLDER);
        File image = new File(directory, imageName);
        return image.exists();
    }

    private void loadImage(final String imageName) {
        StorageReference imageDirRef = FirebaseStorage.getInstance().getReference(FirebaseContract.IMAGES_FOLDER);
        StorageReference imageRef = imageDirRef.child(imageName);
        imageRef.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                File imageFile = new File(mImagesDir, imageName);
                try {
                    FileOutputStream fous = new FileOutputStream(imageFile);
                    fous.write(bytes);
                    fous.close();
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }
}
