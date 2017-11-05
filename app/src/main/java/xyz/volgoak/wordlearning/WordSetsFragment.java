package xyz.volgoak.wordlearning;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;
import xyz.volgoak.wordlearning.recycler.CursorRecyclerAdapter;
import xyz.volgoak.wordlearning.recycler.SetsRecyclerAdapter;
import xyz.volgoak.wordlearning.recycler.SingleChoiceMode;
import xyz.volgoak.wordlearning.training_utils.TrainingFabric;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

/**
 * A simple {@link Fragment} subclass.
 */
public class WordSetsFragment extends Fragment implements SetsRecyclerAdapter.SetStatusChanger,
        CursorRecyclerAdapter.AdapterClickListener, CursorRecyclerAdapter.AdapterLongClickListener{

    public static final String TAG = "WordSetsFragment";
    public static final String EXTRA_PARTSCREEN_MODE = "extra_screen_mode";
    public static final String SAVED_POSITION = "saved_position";

    private FragmentListener mFragmentListener;
    private SetsFragmentListener mSetsFragmentListener;
    private WordsDbAdapter mDbAdapter;
    private SetsRecyclerAdapter mCursorAdapter;

    private RecyclerView mRecyclerView;

    private boolean mPartScreenMode = true;

    private int mRvSavedPosition;

     public static WordSetsFragment newInstance(boolean part_mode) {
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_PARTSCREEN_MODE, part_mode);

        WordSetsFragment fragment = new WordSetsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public WordSetsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
//        mFragmentListener = (FragmentListener) getActivity();
        return inflater.inflate(R.layout.fragment_word_sets, container, false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            mPartScreenMode = getArguments().getBoolean(EXTRA_PARTSCREEN_MODE, false);
        }

        if(savedInstanceState != null){
            mRvSavedPosition = savedInstanceState.getInt(SAVED_POSITION, 0);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

//        mFragmentListener.setActionBarTitle(getString(R.string.sets));

        mDbAdapter = new WordsDbAdapter();

        mRecyclerView = (RecyclerView) getView().findViewById(R.id.rv_setsfrag);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);

        Cursor setsCursor = mDbAdapter.fetchAllSets();
        mCursorAdapter = new SetsRecyclerAdapter(getContext(), setsCursor, mRecyclerView);
        mCursorAdapter.setAdapterClickListener(this);
        mCursorAdapter.setSetStatusChanger(this);

        if(mPartScreenMode) {
            mCursorAdapter.setChoiceMode(new SingleChoiceMode());
        }

        mRecyclerView.setAdapter(mCursorAdapter);

//
//        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                long setId = mCursorAdapter.getItemId(position);
//                mSetsFragmentListener.startSet(setId);
//                return true;
//            }
//        });
//
//        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                invokeSetMenu(id);
//            }
//        });

    }

    @Override
    public void onStop() {
        super.onStop();
        mCursorAdapter.closeCursor();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof SetsFragmentListener){
            Log.d(TAG, "onAttach: FragmentListener set");
            mSetsFragmentListener = (SetsFragmentListener) context;
        }else{
            throw new RuntimeException("Activity must implement WordsFragmentListener");
        }

        if(context instanceof FragmentListener){
            mFragmentListener = (FragmentListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach: ");
        mSetsFragmentListener = null;
        mFragmentListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mRecyclerView.getLayoutManager().scrollToPosition(mRvSavedPosition);
        mRvSavedPosition = 0;
    }

    @Override
    public void onPause() {
        super.onPause();
        mRvSavedPosition = ((LinearLayoutManager)mRecyclerView.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_POSITION, mRvSavedPosition);
    }

    public void invokeSetMenu(final long setId) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);

        Cursor cursor = mDbAdapter.fetchSetById(setId);
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndex(DatabaseContract.Sets.COLUMN_NAME));
        final int currentSetStatus = cursor.getInt(cursor.getColumnIndex(DatabaseContract.Sets.COLUMN_STATUS));
        cursor.close();

        TextView dialogTitle = (TextView) dialog.findViewById(R.id.dialog_title);
        dialogTitle.setText(name);

        Button openButton = (Button) dialog.findViewById(R.id.dialog_bt_one);
        openButton.setText(R.string.open);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SingleSetActivity.class);
                intent.putExtra(SingleSetActivity.ID_EXTRA, setId);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        Button addButton = (Button) dialog.findViewById(R.id.dialog_bt_two);
        int actionStringId = currentSetStatus == DatabaseContract.Sets.IN_DICTIONARY ?
                R.string.remove_from_dictionary :  R.string.add_to_dictionary;

        addButton.setText(actionStringId);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSetStatus(setId);
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
    public void changeSetStatus(long setId) {
//        Log.d(TAG, "changeSetStatus: ");
        Cursor cursor = mDbAdapter.fetchSetById(setId);
        if(!cursor.moveToFirst()){
//            Log.d(TAG, "changeSetStatus: incorrect id");
            cursor.close();
            return;
        }

        int currentStatus = cursor.getInt(cursor.getColumnIndex(DatabaseContract.Sets.COLUMN_STATUS));
        int newStatus = currentStatus == DatabaseContract.Sets.IN_DICTIONARY ?
            DatabaseContract.Sets.OUT_OF_DICTIONARY : DatabaseContract.Sets.IN_DICTIONARY;
        String setName = cursor.getString(cursor.getColumnIndex(DatabaseContract.Sets.COLUMN_NAME));
        cursor.close();

        String message ;
        message = newStatus == DatabaseContract.Sets.IN_DICTIONARY ? getString(R.string.set_added_message, setName) :
                getString(R.string.set_removed_message, setName);
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        mDbAdapter.changeSetStatus(setId, newStatus);

        mCursorAdapter.changeCursor(mDbAdapter.fetchAllSets());
    }

    @Override
    public void onClick(View root, int position, long id) {
        Log.d(TAG, "onClick: " + id);
        mSetsFragmentListener.startSet(id, root.findViewById(R.id.civ_sets));
    }

    @Override
    public boolean onLongClick(View root, int position, long id) {
        Log.d(TAG, "on item long click");
        invokeSetMenu(id);
        return true;
    }

    interface SetsFragmentListener{
        void startSet(long setId, View shared);
    }
}
