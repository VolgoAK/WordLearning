package xyz.volgoak.wordlearning.entities;

import com.google.gson.annotations.Expose;

import java.util.List;


/**
 * Created by alex on 1/3/18.
 */

public class Dictionary {

    @Expose
    private List<Set> sets;
    @Expose
    private List<Theme> themes;

    public List<Theme> getThemes() {
        return themes;
    }

    public void setThemes(List<Theme> themes) {
        this.themes = themes;
    }

    public List<Set> getSets() {
        return sets;
    }

    public void setSets(List<Set> sets) {
        this.sets = sets;
    }
}
