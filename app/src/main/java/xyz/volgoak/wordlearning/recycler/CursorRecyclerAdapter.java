package xyz.volgoak.wordlearning.recycler;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
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
    private ControllerClickListener mControllerClickListener;
    private ControllerLongClickListener mControllerLongClickListener;

    public CursorRecyclerAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursor = cursor;
    }

    @Override
    public void onBindViewHolder(RowController controller, int position) {
        mCursor.moveToPosition(position);
        controller.bindController(mCursor);
        controller.setClickListener(mControllerClickListener);
        controller.setLongClickListener(mControllerLongClickListener);
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

    public void setControllerClickListener(ControllerClickListener listener){
        mControllerClickListener = listener;
    }

    public void setControllerLongClickListener(ControllerLongClickListener listener){
        mControllerLongClickListener = listener;
    }

    public interface ControllerClickListener{
        void onClick(View root, int position, long id);
    }

    public interface ControllerLongClickListener{
        void onClick(View root, int position, long id);
    }
}
