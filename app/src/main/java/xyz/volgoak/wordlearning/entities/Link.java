package xyz.volgoak.wordlearning.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import xyz.volgoak.wordlearning.data.DatabaseContract.*;

/**
 * Created by alex on 1/4/18.
 */

@Entity(tableName = WordLinks.TABLE_NAME, foreignKeys =
        {@ForeignKey(entity = Set.class, parentColumns = Sets._ID, childColumns = WordLinks.COLUMN_SET_ID),
        @ForeignKey(entity = Word.class, parentColumns = Words._ID, childColumns = WordLinks.COLUMN_WORD_ID)})
public class Link implements DataEntity {

    @ColumnInfo(name = WordLinks._ID)
    @PrimaryKey(autoGenerate = true)
    private long id;
    @ColumnInfo(name = WordLinks.COLUMN_WORD_ID)
    private long wordId;
    @ColumnInfo(name = WordLinks.COLUMN_SET_ID)
    private long idOfSet;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public long getWordId() {
        return wordId;
    }

    public void setWordId(long wordId) {
        this.wordId = wordId;
    }

    public long getIdOfSet() {
        return idOfSet;
    }

    public void setIdOfSet(long idOfSet) {
        this.idOfSet = idOfSet;
    }
}
