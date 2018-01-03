package xyz.volgoak.wordlearning.recycler;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import xyz.volgoak.wordlearning.entities.DataEntity;

/**
 * Created by alex on 1/3/18.
 */

public abstract class RecyclerAdapter<RC extends NewRowController> extends RecyclerView.Adapter<RC> {
    public static final String TAG = "CursorRecyclerAdapter";

    protected Context mContext;
    protected List<DataEntity> mEntities;
    private AdapterClickListener mAdapterClickListener;
    private AdapterLongClickListener mAdapterLongClickListener;
    private RecyclerView mRecyclerView;
    private boolean mSelectable;
    private ChoiceMode mChoiceMode;

    public RecyclerAdapter(Context context, List<DataEntity> entities, RecyclerView rv){
        mContext = context;
        mEntities = entities;
        mRecyclerView = rv;
    }

    @Override
    public void onBindViewHolder(NewRowController controller, int position) {
        controller.setSelectable(mSelectable);
        if(mChoiceMode != null){
            controller.setChecked(mChoiceMode.isChecked(position));
        }
        controller.bindController(mEntities.get(position));
    }

    @Override
    public int getItemCount() {
        return mEntities.size();
    }

    @Override
    public long getItemId(int position) {
        return mEntities.get(position).getId();
    }

    private void setSelectable(boolean selectable){
        if(mSelectable != selectable) {
            mSelectable = selectable;
            for (int a = 0; a < getItemCount(); a++) {
                NewRowController controller = (NewRowController) mRecyclerView.findViewHolderForAdapterPosition(a);

                if (controller != null) {
                    controller.setSelectable(selectable);
                    if(selectable && mChoiceMode != null){
                        controller.setChecked(mChoiceMode.isChecked(controller.getAdapterPosition()));
                    }
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

    public void changeData(List<DataEntity> entities){
        mEntities = entities;
        notifyDataSetChanged();
    }

    public List<DataEntity> getData(){
        return mEntities;
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

    public void onControllerClick(NewRowController controller, View root, int position){
        if(mChoiceMode != null){
            if(mChoiceMode instanceof SingleChoiceMode) {
                if(mChoiceMode.getCheckedPosition() != -1){
                    NewRowController rc = (NewRowController)
                            mRecyclerView.findViewHolderForAdapterPosition(mChoiceMode.getCheckedPosition());
                    if(rc != null) rc.setChecked(false);
                }
                controller.setChecked(true);
                mChoiceMode.setChecked(position, true);
            }else if(mChoiceMode instanceof MultiChoiceMode) {
                //change checked state
                boolean checked = !mChoiceMode.isChecked(position);
                controller.setChecked(checked);
                mChoiceMode.setChecked(position, checked);
            }
        }
        if(mAdapterClickListener != null){
            mAdapterClickListener.onClick(root, position, getItemId(position));
        }
    }

    public boolean onControllerLongClick(NewRowController controller, View root, int position){
        if(mAdapterLongClickListener != null){
            return  mAdapterLongClickListener.onLongClick(root, position, getItemId(position));
        }
        return false;
    }

    public void notifyEntityChanged(DataEntity dataEntity) {
        int position = mEntities.indexOf(dataEntity);
        if(position != -1) {
            mEntities.set(position, dataEntity);
            notifyItemChanged(position);
        }
    }

    public interface AdapterClickListener{
        void onClick(View root, int position, long id);
    }

    public interface AdapterLongClickListener {
        boolean onLongClick(View root, int position, long id);
    }
}
