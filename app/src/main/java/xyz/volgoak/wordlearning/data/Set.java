package xyz.volgoak.wordlearning.data;

/**
 * Created by alex on 1/3/18.
 */

public class Set implements Entity{

    private String name;
    private String description;
    private String imageUrl;
    private String lang;
    private long id;
    private int wordsCount;
    private int status;
    private int visibitity;
    private int themeCode;

    public Set(String name) {
        this.name = name;
    }

    public Set(String name, String description, String imageUrl, String lang,
               long id, int wordsCount, int status, int visibitity, int themeCode) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.lang = lang;
        this.id = id;
        this.wordsCount = wordsCount;
        this.status = status;
        this.visibitity = visibitity;
        this.themeCode = themeCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public int getWordsCount() {
        return wordsCount;
    }

    public void setWordsCount(int wordsCount) {
        this.wordsCount = wordsCount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getVisibitity() {
        return visibitity;
    }

    public void setVisibitity(int visibitity) {
        this.visibitity = visibitity;
    }

    public int getThemeCode() {
        return themeCode;
    }

    public void setThemeCode(int themeCode) {
        this.themeCode = themeCode;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Set) {
            return id == ((Set) obj).getId();
        }
        return false;
    }
}
