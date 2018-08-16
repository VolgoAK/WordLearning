package xyz.volgoak.wordlearning.fragment;

import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.annimon.stream.Stream;
import com.attiladroid.data.entities.Word;

import java.util.ArrayList;

import xyz.volgoak.wordlearning.FragmentListener;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.WordsApp;
import xyz.volgoak.wordlearning.databinding.FragmentRedactorBinding;
import xyz.volgoak.wordlearning.screens.set.viewModel.WordsViewModel;
import xyz.volgoak.wordlearning.adapter.WordsRecyclerAdapter;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

/**
 * A simple {@link Fragment} subclass.
 */
public class DictionaryFragment extends Fragment {

    public static final String TAG = DictionaryFragment.class.getSimpleName();

    private WordsRecyclerAdapter mRecyclerAdapter;
    private FragmentListener mFragmentListener;

    private FragmentRedactorBinding mBinding;
    private WordsViewModel viewModel;

    public DictionaryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        WordsApp.getsComponent().inject(this);
        mFragmentListener = (FragmentListener) getActivity();
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_redactor, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvRedactor.setLayoutManager(layoutManager);

        mRecyclerAdapter = new WordsRecyclerAdapter(getContext(), new ArrayList<>(), mBinding.rvRedactor);
//        mRecyclerAdapter.setAdapterClickListener((root, position, word) -> onWordClicked(position, (Word) word));
        mBinding.rvRedactor.setAdapter(mRecyclerAdapter);

        viewModel = ViewModelProviders.of(getActivity()).get(WordsViewModel.class);
        viewModel.changeToDictionary();
        /*viewModel.getWordsForSet().observe(this, list -> {
            mRecyclerAdapter.changeData(Stream.of(list)
                .sorted((w1, w2) -> Long.compare(w2.getAddedTime(), w1.getAddedTime()))
                .toList());
        });*/

        mBinding.fabAddRedactor.setOnClickListener((v) -> fireAddWordDialog());

        return mBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        mFragmentListener.setActionBarTitle(getString(R.string.redactor));
    }

    private void onWordClicked(int position, Word word) {
        mFragmentListener.startCards(position);
    }

    public void fireAddWordDialog() {
        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_word);

        EditText wordEt = dialog.findViewById(R.id.et_word_redactor_dialog);
        EditText translationEt = dialog.findViewById(R.id.et_translation_redactor_dialog);

        Button addButton = dialog.findViewById(R.id.bt_add_redactor_dialog);
        addButton.setOnClickListener((v) -> {
            String word = wordEt.getText().toString();
            String translation = translationEt.getText().toString();
            if (!word.isEmpty() && !translation.isEmpty()) {
                //todo manage
                /*Word newWord = new Word(word, translation);
                newWord.setStatus(DataContract.Words.IN_DICTIONARY);
                newWord.setAddedTime(System.currentTimeMillis());
                viewModel.insertWord(newWord);*/
            } else {
                Toast.makeText(getContext(), R.string.fields_empty_message, Toast.LENGTH_LONG).show();
            }
            dialog.dismiss();
        });

        Button cancelButton = dialog.findViewById(R.id.bt_cancel_redactor_dialog);
        cancelButton.setOnClickListener((v) -> dialog.dismiss());

        Toolbar toolbar = dialog.findViewById(R.id.dialog_add_word_toolbar);
        toolbar.setTitle(R.string.new_word);

        dialog.show();
    }

}
