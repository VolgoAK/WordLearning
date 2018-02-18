package xyz.volgoak.wordlearning.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.SystemClock;

import com.annimon.stream.Stream;

import java.util.List;

import javax.inject.Inject;

import io.fabric.sdk.android.services.concurrency.AsyncTask;
import xyz.volgoak.wordlearning.WordsApp;
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.entities.Set;
import xyz.volgoak.wordlearning.entities.Theme;

/**
 * Created by alex on 2/13/18.
 */

public class SetsViewModel extends ViewModel{

    @Inject
    DataProvider provider;

    private List<Set> sets;
    private List<Theme> themes;
    private MutableLiveData<List<Set>> setsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Theme>> themesLiveData = new MutableLiveData<>();

    public SetsViewModel() {
        WordsApp.getsComponent().inject(this);
    }

    public LiveData<List<Set>> getSets(String theme) {
        if(sets == null || sets.size() == 0) {
            AsyncTask.execute(() -> {
                sets = provider.getAllSets();
                List<Set> themed = Stream.of(sets)
                        .filter(set -> set.getThemeCodes().contains(theme))
                        .toList();
                setsLiveData.postValue(themed);
            });
        }
        return setsLiveData;
    }

    public LiveData<List<Theme>> getThemes() {
        if(themes == null || themes.size() == 0) {
            AsyncTask.execute(() -> {
                themes = provider.getAllThemes();
                themesLiveData.postValue(themes);
            });
        }

        return themesLiveData;
    }

    public void changeTheme(String theme) {
        AsyncTask.execute(() -> {
            List<Set> themeSet = Stream.of(sets).filter(set -> set.getThemeCodes().contains(theme)).toList();
            setsLiveData.postValue(themeSet);
        });
    }
}
