package xyz.volgoak.wordlearning;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import static xyz.volgoak.wordlearning.WordsSqlHelper.*;


/**
 * A simple {@link Fragment} subclass.
 */
public class RedactorFragment extends Fragment {


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
        WordsDbAdapter dbAdapter = new WordsDbAdapter(getContext());
        Cursor cursor = dbAdapter.fetchAllWords();
        ListView listView = (ListView) getView().findViewById(R.id.redactor_list_view);
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getContext(), R.layout.redactor_cursor_adapter, cursor,
                new String[]{COLUMN_WORD, COLUMN_TRANSLATION}, new int[]{R.id.adapter_text_1, R.id.adapter_text_2}, 0);
        listView.setAdapter(adapter);
    }

}
