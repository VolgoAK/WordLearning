package xyz.volgoak.wordlearning.recycler;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.entities.DataEntity;
import xyz.volgoak.wordlearning.entities.Word;

/**
 * Created by alex on 1/26/18.
 */

public class WordCardRowController extends NewRowController{

    private TextView tvWord;

    public WordCardRowController(View view, Context context, RecyclerAdapter adapter) {
        super(view, context, adapter);
        tvWord = view .findViewById(R.id.tv_word_cards);
    }

    @Override
    public void bindController(DataEntity dataEntity) {
        tvWord.setText(((Word) dataEntity).getWord());
    }

    @Override
    public void setChecked(boolean checked) {

    }
}
