package xyz.volgoak.wordlearning.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import xyz.volgoak.wordlearning.entities.Entity;

/**
 * Created by alex on 1/3/18.
 */

// TODO: 1/3/18 rename
public abstract class NewRowController extends RecyclerView.ViewHolder{



    protected Context mContext;
    protected RecyclerAdapter mAdapter;

    public NewRowController(View view, Context context, final RecyclerAdapter adapter){
        super(view);
        mContext = context;
        mAdapter = adapter;
    }

    public abstract void bindController(Entity entity);

    public abstract void setChecked(boolean checked);

    public void setSelectable(boolean selectable){
        //do nothing
    }
}
