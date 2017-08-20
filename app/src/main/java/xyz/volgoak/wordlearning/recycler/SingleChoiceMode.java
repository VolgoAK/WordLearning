package xyz.volgoak.wordlearning.recycler;

import android.os.Bundle;

import java.util.List;

/**
 * Created by Volgoak on 20.08.2017.
 */

public class SingleChoiceMode implements ChoiceMode {

    private int checkedPosition = -1;

    @Override
    public void setChecked(int position, boolean checked) {
        if(checked){
            checkedPosition = position;
        }
    }

    @Override
    public boolean isChecked(int position) {
        return position == checkedPosition;
    }

    @Override
    public void onSaveInstanceState(Bundle instanceState) {

    }

    @Override
    public void restoreInstanceState(Bundle instanceState) {

    }

    @Override
    public int getCheckedCount() {
        return checkedPosition == -1 ? 0 : 1;
    }

    @Override
    public void clearChecks() {
        checkedPosition = -1;
    }

    @Override
    public List<Integer> getCheckedList() {
        return null;
    }

    @Override
    public int getCheckedPosition() {
        return checkedPosition;
    }
}
