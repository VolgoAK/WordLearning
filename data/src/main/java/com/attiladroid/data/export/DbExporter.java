package com.attiladroid.data.export;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

import com.attiladroid.data.DataContract;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;


/**
 * Created by alex on 1/6/18.
 */

public class DbExporter {

    public static void  exportDb() {
        File external = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();

        FileChannel source = null;
        FileChannel destination = null;

        String currentDBPath = "/data/"+ "xyz.volgoak.wordlearning" +"/databases/"+ DataContract.DB_NAME;
        String dbPath = DataContract.DB_NAME;

        File currentDb = new File(data + currentDBPath);
        File backUpDb = new File(external + "/" + dbPath);
        try {
            source = new FileInputStream(currentDb).getChannel();
            destination = new FileOutputStream(backUpDb).getChannel();

            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void importDb(Context context) {
        AssetManager assetManager = context.getAssets();
        try {
            InputStream inputStream = assetManager.open(DataContract.DB_NAME + ".sqlite3");
            File file = new File(Environment.getDataDirectory()
                    + "/data/"+ "xyz.volgoak.wordlearning" +"/databases/"+ DataContract.DB_NAME);
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int read = inputStream.read(buffer);
            while (read != -1) {
                outputStream.write(buffer, 0, read);
                read = inputStream.read(buffer);
            }
        }catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
