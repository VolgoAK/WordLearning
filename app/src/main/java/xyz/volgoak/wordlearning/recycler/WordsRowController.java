package xyz.volgoak.wordlearning.recycler;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import timber.log.Timber;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.entities.DataEntity;
import xyz.volgoak.wordlearning.entities.Word;
import xyz.volgoak.wordlearning.utils.WordSpeaker;

/**
 * Created by Volgoak on 18.08.2017.
 */

class WordsRowController extends NewRowController {
    
    public static final String TAG = WordsRowController.class.getSimpleName();

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

    public WordsRowController(View view, Context context, RecyclerAdapter adapter) {
        super(view, context, adapter);
        rootView = view;
        wordText = view.findViewById(R.id.tv_word_adapter);
        translationText = view.findViewById(R.id.tv_translation_adapter);
        trainWTImage = view.findViewById(R.id.iv_progress_wt_adapter);
        trainTWImage = view.findViewById(R.id.iv_progress_tw_adapter);
        inDictionaryImage = view.findViewById(R.id.iv_status_word_controller);
        soundButton = view.findViewById(R.id.adapter_button);
        checkBox = view.findViewById(R.id.cb_selected_dict_adapter);

        if (!isDrawableInit) initDrawables(context);
    }

    @Override
    public void bindController(DataEntity dataEntity) {
        Word word = (Word) dataEntity;

        wordText.setText(word.getWord());
        translationText.setText(word.getTranslation());

        try {
            trainTWImage.setImageDrawable(sProgresIcons[word.getTrainedTw()]);
        } catch (IndexOutOfBoundsException ex) {
            Timber.e(ex);
            trainTWImage.setImageResource(R.drawable.ic_progress_4);
        }

        try {
            trainWTImage.setImageDrawable(sProgresIcons[word.getTrainedWt()]);
        } catch (IndexOutOfBoundsException ex) {
            Timber.e(ex);
            trainWTImage.setImageResource(R.drawable.ic_progress_4);
        }

        int status = word.getStatus();
        inDictionaryImage.setVisibility(status == DatabaseContract.Words.IN_DICTIONARY
                ? View.VISIBLE : View.INVISIBLE);

        rootView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return mAdapter.onControllerLongClick(WordsRowController.this, v, getAdapterPosition());
            }
        });

        rootView.setOnClickListener((v) -> {
            mAdapter.onControllerClick(WordsRowController.this, v, getAdapterPosition());
        });

        soundButton.setOnClickListener((v) -> {
            WordSpeaker.speakWord(word.getWord());
        });
    }

    @Override
    public void setChecked(boolean checked) {
        if (isSelectable) checkBox.setChecked(checked);
    }

    @Override
    public void setSelectable(boolean selectable) {
        isSelectable = selectable;
        checkBox.setVisibility(selectable ? View.VISIBLE : View.GONE);
    }

    public static void initDrawables(Context context) {
        sProgresIcons[0] = ContextCompat.getDrawable(context, R.drawable.ic_progress_0);
        sProgresIcons[1] = ContextCompat.getDrawable(context, R.drawable.ic_progress_1);
        sProgresIcons[2] = ContextCompat.getDrawable(context, R.drawable.ic_progress_2);
        sProgresIcons[3] = ContextCompat.getDrawable(context, R.drawable.ic_progress_3);
        sProgresIcons[4] = ContextCompat.getDrawable(context, R.drawable.ic_progress_4);
        isDrawableInit = true;
    }
}
