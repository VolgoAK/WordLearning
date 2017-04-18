package xyz.volgoak.wordlearning.utils;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.data.DatabaseContract;

import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_TRAINED_TW;
import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_TRAINED_WT;
import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_TRANSLATION;
import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_WORD;

/**
 * Created by Volgoak on 15.04.2017.
 */

public class DictionaryCursorAdapter extends SimpleCursorAdapter {

    private static final String[] FROM = new String[]{COLUMN_WORD, COLUMN_TRANSLATION, COLUMN_TRAINED_WT, COLUMN_TRAINED_TW};
    private static final int[] TO = new int[]{R.id.adapter_text_1, R.id.adapter_text_2, R.id.adapter_text_3, R.id.adapter_text_4};

    private static int mLayoutId = R.layout.redactor_cursor_adapter;
    private Context mContext;
    private WordSpeaker mSpeaker;

    public DictionaryCursorAdapter(Cursor cursor, Context context){
        super(context, mLayoutId, cursor, FROM, TO, 0);
        mContext = context;
        mSpeaker = new WordSpeaker(context.getApplicationContext());
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder =(ViewHolder) view.getTag();
        if(holder == null){
            holder = new ViewHolder();
            holder.wordText = (TextView) view.findViewById(R.id.adapter_text_1);
            holder.translationText = (TextView) view.findViewById(R.id.adapter_text_2);
            holder.trainWtText = (TextView) view.findViewById(R.id.adapter_text_3);
            holder.trainTWText = (TextView) view.findViewById(R.id.adapter_text_4);
            holder.soundButton = (ImageButton) view.findViewById(R.id.adapter_button);
            view.setTag(holder);
        }
        holder.wordText.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_WORD)));
        holder.translationText.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_TRANSLATION)));
        holder.trainWtText.setText(cursor.getInt(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_TRAINED_WT)) + "");
        holder.trainTWText.setText(cursor.getInt(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_TRAINED_TW)) + "");

        final String word = cursor.getString(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_WORD));
        holder.soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpeaker.speakWord(word);
            }
        });
    }

    private class ViewHolder{
        TextView wordText;
        TextView translationText;
        TextView trainWtText;
        TextView trainTWText;
        ImageButton soundButton;
    }
}
