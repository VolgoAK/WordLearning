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
import android.widget.Toast;

import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;
import xyz.volgoak.wordlearning.utils.SetsCursorAdapter;

import static android.R.id.list;


/**
 * A simple {@link Fragment} subclass.
 */
public class WordSetsFragment extends Fragment implements SetsCursorAdapter.SetStatusChanger{

    private FragmentListener mFragmentListener;
    private WordsDbAdapter mDbAdapter;
    private SetsCursorAdapter mCursorAdapter;

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
        mDbAdapter = new WordsDbAdapter(getContext());
        Cursor setsCursor = mDbAdapter.fetchSets();

        mCursorAdapter = new SetsCursorAdapter(getContext(), setsCursor);
        mCursorAdapter.setStatusChanger(this);

        ListView list = (ListView) getView().findViewById(R.id.lv_setsfrag);
        list.setAdapter(mCursorAdapter);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                long setId = mCursorAdapter.getItemId(position);
                mFragmentListener.startSetFragment(setId);
                return true;
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        mDbAdapter.close();
        mCursorAdapter.getCursor().close();
    }

    @Override
    public void changeSetStatus(long setId, int newStatus, String setName) {
        String message = getString(R.string.set_added_message, setName);
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        mDbAdapter.changeSetStatus(setId, newStatus);
    }


}
