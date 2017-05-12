package xyz.volgoak.wordlearning;


import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;
import xyz.volgoak.wordlearning.training_utils.TrainingFabric;
import xyz.volgoak.wordlearning.utils.SetsCursorAdapter;

import static android.R.attr.dialogTitle;


/**
 * A simple {@link Fragment} subclass.
 */
public class WordSetsFragment extends Fragment implements SetsCursorAdapter.SetStatusChanger {

    public static final String TAG = "WordSetsFragment";

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

        mFragmentListener.setActionBarTitle(getString(R.string.sets));

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
                Intent intent = new Intent(getActivity(), SetActivity.class);
                intent.putExtra(SetActivity.ID_EXTRA, setId);
                startActivity(intent);
                return true;
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                invokeSetMenu(id);
            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        mCursorAdapter.getCursor().close();
        mDbAdapter.close();
    }

    /*@Override
    public void changeSetStatus(long setId, int newStatus, String setName) {
        String message ;
        message = newStatus == DatabaseContract.Sets.IN_DICTIONARY ? getString(R.string.set_added_message, setName) :
                getString(R.string.set_removed_message, setName);
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        mDbAdapter.changeSetStatus(setId, newStatus);

        mCursorAdapter.swapCursor(mDbAdapter.fetchSets());
    }*/


    public void invokeSetMenu(final long setId) {

        Log.d(TAG, "invokeSetMenu id: " + setId);
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);

        Cursor cursor = mDbAdapter.fetchSetById(setId);
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndex(DatabaseContract.Sets.COLUMN_NAME));
        cursor.close();

        TextView dialogTitle = (TextView) dialog.findViewById(R.id.dialog_title);
        dialogTitle.setText(name);

        Button openButton = (Button) dialog.findViewById(R.id.dialog_bt_one);
        openButton.setText(R.string.open);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SetActivity.class);
                intent.putExtra(SetActivity.ID_EXTRA, setId);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        Button addButton = (Button) dialog.findViewById(R.id.dialog_bt_two);
        addButton.setText(R.string.add_set);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 05.05.2017 finish this method
                dialog.dismiss();
            }
        });


        Button wtButton = (Button) dialog.findViewById(R.id.dialog_bt_three);
        wtButton.setVisibility(View.VISIBLE);
        wtButton.setText(R.string.word_translation);
        wtButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentListener.startTraining(TrainingFabric.WORD_TRANSLATION, setId);
                dialog.dismiss();
            }
        });

        Button twButton = (Button) dialog.findViewById(R.id.dialog_bt_four);
        twButton.setVisibility(View.VISIBLE);
        twButton.setText(R.string.translation_word);
        twButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFragmentListener.startTraining(TrainingFabric.TRANSLATION_WORD, setId);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void changeSetStatus(long setId, int newStatus, String setName) {
        Log.d(TAG, "changeSetStatus: ");
        String message ;
        message = newStatus == DatabaseContract.Sets.IN_DICTIONARY ? getString(R.string.set_added_message, setName) :
                getString(R.string.set_removed_message, setName);
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        mDbAdapter.changeSetStatus(setId, newStatus);

        mCursorAdapter.swapCursor(mDbAdapter.fetchSets());
    }
}
