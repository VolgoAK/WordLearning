package xyz.volgoak.wordlearning.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.crashlytics.android.Crashlytics;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.fabric.sdk.android.services.concurrency.AsyncTask;
import xyz.volgoak.wordlearning.WordsApp;
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.entities.Word;

/**
 * Created by alex on 2/19/18.
 */

public class DictionaryViewModel extends ViewModel {

    @Inject
    DataProvider dataProvider;

    private MutableLiveData<List<Word>> wordsLiveData;

    public DictionaryViewModel() {
        WordsApp.getsComponent().inject(this);
    }

    public LiveData<List<Word>> getDictionaryWords() {
        if (wordsLiveData == null) {
            wordsLiveData = new MutableLiveData<>();
            dataProvider.getDictionaryWordsFlowable()
                    .subscribe((list) -> {
                                Collections.sort(list, (one, two) -> Long.compare(two.getAddedTime(), one.getAddedTime()));
                                wordsLiveData.postValue(list);
                            }
                            , Crashlytics::logException);
        }

        return wordsLiveData;
    }

    public void insertWord(Word newWord) {
        dataProvider.insertWord(newWord);
    }

    public void deleteOrHideWord(Word word) {
        word.setStatus(DatabaseContract.Words.OUT_OF_DICTIONARY);
        AsyncTask.execute(() -> dataProvider.updateWords(word));
    }

    public void resetWordProgress(Word word) {
        dataProvider.resetWordProgress(word.getId());
    }
}
