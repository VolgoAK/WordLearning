package xyz.volgoak.wordlearning.recycler;

import android.os.Bundle;

import java.util.List;

/**
 * Created by Volgoak on 20.08.2017.
 */

public interface ChoiceMode {
    void setChecked(int position, boolean checked);
    boolean isChecked(int position);
    void onSaveInstanceState(Bundle instanceState);
    void restoreInstanceState(Bundle instanceState);
    int getCheckedCount();
    void clearChecks();
    int getCheckedPosition();
    List<Integer> getCheckedList();
}
