package xyz.volgoak.wordlearning.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.entities.Word;

/**
 * Created by Volgoak on 18.08.2017.
 */

public class WordsRecyclerAdapter extends RecyclerAdapter {

    public WordsRecyclerAdapter(Context context, List<Word> wordList, RecyclerView rv){
        super(context, wordList, rv);
    }

    @Override
    public WordsRowController onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.redactor_cursor_adapter, parent, false);
        return new WordsRowController(view, mContext, this);
    }

}
