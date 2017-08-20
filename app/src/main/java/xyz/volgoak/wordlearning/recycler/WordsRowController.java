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
import xyz.volgoak.wordlearning.utils.WordSpeaker;

/**
 * Created by Volgoak on 18.08.2017.
 */

 class WordsRowController extends RowController{

    static boolean isColumnBound = false;

    private static int columnWordIndex = -1;
    private static int columnTranslationIndex = -1;
    private static int columnTrainWtIndex = -1;
    private static int columnTrainTwIndex = -1;
    private static int columnIdIndex = -1;

    private static boolean isDrawableInit = false;
    private static Drawable[] sProgresIcons = new Drawable[5];

    private WordSpeaker mSpeaker;

    View rootView;
    TextView wordText;
    TextView translationText;
    ImageView trainWTImage;
    ImageView trainTWImage;
    ImageButton soundButton;

    public WordsRowController(View view, Context context, CursorRecyclerAdapter adapter) {
        super(view, context, adapter);
        rootView = view;
        wordText = (TextView) view.findViewById(R.id.tv_word_adapter);
        translationText = (TextView) view.findViewById(R.id.tv_translation_adapter);
        trainWTImage = (ImageView) view.findViewById(R.id.iv_progress_wt_adapter);
        trainTWImage = (ImageView) view.findViewById(R.id.iv_progress_tw_adapter);
        soundButton = (ImageButton) view.findViewById(R.id.adapter_button);

        mSpeaker = new WordSpeaker(mContext);

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

        rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                    return mAdapter.onControllerLongClick(v, getAdapterPosition());
                }
        });

        final String word = cursor.getString(columnWordIndex);
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSpeaker.speakWord(word);
            }
        });
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
