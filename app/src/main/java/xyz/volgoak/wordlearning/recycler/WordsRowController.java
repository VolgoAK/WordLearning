package xyz.volgoak.wordlearning.recycler;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;
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
    private static int columnInDictionaryIndes = -1;

    private static boolean isDrawableInit = false;
    private static Drawable[] sProgresIcons = new Drawable[5];

    private boolean isSelectable;

    View rootView;
    TextView wordText;
    TextView translationText;
    ImageView trainWTImage;
    ImageView trainTWImage;
    ImageView inDictionaryImage;
    ImageButton soundButton;
    CheckBox checkBox;

    public WordsRowController(View view, Context context, CursorRecyclerAdapter adapter) {
        super(view, context, adapter);
        rootView = view;
        wordText = (TextView) view.findViewById(R.id.tv_word_adapter);
        translationText = (TextView) view.findViewById(R.id.tv_translation_adapter);
        trainWTImage = (ImageView) view.findViewById(R.id.iv_progress_wt_adapter);
        trainTWImage = (ImageView) view.findViewById(R.id.iv_progress_tw_adapter);
        inDictionaryImage = (ImageView) view.findViewById(R.id.iv_status_word_controller);
        soundButton = (ImageButton) view.findViewById(R.id.adapter_button);
        checkBox = (CheckBox) view.findViewById(R.id.cb_selected_dict_adapter);

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

        int status = cursor.getInt(columnInDictionaryIndes);
        inDictionaryImage.setVisibility(status == DatabaseContract.Words.IN_DICTIONARY
                ? View.VISIBLE : View.INVISIBLE);

        rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                    return mAdapter.onControllerLongClick(WordsRowController.this, v, getAdapterPosition());
                }
        });

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapter.onControllerClick(WordsRowController.this, v, getAdapterPosition());
            }
        });

        final String word = cursor.getString(columnWordIndex);
        // TODO: 25.08.2017 many speakers is cause of memory leaks. Fix it 
        soundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WordSpeaker.speakWord(word);
            }
        });
    }

    @Override
    public void setChecked(boolean checked) {
        if(isSelectable) checkBox.setChecked(checked);
    }

    @Override
    public void setSelectable(boolean selectable) {
        isSelectable = selectable;
        checkBox.setVisibility(selectable ? View.VISIBLE : View.GONE);
    }

    public static void bindColumns(Cursor cursor){
        columnIdIndex = cursor.getColumnIndex(DatabaseContract.Words._ID);
        columnWordIndex = cursor.getColumnIndex(DatabaseContract.Words.COLUMN_WORD);
        columnTranslationIndex = cursor.getColumnIndex(DatabaseContract.Words.COLUMN_TRANSLATION);
        columnTrainTwIndex = cursor.getColumnIndex(DatabaseContract.Words.COLUMN_TRAINED_TW);
        columnTrainWtIndex = cursor.getColumnIndex(DatabaseContract.Words.COLUMN_TRAINED_WT);
        columnInDictionaryIndes = cursor.getColumnIndex(DatabaseContract.Words.COLUMN_STATUS);
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
