package xyz.volgoak.wordlearning.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import xyz.volgoak.wordlearning.R;

/**
 * Created by alex on 1/26/18.
 */

public class CardsRecyclerAdapter extends RecyclerAdapter {

    public CardsRecyclerAdapter(Context context, List list, RecyclerView rv) {
        super(context, list, rv);
    }

    @Override
    public WordCardRowController onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.word_card_holder, parent, false);
        return  new WordCardRowController(view, mContext, this);
    }
}
