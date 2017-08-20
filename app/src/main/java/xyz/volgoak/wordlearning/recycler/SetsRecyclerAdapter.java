package xyz.volgoak.wordlearning.recycler;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.volgoak.wordlearning.R;

/**
 * Created by Volgoak on 18.08.2017.
 */

public class SetsRecyclerAdapter extends CursorRecyclerAdapter {

    private SetStatusChanger mSetStatusChanger;
    private RecyclerView mRecyclerView;

    public SetsRecyclerAdapter(Context context, Cursor cursor, RecyclerView recyclerView){
        super(context, cursor);
        mRecyclerView = recyclerView;
    }

    @Override
    public SetsRowController onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sets_cursor_adapter, parent, false);
        SetsRowController controller = new SetsRowController(view, mContext, this);
        controller.setmStatusChanger(mSetStatusChanger);
        return controller;
    }

    @Override
    public void onControllerClick(RowController controller, View root, int position) {
        super.onControllerClick(controller, root, position);
        if(mChoiceMode != null){
            if(mChoiceMode.getCheckedCount() > 0){
                int checkedPosition = mChoiceMode.getCheckedPosition();
                SetsRowController rc = (SetsRowController)
                        mRecyclerView.findViewHolderForAdapterPosition(checkedPosition);
                if(rc != null)rc.setChecked(false);
            }

            controller.setChecked(true);

            mChoiceMode.setChecked(position, true);
            Log.d("SetsRecyclerAdapter", "onControllerClick: set activated");
        }
    }

    public void setSetStatusChanger(SetStatusChanger SetStatusChanger) {
        mSetStatusChanger = SetStatusChanger;
    }

    public interface SetStatusChanger{
        void changeSetStatus(long id);
    }
}
