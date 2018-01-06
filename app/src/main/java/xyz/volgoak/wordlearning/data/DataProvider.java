package xyz.volgoak.wordlearning.data;

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

    public void insertLink(Word word, int setId) {

    }

    public long insertWord(Word word) {
        return wordDao.insertWord(word);
    }

    public void insertWords(Word... words) {
        wordDao.insertWords(words);
    }

    public void updateWords(Word... words) {
        wordDao.udateWords(words);
    }

    public List<Word> getWordsByTrained(String trainedType, int wordsLimit, int trainedLimit) {
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

    public List<Set> getAllSets() {
        return setsDao.getAllSets();
    }

    public Set getSetById(int setId) {
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

    public int insertTheme(Theme theme) {
        return themeDao.insertTheme(theme);
    }

    public List<Theme> getAllThemes() {
        return themeDao.getAllThemes();
    }
}
