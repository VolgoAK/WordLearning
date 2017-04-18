package xyz.volgoak.wordlearning;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class WordSetsFragment extends Fragment {


    public WordSetsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_word_sets, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        int[] to = new int[]{android.R.id.text1};
        String[] from = new String[]{DatabaseContract.Sets.COLUMN_NAME};
        Cursor setsCursor = new WordsDbAdapter(getContext()).fetchSets();
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getContext(), android.R.layout.simple_expandable_list_item_1,
            setsCursor, from, to, 0);

        ListView list = (ListView) getView().findViewById(R.id.lv_setsfrag);
        list.setAdapter(adapter);

    }
}
