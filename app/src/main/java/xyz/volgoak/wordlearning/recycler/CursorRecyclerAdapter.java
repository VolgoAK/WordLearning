package xyz.volgoak.wordlearning.recycler;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by Volgoak on 16.08.2017.
 */

public abstract class CursorRecyclerAdapter<RC extends RowController> extends RecyclerView.Adapter<RC>{

    public static final String TAG = "CursorRecyclerAdapter";

    protected Context mContext;
    protected Cursor mCursor;
    private AdapterClickListener mAdapterClickListener;
    private AdapterLongClickListener mAdapterLongClickListener;
    private RecyclerView mRecyclerView;
    private boolean mSelectable;
    private ChoiceMode mChoiceMode;

    public CursorRecyclerAdapter(Context context, Cursor cursor, RecyclerView rv){
        mContext = context;
        mCursor = cursor;
        mRecyclerView = rv;
    }

    @Override
    public void onBindViewHolder(RowController controller, int position) {
        mCursor.moveToPosition(position);
        Log.d(TAG, "onBind position " + position + " selectable " + mSelectable);
        controller.setSelectable(mSelectable);
        if(mChoiceMode != null){
            controller.setChecked(mChoiceMode.isChecked(position));
        }
        controller.bindController(mCursor);
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
            mSelectable = selectable;
            for (int a = 0; a < getItemCount(); a++) {
                RowController controller = (RowController) mRecyclerView.findViewHolderForAdapterPosition(a);

                if (controller != null) {
                    controller.setSelectable(selectable);
//                    Log.d(TAG, "setSelectable: "+ selectable + " for " + a);
                }
            }

            //dirty trick
            LinearLayoutManager llm =(LinearLayoutManager) mRecyclerView.getLayoutManager();
            int firstVisible = llm.findFirstVisibleItemPosition();
            int lastVisible = llm.findLastVisibleItemPosition();

            for(int a = 1; a < 4 ; a++){
                notifyItemChanged(firstVisible - a);
                notifyItemChanged(lastVisible + a);
            }

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

    public void setAdapterLongClickListener(AdapterLongClickListener adapterLongClickListener) {
        mAdapterLongClickListener = adapterLongClickListener;
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
        if(mAdapterLongClickListener != null){
            return  mAdapterLongClickListener.onLongClick(root, position, getItemId(position));
        }
        return false;
    }

    public interface AdapterClickListener{
        void onClick(View root, int position, long id);
    }

    public interface AdapterLongClickListener {
        boolean onLongClick(View root, int position, long id);
    }
}
