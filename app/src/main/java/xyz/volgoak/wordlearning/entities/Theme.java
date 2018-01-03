package xyz.volgoak.wordlearning.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.Expose;

import xyz.volgoak.wordlearning.data.DatabaseContract.Themes;

/**
 * Created by alex on 1/3/18.
 */

@Entity(tableName = Themes.TABLE_NAME, indices = { @Index(value = Themes.COLUMN_CODE, unique = true)})
public class Theme implements DataEntity {

    @ColumnInfo(name = Themes._ID)
    @PrimaryKey(autoGenerate = true)
    private long id;
    @Expose
    @ColumnInfo(name = Themes.COLUMN_NAME)
    private String name;
    @Expose
    @ColumnInfo(name = Themes.COLUMN_CODE)
    private int code;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
