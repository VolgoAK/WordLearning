package xyz.volgoak.wordlearning;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;
import xyz.volgoak.wordlearning.utils.SetsCursorAdapter;

import static android.R.id.list;


/**
 * A simple {@link Fragment} subclass.
 */
public class WordSetsFragment extends Fragment {

    private FragmentListener mFragmentListener;

    public WordSetsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mFragmentListener = (FragmentListener) getActivity();
        return inflater.inflate(R.layout.fragment_word_sets, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        int[] to = new int[]{android.R.id.text1};
        String[] from = new String[]{DatabaseContract.Sets.COLUMN_NAME};
        Cursor setsCursor = new WordsDbAdapter(getContext()).fetchSets();

        final SetsCursorAdapter adapter = new SetsCursorAdapter(getContext(), setsCursor);

        ListView list = (ListView) getView().findViewById(R.id.lv_setsfrag);
        list.setAdapter(adapter);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                long setId = adapter.getItemId(position);
                mFragmentListener.startSetFragment(setId);
                return true;
            }
        });

    }
}
