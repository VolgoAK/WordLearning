package xyz.volgoak.wordlearning.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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

    public static final String TAG = "DictionaryCursorAdapter";

    private static final String[] FROM = new String[]{COLUMN_WORD, COLUMN_TRANSLATION,};
    private static final int[] TO = new int[]{R.id.tv_word_adapter, R.id.tv_translation_adapter};

    private static int mLayoutId = R.layout.redactor_cursor_adapter;
    private Context mContext;
    private WordSpeaker mSpeaker;

    private Drawable[] mProgresIcons = new Drawable[5];

    public DictionaryCursorAdapter(Cursor cursor, Context context){
        super(context, mLayoutId, cursor, FROM, TO, 0);
        mContext = context;
        mSpeaker = new WordSpeaker(context.getApplicationContext());

        mProgresIcons[0] = ContextCompat.getDrawable(context, R.drawable.ic_progress_0);
        mProgresIcons[1] = ContextCompat.getDrawable(context, R.drawable.ic_progress_1);
        mProgresIcons[2] = ContextCompat.getDrawable(context, R.drawable.ic_progress_2);
        mProgresIcons[3] = ContextCompat.getDrawable(context, R.drawable.ic_progress_3);
        mProgresIcons[4] = ContextCompat.getDrawable(context, R.drawable.ic_progress_4);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder =(ViewHolder) view.getTag();
        if(holder == null){
            holder = new ViewHolder();
            holder.wordText = (TextView) view.findViewById(R.id.tv_word_adapter);
            holder.translationText = (TextView) view.findViewById(R.id.tv_translation_adapter);
            holder.trainWTImage = (ImageView) view.findViewById(R.id.iv_progress_wt_adapter);
            holder.trainTWImage = (ImageView) view.findViewById(R.id.iv_progress_tw_adapter);
            holder.soundButton = (ImageButton) view.findViewById(R.id.adapter_button);
            view.setTag(holder);
        }
        holder.wordText.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_WORD)));
        holder.translationText.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_TRANSLATION)));

        int trainedWt = cursor.getInt(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_TRAINED_WT));
        int trainedTW = cursor.getInt(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_TRAINED_TW));
        try{
            holder.trainWTImage.setImageDrawable(mProgresIcons[trainedWt]);
            holder.trainTWImage.setImageDrawable(mProgresIcons[trainedTW]);
        }catch(IndexOutOfBoundsException ex){
            holder.trainWTImage.setImageDrawable(mProgresIcons[0]);
            holder.trainWTImage.setImageDrawable(mProgresIcons[0]);
            Log.e(TAG, "bindView: trained status too big", ex);
        }

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
        ImageView trainWTImage;
        ImageView trainTWImage;
        ImageButton soundButton;
    }
}
