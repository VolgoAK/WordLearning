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
 * Created by Volgoak on 16.08.2017.
 */

public abstract class CursorRecyclerAdapter<RC extends RowController> extends RecyclerView.Adapter<RC>{

    protected Context mContext;
    protected Cursor mCursor;
    private AdapterClickListener mAdapterClickListener;
    private RecyclerView mRecyclerView;
    ChoiceMode mChoiceMode;

    public CursorRecyclerAdapter(Context context, Cursor cursor, RecyclerView rv){
        mContext = context;
        mCursor = cursor;
        mRecyclerView = rv;
    }

    @Override
    public void onBindViewHolder(RowController controller, int position) {
        mCursor.moveToPosition(position);
        controller.bindController(mCursor);
        if(mChoiceMode != null){
            controller.setChecked(mChoiceMode.isChecked(position));
        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        long id = -1;
        if(mCursor != null){
            mCursor.moveToPosition(position);
            id = mCursor.getLong(mCursor.getColumnIndex("_id"));
        }
        return id;
    }

    public void setChoiceMode(ChoiceMode choiceMode){
        mChoiceMode = choiceMode;
    }

    public void changeCursor(Cursor cursor){
        if(mCursor != null){
            mCursor.close();
        }
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public Cursor getCursor(){
        return mCursor;
    }

    public void closeCursor(){
        if(mCursor != null && !mCursor.isClosed()){
            mCursor.close();
        }
    }

    public void setAdapterClickListener(AdapterClickListener listener){
        mAdapterClickListener = listener;
    }

    public void onControllerClick(RowController controller, View root, int position){
        if(mAdapterClickListener != null){
            mAdapterClickListener.onClick(root, position, getItemId(position));
        }
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

    public boolean onControllerLongClick(RowController controller, View root, int position){
        if(mAdapterClickListener != null){
            return mAdapterClickListener.onLongClick(root, position, getItemId(position));
        }
        return false;
    }

    public Context getContext(){
        return mContext;
    }

    public interface AdapterClickListener{
        void onClick(View root, int position, long id);
        boolean onLongClick(View root, int position, long id);
    }
}
