package xyz.volgoak.wordlearning.recycler;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import timber.log.Timber;
import xyz.volgoak.wordlearning.databinding.WordCardHolderBinding;
import xyz.volgoak.wordlearning.entities.Word;

/**
 * Created by alex on 1/26/18.
 */

public class CardsRecyclerAdapter extends RecyclerView.Adapter<WordCardRowController> {

    private List<Word> dataList;
    private WordListener progressResetListener;
    private WordListener removeListener;


    @Override
    public WordCardRowController onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        WordCardHolderBinding binding = WordCardHolderBinding.inflate(inflater, parent, false);
        return new WordCardRowController(binding, this);
    }

    @Override
    public void onBindViewHolder(WordCardRowController holder, int position) {
        Timber.d("onBindViewHolder: position %1$d", position);
        try {
            holder.bindController(dataList.get(position % dataList.size()));
        }catch (ArithmeticException ex) {
            Timber.e(ex);
        }
    }

    @Override
    public int getItemCount() {
        return dataList == null ? 0 : Integer.MAX_VALUE;
    }

    public void setDataList(List<Word> dataList) {
        this.dataList = dataList;
        notifyDataSetChanged();
    }

    public void setProgressResetListener(WordListener progressResetListener) {
        this.progressResetListener = progressResetListener;
    }

    public void setRemoveListener(WordListener removeListener) {
        this.removeListener = removeListener;
    }

    public void resetProgress(Word word) {
        Timber.d("resetProgress: ");
        if (progressResetListener != null) {
            progressResetListener.onAction(word);
        }
    }

    public void addOrRemove(Word word) {
        if (removeListener != null) {
            removeListener.onAction(word);
        }
    }

    public interface WordListener {
        void onAction(Word word);
    }
}
