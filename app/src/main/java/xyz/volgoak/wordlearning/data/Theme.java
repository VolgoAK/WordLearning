package xyz.volgoak.wordlearning.data;

import com.google.gson.annotations.Expose;

/**
 * Created by alex on 1/3/18.
 */

public class Theme implements Entity{

    private long id;
    @Expose
    private String name;
    @Expose
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
