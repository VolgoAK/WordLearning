package xyz.volgoak.wordlearning;


import android.app.Dialog;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Toast;

import xyz.volgoak.wordlearning.data.WordsDbAdapter;
import xyz.volgoak.wordlearning.databinding.FragmentRedactorBinding;
import xyz.volgoak.wordlearning.utils.DictionaryRecyclerAdapter;

import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_TRAINED_TW;
import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_TRAINED_WT;


/**
 * A simple {@link Fragment} subclass.
 */
public class RedactorFragment extends Fragment{

    private WordsDbAdapter mDbAdapter;
    private DictionaryRecyclerAdapter mCursorAdapter;
    private FragmentListener mFragmentListener;

    private FragmentRedactorBinding mBinding;

    public RedactorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentListener = (FragmentListener) getActivity();
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_redactor, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onStart(){
        super.onStart();

        mFragmentListener.setActionBarTitle(getString(R.string.redactor));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mBinding.rvRedactor.setLayoutManager(layoutManager);

        mDbAdapter = new WordsDbAdapter(getContext());
        Cursor cursor = mDbAdapter.fetchWordsByTrained(null, Integer.MAX_VALUE, Integer.MAX_VALUE);

        mCursorAdapter = new DictionaryRecyclerAdapter(cursor, getContext());
        mBinding.rvRedactor.setAdapter(mCursorAdapter);

        mCursorAdapter.setOnWordLongClickListener(new DictionaryRecyclerAdapter.WordLongClickListener() {
            @Override
            public void onLongClick(long id) {
                fireCustomDialog(id);
            }
        });


        mBinding.btAddRedactor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = mBinding.etWordRedactor.getText().toString();
                String translation = mBinding.etTranslationRedactor.getText().toString();

                if(word.isEmpty() || translation.isEmpty()){
                    Toast.makeText(getContext(), getString(R.string.fields_empty_message), Toast.LENGTH_LONG).show();
                    return;
                }
                mDbAdapter.insertWord(word, translation, -1);
                mBinding.etWordRedactor.setText("");
                mBinding.etTranslationRedactor.setText("");
                mCursorAdapter.changeCursor(mDbAdapter.fetchWordsByTrained(null, Integer.MAX_VALUE, Integer.MAX_VALUE));
            }
        });
    }

    public void fireCustomDialog(final long id){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.redactor_dialog);
        Button toTrainingButton = (Button) dialog.findViewById(R.id.red_dialog_zero_button);
        toTrainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 10.06.2016 add method for change all trained collumns to zero at once
                mDbAdapter.changeTrainedStatus(id, WordsDbAdapter.TO_ZERO, COLUMN_TRAINED_WT );
                mDbAdapter.changeTrainedStatus(id, WordsDbAdapter.TO_ZERO, COLUMN_TRAINED_TW);
                mCursorAdapter.changeCursor(mDbAdapter.fetchWordsByTrained(null, Integer.MAX_VALUE, Integer.MAX_VALUE));
                dialog.dismiss();
            }
        });

        Button deleteButton = (Button) dialog.findViewById(R.id.red_dialog_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDbAdapter.deleteWordById(id);
                mCursorAdapter.changeCursor(mDbAdapter.fetchWordsByTrained(null, Integer.MAX_VALUE, Integer.MAX_VALUE));
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
