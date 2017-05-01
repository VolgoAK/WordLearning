package xyz.volgoak.wordlearning.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.data.DatabaseContract;

/**
 * Created by Volgoak on 29.04.2017.
 */

public class DictionaryRecyclerAdapter extends CursorRecyclerAdapter<DictionaryRecyclerAdapter.ViewHolder>{

    public static final String TAG = "DicRecyclerAdapter";

    private Drawable[] mProgresIcons = new Drawable[5];
    private WordSpeaker mSpeaker;
    private Context mContext;

    public DictionaryRecyclerAdapter(Cursor cursor, Context context){
        super(cursor);
        mContext = context;

        mSpeaker = new WordSpeaker(context.getApplicationContext());

        mProgresIcons[0] = ContextCompat.getDrawable(context, R.drawable.ic_progress_0);
        mProgresIcons[1] = ContextCompat.getDrawable(context, R.drawable.ic_progress_1);
        mProgresIcons[2] = ContextCompat.getDrawable(context, R.drawable.ic_progress_2);
        mProgresIcons[3] = ContextCompat.getDrawable(context, R.drawable.ic_progress_3);
        mProgresIcons[4] = ContextCompat.getDrawable(context, R.drawable.ic_progress_4);
    }

    @Override
    public void onBindViewHolderCursor(ViewHolder holder, Cursor cursor) {
        holder.wordText.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_WORD)));
        holder.translationText.setText(cursor.getString(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_TRANSLATION)));

        //set  image for trained status of word
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
        Log.d(TAG, "onBindViewHolderCursor: ");

        //set listener for speak word
        final String word = cursor.getString(cursor.getColumnIndex(DatabaseContract.Words.COLUMN_WORD));
        holder.soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpeaker.speakWord(word);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.redactor_cursor_adapter, parent, false);
        return new ViewHolder(view);
    }



    class ViewHolder extends RecyclerView.ViewHolder{

         TextView wordText;
         TextView translationText;
         ImageView trainWTImage;
         ImageView trainTWImage;
         ImageButton soundButton;

         public ViewHolder(View view){
             super(view);
             wordText = (TextView) view.findViewById(R.id.tv_word_adapter);
             translationText = (TextView) view.findViewById(R.id.tv_translation_adapter);
             trainWTImage = (ImageView) view.findViewById(R.id.iv_progress_wt_adapter);
             trainTWImage = (ImageView) view.findViewById(R.id.iv_progress_tw_adapter);
             soundButton = (ImageButton) view.findViewById(R.id.adapter_button);
         }

    }
}
