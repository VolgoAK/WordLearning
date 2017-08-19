package xyz.volgoak.wordlearning.recycler;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Volgoak on 17.08.2017.
 */

public abstract class RowController extends RecyclerView.ViewHolder{

    protected Context mContext;
    protected CursorRecyclerAdapter.ControllerClickListener mClickListener;
    protected CursorRecyclerAdapter.ControllerLongClickListener mLongClickListener;

    public RowController(View view, Context context){
        super(view);
        mContext = context;
    }

    public abstract void bindController(Cursor cursor);

    public void setClickListener(CursorRecyclerAdapter.ControllerClickListener listener){
        mClickListener = listener;
    }

    public void setLongClickListener(CursorRecyclerAdapter.ControllerLongClickListener listener){
        mLongClickListener = listener;
    }

    /*public void setOnClickListener(View.OnClickListener listener){
        mClickListener = listener;
    }

    public void setOnLongClickListener(View.OnLongClickListener)*/
}
