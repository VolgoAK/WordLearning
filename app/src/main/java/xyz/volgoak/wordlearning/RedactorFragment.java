package xyz.volgoak.wordlearning;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import static xyz.volgoak.wordlearning.WordsSqlHelper.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class RedactorFragment extends Fragment {

    WordsDbAdapter dbAdapter;
    SimpleCursorAdapter cursorAdapter;

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
        Cursor cursor = dbAdapter.fetchWardsByTrained(WordsSqlHelper.COLUMN_TRAINED_WT);
        ListView listView = (ListView) getView().findViewById(R.id.redactor_list_view);
        cursorAdapter = new SimpleCursorAdapter(getContext(), R.layout.redactor_cursor_adapter, cursor,
                new String[]{COLUMN_WORD, COLUMN_TRANSLATION, COLUMN_TRAINED_WT},
                new int[]{R.id.adapter_text_1, R.id.adapter_text_2, R.id.adapter_text_3}, 0);
        listView.setAdapter(cursorAdapter);

        final EditText wordEdit = (EditText) getView().findViewById(R.id.redactor_edit_word);
        final EditText translationEdit = (EditText) getView().findViewById(R.id.redactor_edit_translation);
        Button addButton = (Button) getView().findViewById(R.id.redactor_button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbAdapter.insertWord(wordEdit.getText().toString(), translationEdit.getText().toString());
                wordEdit.setText("");
                translationEdit.setText("");
                cursorAdapter.changeCursor(dbAdapter.fetchAllWords());
            }
        });

        dbAdapter.changeTrainedStatus(2, WordsDbAdapter.INCREASE, WordsSqlHelper.COLUMN_TRAINED_WT);
        dbAdapter.changeTrainedStatus(2, WordsDbAdapter.INCREASE, WordsSqlHelper.COLUMN_TRAINED_WT);
        dbAdapter.changeTrainedStatus(2, WordsDbAdapter.INCREASE, WordsSqlHelper.COLUMN_TRAINED_WT);
        dbAdapter.changeTrainedStatus(2, WordsDbAdapter.INCREASE, WordsSqlHelper.COLUMN_TRAINED_WT);
    }

}
