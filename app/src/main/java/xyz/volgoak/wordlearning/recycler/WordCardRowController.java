package xyz.volgoak.wordlearning.recycler;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.databinding.WordCardHolderBinding;
import xyz.volgoak.wordlearning.entities.DataEntity;
import xyz.volgoak.wordlearning.entities.Word;
import xyz.volgoak.wordlearning.utils.WordSpeaker;

/**
 * Created by alex on 1/26/18.
 */

public class WordCardRowController extends RecyclerView.ViewHolder{

    private static Drawable[] sProgresIcons = new Drawable[5];
    private static boolean isDrawableInit;

    private WordCardHolderBinding binding;
    private CardsRecyclerAdapter adapter;

    private boolean settingsVisible;

    public WordCardRowController(WordCardHolderBinding binding, CardsRecyclerAdapter adapter) {
        super(binding.getRoot());
        this.binding = binding;
        this.adapter = adapter;
        if(!isDrawableInit) initDrawables(binding.getRoot().getContext());
    }

    public void bindController(DataEntity dataEntity) {
        settingsVisible = false;
        Word word = (Word) dataEntity;
        binding.tvCardTranslation.setText(word.getTranslation());
        binding.tvCardTranscription.setText(word.getTranscription());
        binding.tvCardWord.setText(word.getWord());

        binding.tvCardTranslation.setVisibility(View.INVISIBLE);

        binding.ivAddRemove.setImageResource(word.isInDictionary() ?
                R.drawable.ic_remove : R.drawable.ic_add_blue_50dp);

        binding.tvWordInDictionary.setText(word.isInDictionary() ?
            R.string.remove : R.string.add);

        Drawable wtProgress = word.getTrainedWt() >= sProgresIcons.length ?
                sProgresIcons[sProgresIcons.length - 1] : sProgresIcons[word.getTrainedWt()];
        binding.ivProgressWt.setImageDrawable(wtProgress);

        Drawable twProgress = word.getTrainedTw() >= sProgresIcons.length ?
                sProgresIcons[sProgresIcons.length - 1] : sProgresIcons[word.getTrainedTw()];
        binding.ivProgressTw.setImageDrawable(twProgress);

        binding.ivAddRemove.setOnClickListener(v -> adapter.addOrRemove(word));
        binding.ivReset.setOnClickListener(v -> adapter.resetProgress(word));

        binding.btShow.setOnClickListener(v -> binding.tvCardTranslation.setVisibility(View.VISIBLE));
        binding.btPronounce.setOnClickListener(v -> WordSpeaker.speakWord(word.getWord()));

        hideShowSettings();
        binding.ivShowSettings.setOnClickListener(v -> {
            settingsVisible = !settingsVisible;
            hideShowSettings();
        });
    }

    private void hideShowSettings() {
        binding.group.setVisibility(settingsVisible ? View.VISIBLE : View.GONE);
        binding.ivShowSettings.setImageResource(settingsVisible ? R.drawable.ic_arrow_up :
                R.drawable.ic_arrow_down);
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
