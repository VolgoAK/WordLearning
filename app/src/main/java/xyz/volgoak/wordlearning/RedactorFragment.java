package xyz.volgoak.wordlearning;


import android.app.Dialog;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import xyz.volgoak.wordlearning.data.WordsDbAdapter;
import xyz.volgoak.wordlearning.utils.DictionaryCursorAdapter;

import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_TRAINED_TW;
import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_TRAINED_WT;
import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_TRANSLATION;
import static xyz.volgoak.wordlearning.data.DatabaseContract.Words.COLUMN_WORD;


/**
 * A simple {@link Fragment} subclass.
 */
public class RedactorFragment extends Fragment{

    private WordsDbAdapter dbAdapter;
    private SimpleCursorAdapter cursorAdapter;

    public RedactorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_redactor, container, false);
    }

    @Override
    public void onStart(){
        super.onStart();
        dbAdapter = new WordsDbAdapter(getContext());
        Cursor cursor = dbAdapter.fetchDictionaryWords();
        ListView listView = (ListView) getView().findViewById(R.id.redactor_list_view);
        cursorAdapter = new DictionaryCursorAdapter(cursor, getContext());
        listView.setAdapter(cursorAdapter);

        final EditText wordEdit = (EditText) getView().findViewById(R.id.redactor_edit_word);
        final EditText translationEdit = (EditText) getView().findViewById(R.id.redactor_edit_translation);
        Button addButton = (Button) getView().findViewById(R.id.redactor_button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbAdapter.insertWord(wordEdit.getText().toString(), translationEdit.getText().toString(), -1);
                wordEdit.setText("");
                translationEdit.setText("");
                cursorAdapter.changeCursor(dbAdapter.fetchAllWords());
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                fireCustomDialog((int) id);
                return true;
            }
        });
    }

    public void fireCustomDialog(final int id){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.redactor_dialog);
        Button toTrainingButton = (Button) dialog.findViewById(R.id.red_dialog_zero_button);
        toTrainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 10.06.2016 add method for change all trained collumns to zero at once
                dbAdapter.changeTrainedStatus(id, WordsDbAdapter.TO_ZERO, COLUMN_TRAINED_WT );
                dbAdapter.changeTrainedStatus(id, WordsDbAdapter.TO_ZERO, COLUMN_TRAINED_TW);
                cursorAdapter.changeCursor(dbAdapter.fetchWordsByTrained());
                dialog.dismiss();
            }
        });

        Button deleteButton = (Button) dialog.findViewById(R.id.red_dialog_delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbAdapter.deleteWordById(id);
                cursorAdapter.changeCursor(dbAdapter.fetchWordsByTrained());
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
