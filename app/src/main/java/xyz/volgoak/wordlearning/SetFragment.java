package xyz.volgoak.wordlearning;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import xyz.volgoak.wordlearning.data.WordsDbAdapter;
import xyz.volgoak.wordlearning.utils.DictionaryCursorAdapter;


/**
 * A simple {@link Fragment} subclass.
 */
public class SetFragment extends Fragment {

    private WordsDbAdapter mDbAdapter;
    private long mSetId;
    private FragmentListener mListener;

    public static SetFragment newInstance(long setId){
        SetFragment fragment = new SetFragment();
        fragment.mSetId = setId;
        return fragment;
    }

    public SetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mListener = (FragmentListener) getActivity();
        return inflater.inflate(R.layout.fragment_set, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        mListener.setActionBarTitle(getString(R.string.set));

        mDbAdapter = new WordsDbAdapter(getContext());
        Cursor cursor = mDbAdapter.fetchWordsBySetId(mSetId);

        final DictionaryCursorAdapter cursorAdapter = new DictionaryCursorAdapter(cursor, getContext());
        ListView listView = (ListView) getView().findViewById(R.id.lv_words_setfrag);
        listView.setAdapter(cursorAdapter);

        Button resetButton = (Button) getView().findViewById(R.id.bt_reset_setfrag);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDbAdapter.resetSetStatus(mSetId);
                Cursor newCursor = mDbAdapter.fetchWordsBySetId(mSetId);
                cursorAdapter.changeCursor(newCursor);
            }
        });
    }
}
