package xyz.volgoak.wordlearning.utils;

import android.support.annotation.Nullable;

import java.util.NoSuchElementException;

/**
 * Created by alex on 3/20/18.
 */

public class Optional<M> {

    private final M optional;

    public Optional(@Nullable M optional) {
        this.optional = optional;
    }

    public boolean isEmpty() {
        return this.optional == null;
    }

    public M get() {
        if (optional == null) {
            throw new NoSuchElementException("No value present");
        }
        return optional;
    }
}
