package xyz.volgoak.wordlearning.recycler;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Volgoak on 17.08.2017.
 */

abstract class RowController extends RecyclerView.ViewHolder{

    protected Context mContext;
    protected CursorRecyclerAdapter mAdapter;

    public RowController(View view, Context context, final CursorRecyclerAdapter adapter){
        super(view);
        mContext = context;
        mAdapter = adapter;
    }

    public abstract void bindController(Cursor cursor);

}
