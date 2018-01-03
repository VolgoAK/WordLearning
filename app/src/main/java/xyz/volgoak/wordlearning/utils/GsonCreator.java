package xyz.volgoak.wordlearning.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

import xyz.volgoak.wordlearning.data.Set;
import xyz.volgoak.wordlearning.data.Word;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;

/**
 * Created by alex on 1/3/18.
 */

public class GsonCreator {

    public void createGson(Context context) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        gsonBuilder.disableHtmlEscaping();
        gsonBuilder.setPrettyPrinting();
        Gson gson = gsonBuilder.create();
        WordsDbAdapter adapter = new WordsDbAdapter();
        List<Set> setList = adapter.fetchAllSets();
        for(Set set : setList) {
            List<Word> words = adapter.fetchWordsBySetId(set.getId());
            set.setWords(words);
        }

        Dictionary dictionary = new Dictionary();
        dictionary.setSets(setList);
        dictionary.setThemes(adapter.fetchAllThemes());
        try {

            File file = Environment.getExternalStorageDirectory();

            FileWriter writer = new FileWriter(file + "/my.json");
            writer.write(gson.toJson(dictionary));
            writer.flush();
            writer.close();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
