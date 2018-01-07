package xyz.volgoak.wordlearning.data;

import android.support.annotation.NonNull;

import java.util.List;

import xyz.volgoak.wordlearning.entities.Link;
import xyz.volgoak.wordlearning.entities.Set;
import xyz.volgoak.wordlearning.entities.Theme;
import xyz.volgoak.wordlearning.entities.Word;

/**
 * Created by alex on 1/5/18.
 */

public class DataProvider {

    private InfoDao infoDao;
    private LinkDao linkDao;
    private SetsDao setsDao;
    private ThemeDao themeDao;
    private WordDao wordDao;

    public DataProvider(InfoDao infoDao, LinkDao linkDao, SetsDao setsDao, ThemeDao themeDao, WordDao wordDao) {
        this.infoDao = infoDao;
        this.linkDao = linkDao;
        this.setsDao = setsDao;
        this.themeDao = themeDao;
        this.wordDao = wordDao;
    }

    public void insertLink(Link link) {
        linkDao.insertLinks(link);
    }

    public long insertWord(Word word) {
        return wordDao.insertWord(word);
    }

    public Word getWord(String word, String translation) {
        return wordDao.getWord(word, translation);
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
    
    public void deleteOrHideWordById(long wordId) {
        // TODO: 1/7/18 implement this method 
    }

    public void updateWordStatus(){}

    public List<Word> getDictionaryWords() {
        return wordDao.getDictionaryWords();
    }

    public List<Word> getWordsByTrained(@NonNull String trainedType, int wordsLimit, int trainedLimit) {
        return wordDao.getWordsByTrained(trainedType, wordsLimit, trainedLimit);
    }
    

    public List<Word> getWordsBySetId(long setId) {
        return wordDao.getWordsBySetId(setId);
    }

    public long insertSet(Set set) {
        return setsDao.insertSet(set);
    }

    public void insertSets(Set... sets) {
        setsDao.insertSets(sets);
    }

    public void updateSets(Set...sets) {
        setsDao.updateSets(sets);
    }

    public List<Set> getAllSets() {
        return setsDao.getAllSets();
    }

    public Set getSetById(long setId) {
        return setsDao.getSetById(setId);
    }

    public List<Set> getSetsByTheme(int themeCode) {
        return setsDao.getSetsByTheme(themeCode);
    }

    public void insertLinks(Link... links) {
        linkDao.insertLinks(links);
    }

    public DictionaryInfo getDictionaryInfo() {
        return infoDao.getDictionaryInfo();
    }

    public long insertTheme(Theme theme) {
        return themeDao.insertTheme(theme);
    }

    public void insertThemes(Theme...themes) {
        themeDao.insertThemes(themes);
    }

    public List<Theme> getAllThemes() {
        return themeDao.getAllThemes();
    }
}
