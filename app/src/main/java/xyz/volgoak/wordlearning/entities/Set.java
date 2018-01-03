package xyz.volgoak.wordlearning.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import java.util.List;
import xyz.volgoak.wordlearning.data.DatabaseContract.*;

/**
 * Created by alex on 1/3/18.
 */

@Entity(tableName = Sets.TABLE_NAME, foreignKeys = @ForeignKey(entity = Theme.class, parentColumns = Themes.COLUMN_CODE,
        childColumns = Sets.COLUMN_THEME_CODE))
public class Set implements DataEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = Sets._ID)
    private long id;

    @Expose
    @ColumnInfo(name = Sets.COLUMN_NAME)
    private String name;
    @Expose
    @ColumnInfo(name = Sets.COLUMN_DESCRIPTION)
    private String description;
    @Expose
    @ColumnInfo(name = Sets.COLUMN_IMAGE_URL)
    private String imageUrl;
    @Expose
    @ColumnInfo(name = Sets.COLUMN_LANG)
    private String lang;
    @ColumnInfo(name = Sets.COLUMN_NUM_OF_WORDS)
    private int wordsCount;
    @ColumnInfo(name = Sets.COLUMN_STATUS)
    private int status;
    @ColumnInfo(name = Sets.COLUMN_VISIBILITY)
    private int visibitity;

    @Expose
    @ColumnInfo(name = Sets.COLUMN_THEME_CODE)
    private int themeCode;
    @Expose
    @Ignore
    private List<Word> words;

    public Set() {
    }

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

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Set) {
            return id == ((Set) obj).getId();
        }
        return false;
    }
}
