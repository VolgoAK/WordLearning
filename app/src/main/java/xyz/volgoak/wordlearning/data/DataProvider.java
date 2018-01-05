package xyz.volgoak.wordlearning.data;

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
}
