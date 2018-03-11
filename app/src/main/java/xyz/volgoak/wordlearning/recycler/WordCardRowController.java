package xyz.volgoak.wordlearning.recycler;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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

    private WordCardHolderBinding binding;

    public WordCardRowController(WordCardHolderBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bindController(DataEntity dataEntity) {
        Word word = (Word) dataEntity;
        binding.tvCardTranslation.setText(word.getTranslation());
        binding.tvCardTranscription.setText(word.getTranscription());
        binding.tvCardWord.setText(word.getWord());

        binding.tvCardTranslation.setVisibility(View.INVISIBLE);
        binding.btShow.setOnClickListener(v -> binding.tvCardTranslation.setVisibility(View.VISIBLE));

        binding.btPronounce.setOnClickListener(v -> WordSpeaker.speakWord(word.getWord()));
    }
}
