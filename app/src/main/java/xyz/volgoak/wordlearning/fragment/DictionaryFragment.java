package xyz.volgoak.wordlearning.fragment;

import android.app.Dialog;
import android.arch.lifecycle.LiveData;
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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import xyz.volgoak.wordlearning.FragmentListener;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.WordsApp;
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.databinding.FragmentRedactorBinding;
import xyz.volgoak.wordlearning.entities.Word;
import xyz.volgoak.wordlearning.model.DictionaryViewModel;
import xyz.volgoak.wordlearning.recycler.WordsRecyclerAdapter;

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
    private DictionaryViewModel viewModel;

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
        mRecyclerAdapter.setAdapterClickListener((root, position, word) -> fireCustomDialog((Word) word));
        mBinding.rvRedactor.setAdapter(mRecyclerAdapter);

        viewModel = ViewModelProviders.of(this).get(DictionaryViewModel.class);
        viewModel.getDictionaryWords().observe(this, list -> mRecyclerAdapter.changeData(list));

        mBinding.fabAddRedactor.setOnClickListener((v) -> fireAddWordDialog());

        return mBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        mFragmentListener.setActionBarTitle(getString(R.string.redactor));




    }

    public void fireCustomDialog(final Word word) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);

        TextView dialogTitle = dialog.findViewById(R.id.dialog_title);
        dialogTitle.setText(R.string.what_to_do);

        Button toTrainingButton = dialog.findViewById(R.id.dialog_bt_one);
        toTrainingButton.setText(getString(R.string.send_to_training));
        toTrainingButton.setOnClickListener(v -> {
            viewModel.resetWordProgress(word);
            dialog.dismiss();
        });

        Button deleteButton = dialog.findViewById(R.id.dialog_bt_two);
        deleteButton.setText(getString(R.string.delete));
        deleteButton.setOnClickListener(v -> {
            viewModel.deleteOrHideWord(word);
            dialog.dismiss();
        });

        dialog.show();
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
                Word newWord = new Word(word, translation);
                newWord.setStatus(DatabaseContract.Words.IN_DICTIONARY);
                viewModel.insertWord(newWord);
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