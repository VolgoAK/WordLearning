package xyz.volgoak.wordlearning.recycler;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.data.DatabaseContract;

/**
 * Created by Volgoak on 18.08.2017.
 */

public class WordsRowController extends RowController{

    static boolean isColumnBound = false;

    private static int columnWordIndex = -1;
    private static int columnTranslationIndex = -1;
    private static int columnTrainWtIndex = -1;
    private static int columnTrainTwIndex = -1;
    private static int columnIdIndex = -1;

    View rootView;
    TextView wordText;
    TextView translationText;
    ImageView trainWTImage;
    ImageView trainTWImage;
    ImageButton soundButton;

    private static boolean isDrawableInit = false;
    private static Drawable[] sProgresIcons = new Drawable[5];

    public WordsRowController(View view, Context context) {
        super(view, context);
        rootView = view;
        wordText = (TextView) view.findViewById(R.id.tv_word_adapter);
        translationText = (TextView) view.findViewById(R.id.tv_translation_adapter);
        trainWTImage = (ImageView) view.findViewById(R.id.iv_progress_wt_adapter);
        trainTWImage = (ImageView) view.findViewById(R.id.iv_progress_tw_adapter);
        soundButton = (ImageButton) view.findViewById(R.id.adapter_button);

        if(!isDrawableInit) initDrawables(context);
    }

    @Override
    public void bindController(Cursor cursor) {
        if(!isColumnBound) bindColumns(cursor);
        wordText.setText(cursor.getString(columnWordIndex));
        translationText.setText(cursor.getString(columnTranslationIndex));

        try{
            trainTWImage.setImageDrawable(sProgresIcons[cursor.getInt(columnTrainTwIndex)]);
            trainWTImage.setImageDrawable(sProgresIcons[cursor.getInt(columnTrainWtIndex)]);
        }catch(IndexOutOfBoundsException ex){
            ex.printStackTrace();
            trainWTImage.setImageResource(R.drawable.ic_progress_4);
            trainTWImage.setImageResource(R.drawable.ic_progress_4);
        }

        if(mLongClickListener != null){
            final long itemId = cursor.getLong(columnIdIndex);
            rootView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mLongClickListener.onClick(v, getAdapterPosition(), itemId);
                    return true;
                }
            });
        }
    }

    public static void bindColumns(Cursor cursor){
        columnIdIndex = cursor.getColumnIndex(DatabaseContract.Words._ID);
        columnWordIndex = cursor.getColumnIndex(DatabaseContract.Words.COLUMN_WORD);
        columnTranslationIndex = cursor.getColumnIndex(DatabaseContract.Words.COLUMN_TRANSLATION);
        columnTrainTwIndex = cursor.getColumnIndex(DatabaseContract.Words.COLUMN_TRAINED_TW);
        columnTrainWtIndex = cursor.getColumnIndex(DatabaseContract.Words.COLUMN_TRAINED_WT);
        isColumnBound = true;
    }

    public static void initDrawables(Context context){
        sProgresIcons[0] = ContextCompat.getDrawable(context, R.drawable.ic_progress_0);
        sProgresIcons[1] = ContextCompat.getDrawable(context, R.drawable.ic_progress_1);
        sProgresIcons[2] = ContextCompat.getDrawable(context, R.drawable.ic_progress_2);
        sProgresIcons[3] = ContextCompat.getDrawable(context, R.drawable.ic_progress_3);
        sProgresIcons[4] = ContextCompat.getDrawable(context, R.drawable.ic_progress_4);
        isDrawableInit = true;
    }
}
