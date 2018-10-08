package com.attiladroid.data.update_managment

import android.content.Context
import android.preference.PreferenceManager
import com.attiladroid.data.DataContract
import com.attiladroid.data.db.Dao.StorageContract
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import net.lingala.zip4j.core.ZipFile
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by alex on 1/8/18.
 */

class ImageDownloader(var mContext: Context) {

    private var mImagesDir: File? = null

    fun downloadAllImages() {
        checkOrCreateDirs()
        val photosFile = File(mImagesDir, StorageContract.PHOTOS_ARCHIVE)
        val imagesReference = FirebaseStorage.getInstance().getReference(DataContract.Firebase.IMAGES_FOLDER)
        val archiveReference = imagesReference.child(StorageContract.PHOTOS_ARCHIVE)
        archiveReference.getBytes(java.lang.Long.MAX_VALUE).addOnSuccessListener { bytes ->
            try {
                val fileOutputStream = FileOutputStream(photosFile)
                fileOutputStream.write(bytes)
                fileOutputStream.close()

                val zipFile = ZipFile(photosFile)
                zipFile.extractAll(mImagesDir!!.absolutePath)
                PreferenceManager.getDefaultSharedPreferences(mContext).edit()
                        .putBoolean(DataContract.Preference.IMAGES_LOADED, true).apply()


            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

    private fun checkOrCreateDirs() {
        mImagesDir = File(mContext.filesDir, StorageContract.IMAGES_FOLDER)
        if (!mImagesDir!!.exists()) mImagesDir!!.mkdirs()
    }

    private fun downloadImage(imageName: String) {
        checkOrCreateDirs()
        val imageDirRef = FirebaseStorage.getInstance().getReference(DataContract.Firebase.IMAGES_FOLDER)
        val imageRef = imageDirRef.child(imageName)
        val imageFile = File(mImagesDir, imageName)
        imageRef.getBytes(java.lang.Long.MAX_VALUE).addOnSuccessListener { bytes ->
            try {
                val fous = FileOutputStream(imageFile)
                fous.write(bytes)
                fous.close()
            } catch (ex: FileNotFoundException) {
                ex.printStackTrace()
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
        }
    }

    fun loadImage(imageName: String) {
        if (FirebaseAuth.getInstance().currentUser != null) {
            downloadImage(imageName)
        } else {
            FirebaseAuth.getInstance().signInAnonymously()
            FirebaseAuth.getInstance().addAuthStateListener(object : FirebaseAuth.AuthStateListener {
                override fun onAuthStateChanged(firebaseAuth: FirebaseAuth) {
                    if (firebaseAuth.currentUser != null) {
                        downloadImage(imageName)
                        firebaseAuth.removeAuthStateListener(this)
                    }
                }
            })
        }
    }

    companion object {

        val TAG = ImageDownloader::class.java.simpleName

        fun isImageExist(context: Context, imageName: String): Boolean {
            val directory = File(context.filesDir, StorageContract.IMAGES_FOLDER)
            val image = File(directory, imageName)
            return image.exists()
        }
    }
}
