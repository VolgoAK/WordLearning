package com.attiladroid.data;

import android.arch.lifecycle.LiveData;
import android.content.Context;

import com.attiladroid.data.db.AppDatabase;
import com.attiladroid.data.db.Dao.InfoDao;
import com.attiladroid.data.db.Dao.LinkDao;
import com.attiladroid.data.db.Dao.SetsDao;
import com.attiladroid.data.db.Dao.ThemeDao;
import com.attiladroid.data.db.Dao.WordDao;
import com.attiladroid.data.entities.DictionaryInfo;
import com.attiladroid.data.entities.Link;
import com.attiladroid.data.entities.Word;
import com.attiladroid.data.entities.Set;
import com.attiladroid.data.entities.Theme;

import java.util.List;

import io.reactivex.Flowable;


/**
 * Created by alex on 1/5/18.
 */

public class DataProvider {

    private InfoDao infoDao;
    private LinkDao linkDao;
    private SetsDao setsDao;
    private ThemeDao themeDao;
    private WordDao wordDao;

    public static DataProvider newInstance(Context context) {
        AppDatabase db = AppDatabase.Companion.newInstance(context);
        DataProvider provider = new DataProvider(db.infoDao(), db.linkDao(), db.setsDao(),
                db.themeDao(), db.wordDao());
        return provider;
    }

    private DataProvider(InfoDao infoDao, LinkDao linkDao, SetsDao setsDao, ThemeDao themeDao, WordDao wordDao) {
        this.infoDao = infoDao;
        this.linkDao = linkDao;
        this.setsDao = setsDao;
        this.themeDao = themeDao;
        this.wordDao = wordDao;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Words methods
    ///////////////////////////////////////////////////////////////////////////

    public long insertWord(Word word) {
        return wordDao.insertWord(word);
    }

    public Word getWord(String word, String translation) {
        return wordDao.getWord(word, translation);
    }

    public Word getWordById(long id) {
        return wordDao.getWordById(id);
    }

    public void insertWords(Word... words) {
        wordDao.insertWords(words);
    }

    public void updateWords(Word... words) {
        wordDao.udateWords(words);
    }

    // TODO: 1/7/18 Temp methods. Delete them and replace 
    public void resetWordProgress(long id) {
        Word word = wordDao.getWordById(id);
        word.setStudied(0);
        word.setTrainedTw(0);
        word.setTrainedWt(0);

        wordDao.udateWords(word);
    }

    public void updateWordStatus() {
    }

    public List<Word> getDictionaryWords() {
        return wordDao.dictionaryWords();
    }

    public Flowable<List<Word>> getDictionaryWordsFlowable() {
        return wordDao.dictionaryWordsFlowable();
    }

    public List<Word> getTrainingWords(long setId) {
        return getTrainingWords(setId, Integer.MAX_VALUE);
    }

    public List<Word> getTrainingWords(long setId, int limit) {
        if (setId == -1) {
            return wordDao.dictionaryWords();
        } else return wordDao.getWordsBySetId(setId);
    }

    public List<Word> getVariants(long wordId, int limit, long setId) {
        if (setId == -1) {
            return wordDao.getVariants(wordId, limit);
        } else {
            return wordDao.getVarints(wordId, limit, setId);
        }
    }

    public Flowable<List<Word>> getWordsBySetIdRx(long setId) {
        return wordDao.getWordsBySetIdRx(setId);
    }

    public List<Word> getWordsBySetId(long setId) {
        return wordDao.getWordsBySetId(setId);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Sets methods
    ///////////////////////////////////////////////////////////////////////////

    public long insertSet(Set set) {
        return setsDao.insertSet(set);
    }

    public void insertSets(Set... sets) {
        setsDao.insertSets(sets);
    }

    public void updateSets(Set... sets) {
        setsDao.updateSets(sets);
    }

    public void switchSetStatus(Set set) {
        int status = set.getStatus() == DatabaseContract.Sets.IN_DICTIONARY ?
                DatabaseContract.Sets.OUT_OF_DICTIONARY : DatabaseContract.Sets.IN_DICTIONARY;
        set.setStatus(status);
        updateSetStatus(set);
    }

    public void updateSetStatus(Set set) {
        setsDao.updateSets(set);
        List<Word> words = wordDao.getWordsBySetId(set.getId());
        long time = System.currentTimeMillis();
        boolean updateTime = set.getStatus() == DatabaseContract.Sets.IN_DICTIONARY;
        for (Word word : words) {
            word.setStatus(set.getStatus());
            if (updateTime) word.setAddedTime(time);
        }
        Word[] wordsArray = words.toArray(new Word[0]);
        wordDao.udateWords(wordsArray);
    }

    public void resetSetProgress(Set set) {
        List<Word> words = wordDao.getWordsBySetId(set.getId());
        for(Word word : words) {
            word.resetProgress();
        }
        wordDao.udateWords(words.toArray(new Word[0]));
    }

    public List<Set> getAllSets() {

        return setsDao.allSets();
    }

    public LiveData<List<Set>> getAllSetsLd() {
        return setsDao.allSetsAsync();
    }

    public Flowable<Set> getSetById(long setId) {
        return setsDao.getSetById(setId);
    }

    // TODO: 1/25/18 make with string
    public List<Set> getSetsByTheme(String themeCode) {
        return setsDao.getSetsByTheme("%" + themeCode + "%");
    }

    ///////////////////////////////////////////////////////////////////////////
    // Links, themes and info
    ///////////////////////////////////////////////////////////////////////////

    public void insertLink(Link link) {
        linkDao.insertLinks(link);
    }

    public void insertLinks(Link... links) {
        linkDao.insertLinks(links);
    }

    public LiveData<DictionaryInfo> getDictionaryInfo() {
        return infoDao.getDictionaryInfo();
    }

    public long insertTheme(Theme theme) {
        return themeDao.insertTheme(theme);
    }

    public void insertThemes(Theme... themes) {
        themeDao.insertThemes(themes);
    }

    public LiveData<List<Theme>> getAllThemes() {
        return themeDao.allThemes();
    }
}
