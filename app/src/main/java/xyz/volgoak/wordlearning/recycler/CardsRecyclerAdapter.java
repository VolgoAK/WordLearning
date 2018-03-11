package xyz.volgoak.wordlearning.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import timber.log.Timber;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.databinding.WordCardHolderBinding;
import xyz.volgoak.wordlearning.entities.Word;

/**
 * Created by alex on 1/26/18.
 */

public class CardsRecyclerAdapter extends RecyclerView.Adapter<WordCardRowController> {

    private List<Word> dataList;


    @Override
    public WordCardRowController onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        WordCardHolderBinding binding = WordCardHolderBinding.inflate(inflater, parent, false);
        return  new WordCardRowController(binding);
    }

    @Override
    public void onBindViewHolder(WordCardRowController holder, int position) {
        Timber.d("onBindViewHolder: position " + position);
        holder.bindController(dataList.get(position % dataList.size()));
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : Integer.MAX_VALUE;
    }

    public void setDataList(List<Word> dataList) {
        this.dataList = dataList;
    }
}
