package xyz.volgoak.wordlearning.recycler;

import android.os.Bundle;

import java.util.List;

/**
 * Created by Volgoak on 20.08.2017.
 */

public class MultiChoiceMode implements ChoiceMode {

    private final static String SAVED_STATE = "saved_state";

    private ParcelableSparseBooleanArray mSparceBooleanArray = new ParcelableSparseBooleanArray();

    @Override
    public void setChecked(int position, boolean checked) {
        if (checked){
            mSparceBooleanArray.put(position, checked);
        }else mSparceBooleanArray.delete(position);
    }

    @Override
    public boolean isChecked(int position) {
        return mSparceBooleanArray.get(position);
    }

    @Override
    public void onSaveInstanceState(Bundle instanceState) {
        instanceState.putParcelable(SAVED_STATE, mSparceBooleanArray);
    }

    @Override
    public void restoreInstanceState(Bundle instanceState) {
        mSparceBooleanArray = instanceState.getParcelable(SAVED_STATE);
    }

    @Override
    public int getCheckedCount() {
        return mSparceBooleanArray.size();
    }

    @Override
    public void clearChecks() {
        mSparceBooleanArray.clear();
    }

    @Override
    public List<Integer> getCheckedList() {
        return null;
    }

    @Override
    public int getCheckedPosition() {
        return 0;
    }
}