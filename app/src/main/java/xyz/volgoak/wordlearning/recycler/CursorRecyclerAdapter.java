package xyz.volgoak.wordlearning.recycler;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import xyz.volgoak.wordlearning.R;

/**
 * Created by Volgoak on 16.08.2017.
 */

public abstract class CursorRecyclerAdapter<RC extends RowController> extends RecyclerView.Adapter<RC>{

    public static final String TAG = "CursorRecyclerAdapter";

    protected Context mContext;
    protected Cursor mCursor;
    private AdapterClickListener mAdapterClickListener;
    private RecyclerView mRecyclerView;
    private boolean mSelectable;
    ChoiceMode mChoiceMode;

    //dirty ugly thing
    //I can't get hidden ViewHolder though it's bound
    //but I need to change it without recreating all
    //rows. For do this I hold position of last bound
    //holder and invalidate by position
    private int mLastCreated = 0;

    public CursorRecyclerAdapter(Context context, Cursor cursor, RecyclerView rv){
        mContext = context;
        mCursor = cursor;
        mRecyclerView = rv;
    }

    @Override
    public void onBindViewHolder(RowController controller, int position) {
        mCursor.moveToPosition(position);
        Log.d(TAG, "onBind position " + position + " selectable " + mSelectable);
        if(mChoiceMode != null){
            controller.setSelectable(mSelectable);
            controller.setChecked(mChoiceMode.isChecked(position));
        }
        controller.bindController(mCursor);
        //dirty ugly thing
        mLastCreated = position;
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

    private void setSelectable(boolean selectable){
        if(mSelectable != selectable) {

            for (int a = 0; a < getItemCount(); a++) {
                RowController controller = (RowController) mRecyclerView.findViewHolderForAdapterPosition(a);

                if (controller != null) {
                    controller.setSelectable(selectable);
                    Log.d(TAG, "setSelectable: "+ selectable + " for " + a);
                }else Log.d(TAG, "setSelectable: null at position " + a);
            }
            mSelectable = selectable;
            //dirty trick
            notifyItemChanged(mLastCreated);
        }
    }

    public void setChoiceMode(ChoiceMode choiceMode){
        mChoiceMode = choiceMode;
        if(choiceMode instanceof MultiChoiceMode){
            setSelectable(true);
        }else setSelectable(false);
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

    public Context getContext(){
        return mContext;
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

    public interface AdapterClickListener{
        void onClick(View root, int position, long id);
        boolean onLongClick(View root, int position, long id);
    }
}
