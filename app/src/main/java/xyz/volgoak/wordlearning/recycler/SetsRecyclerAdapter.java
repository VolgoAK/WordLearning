package xyz.volgoak.wordlearning.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.entities.Set;

/**
 * Created by Volgoak on 18.08.2017.
 */

public class SetsRecyclerAdapter extends RecyclerAdapter {

    private SetStatusChanger mSetStatusChanger;

    public SetsRecyclerAdapter(Context context, List<Set> entityList, RecyclerView recyclerView){
        super(context, entityList, recyclerView);

    }

    @Override
    public SetsRowController onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sets_cursor_adapter, parent, false);
        SetsRowController controller = new SetsRowController(view, mContext, this);
        controller.setmStatusChanger(mSetStatusChanger);
        return controller;
    }

    @Override
    public void onControllerClick(NewRowController controller, View root, int position) {
        super.onControllerClick(controller, root, position);

    }

    public void setSetStatusChanger(SetStatusChanger SetStatusChanger) {
        mSetStatusChanger = SetStatusChanger;
    }

    public interface SetStatusChanger{
        void changeSetStatus(long id);
    }
}
