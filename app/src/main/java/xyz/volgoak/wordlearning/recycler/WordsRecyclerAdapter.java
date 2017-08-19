package xyz.volgoak.wordlearning.recycler;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.volgoak.wordlearning.R;

/**
 * Created by Volgoak on 18.08.2017.
 */

public class WordsRecyclerAdapter extends CursorRecyclerAdapter {

    public WordsRecyclerAdapter(Context context, Cursor cursor){
        super(context, cursor);
    }

    @Override
    public WordsRowController onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.redactor_cursor_adapter, parent, false);
        return new WordsRowController(view, mContext, this);
    }

}
