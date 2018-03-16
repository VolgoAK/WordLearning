package xyz.volgoak.wordlearning.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.annimon.stream.Stream;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import io.fabric.sdk.android.services.concurrency.AsyncTask;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import timber.log.Timber;
import xyz.volgoak.wordlearning.WordsApp;
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.entities.Set;
import xyz.volgoak.wordlearning.entities.Theme;
import xyz.volgoak.wordlearning.entities.Word;

/**
 * Created by alex on 2/13/18.
 */

public class WordsViewModel extends ViewModel {

    @Inject
    DataProvider provider;

    private List<Set> sets;
    private List<Theme> themes;
    private MutableLiveData<List<Set>> setsLiveData = new MutableLiveData<>();
    private MutableLiveData<List<Theme>> themesLiveData = new MutableLiveData<>();

    private MutableLiveData<Set> selectedSetData = new MutableLiveData<>();
    private MutableLiveData<List<Word>> selectedSetWordsData = new MutableLiveData<>();

    private Disposable wordsDisposable;
    private Disposable setDisposable;

    private ExecutorService executor = Executors.newSingleThreadExecutor();


    public WordsViewModel() {
        WordsApp.getsComponent().inject(this);
    }

    private boolean isSetLoaded;

    public LiveData<List<Set>> getSets(String theme) {
        if (sets == null || sets.size() == 0) {
            executor.submit(() -> {
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
        if (themes == null || themes.size() == 0) {
            executor.submit(() -> {
                themes = provider.getAllThemes();
                themesLiveData.postValue(themes);
            });
        }

        return themesLiveData;
    }

    public LiveData<List<Word>> getWordsForSet() {
        return selectedSetWordsData;
    }

    public LiveData<Set> getCurrentSet() {
        return selectedSetData;
    }

    public LiveData<Boolean> changeSet(long setId) {
        isSetLoaded = true;
        MutableLiveData<Boolean> loadedCallback = new MutableLiveData<>();

        if(wordsDisposable != null && !wordsDisposable.isDisposed()) {
            wordsDisposable.dispose();
        }

        if(setDisposable != null && !setDisposable.isDisposed()) {
            setDisposable.dispose();
        }

        wordsDisposable = provider.getWordsBySetIdRx(setId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    Timber.d("words from flowable");
                    selectedSetWordsData.setValue(list);
                }, Timber::e);

        setDisposable = provider.getSetById(setId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(set -> {
                    loadedCallback.setValue(true);
                    selectedSetData.setValue(set);
                    Timber.d("set from flowable %1$s", set.getName());
                }, Timber::e);

        return loadedCallback;
    }

    public LiveData<Boolean> changeToDictionary() {
        if(wordsDisposable != null && !wordsDisposable.isDisposed()) {
            wordsDisposable.dispose();
        }

        if(setDisposable != null && !setDisposable.isDisposed()) {
            setDisposable.dispose();
        }

        MutableLiveData<Boolean> loadedCallback = new MutableLiveData<>();

        wordsDisposable = provider.getDictionaryWordsFlowable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    loadedCallback.postValue(true);
                    selectedSetWordsData.postValue(list);
                }, Timber::e);

        return loadedCallback;
    }

    public LiveData<Integer> changeCurrentSetStatus() {
        MutableLiveData<Integer> statusLive = new MutableLiveData<>();
        Set current = selectedSetData.getValue();
        if (current != null) {
            int newStatus = current.getStatus() == DatabaseContract.Sets.IN_DICTIONARY ?
                    DatabaseContract.Sets.OUT_OF_DICTIONARY : DatabaseContract.Sets.IN_DICTIONARY;

            current.setStatus(newStatus);
            executor.submit(() -> {
                provider.updateSetStatus(current);
                statusLive.postValue(newStatus);
            });
        }
        return statusLive;
    }

    public void resetCurrentSetProgress() {
        Set current = selectedSetData.getValue();
        if (current != null) {
            executor.submit(() -> provider.resetSetProgress(current));
        }
    }

    public void updateWords(Word[] words) {
        executor.submit(() -> provider.updateWords(words));
    }

    public void resetWordsProgress(List<Integer> positions) {
        Set set = selectedSetData.getValue();
        if (set != null && positions.size() != 0) {
            executor.submit(() -> {
                Word[] wordsArray = new Word[positions.size()];
                List<Word> words = provider.getWordsBySetId(set.getId());
                for (int a = 0; a < positions.size(); a++) {
                    Word word = words.get(positions.get(a));
                    word.resetProgress();
                    wordsArray[a] = word;
                }
                provider.updateWords(wordsArray);
            });
        }
    }

    public void changeWordsStatus(List<Integer> positions, int status) {
        Set set = selectedSetData.getValue();
        if (set != null && positions.size() != 0) {
            executor.submit(() -> {
                Word[] wordsArray = new Word[positions.size()];
                List<Word> words = provider.getWordsBySetId(set.getId());
                for (int a = 0; a < positions.size(); a++) {
                    Word word = words.get(positions.get(a));
                    word.setStatus(status);
                    wordsArray[a] = word;
                }
                provider.updateWords(wordsArray);
            });
        }
    }

    public void changeTheme(String theme) {
        executor.submit(() -> {
            List<Set> themeSet = Stream.of(sets).filter(set -> set.getThemeCodes().contains(theme)).toList();
            setsLiveData.postValue(themeSet);
        });
    }

    public void updateSetStatus(Set set) {
        executor.submit(() -> provider.updateSetStatus(set));
    }

    public void insertWord(Word newWord) {
        executor.submit(() -> provider.insertWord(newWord));
    }

    public void deleteOrHideWord(Word word) {
        word.setStatus(DatabaseContract.Words.OUT_OF_DICTIONARY);
        AsyncTask.execute(() -> provider.updateWords(word));
    }

    public boolean isSetLoaded() {
        return isSetLoaded;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executor.shutdown();
    }
}
