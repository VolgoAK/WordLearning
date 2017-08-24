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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import xyz.volgoak.wordlearning.data.WordsDbAdapter;
import xyz.volgoak.wordlearning.databinding.FragmentRedactorBinding;
import xyz.volgoak.wordlearning.recycler.CursorRecyclerAdapter;
import xyz.volgoak.wordlearning.recycler.WordsRecyclerAdapter;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

/**
 * A simple {@link Fragment} subclass.
 */
public class RedactorFragment extends Fragment{

    private WordsDbAdapter mDbAdapter;
    private WordsRecyclerAdapter mRecyclerAdapter;
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

        mDbAdapter = new WordsDbAdapter();
        Cursor cursor = mDbAdapter.fetchWordsByTrained(null, Integer.MAX_VALUE, Integer.MAX_VALUE, -1);

        mRecyclerAdapter = new WordsRecyclerAdapter(getContext(), cursor, mBinding.rvRedactor);
        mBinding.rvRedactor.setAdapter(mRecyclerAdapter);

        mRecyclerAdapter.setAdapterClickListener(new CursorRecyclerAdapter.AdapterClickListener() {
            @Override
            public void onClick(View root, int position, long id) {
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
                mDbAdapter.insertWord(word, translation);
                mBinding.etWordRedactor.setText("");
                mBinding.etTranslationRedactor.setText("");
                mRecyclerAdapter.changeCursor(mDbAdapter.fetchWordsByTrained(null, Integer.MAX_VALUE, Integer.MAX_VALUE, -1));
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        mRecyclerAdapter.closeCursor();
    }

    public void fireCustomDialog(final long id){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);

        TextView dialogTitle = (TextView) dialog.findViewById(R.id.dialog_title);
        dialogTitle.setText(R.string.what_to_do);

        Button toTrainingButton = (Button) dialog.findViewById(R.id.dialog_bt_one);
        toTrainingButton.setText(getString(R.string.send_to_training));
        toTrainingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDbAdapter.resetWordProgress(id);
                mRecyclerAdapter.changeCursor(mDbAdapter.fetchWordsByTrained(null, Integer.MAX_VALUE, Integer.MAX_VALUE, -1));
                dialog.dismiss();
            }
        });

        Button deleteButton = (Button) dialog.findViewById(R.id.dialog_bt_two);
        deleteButton.setText(getString(R.string.delete));
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDbAdapter.deleteOrHideWordById(id);
                mRecyclerAdapter.changeCursor(mDbAdapter.fetchWordsByTrained(null, Integer.MAX_VALUE, Integer.MAX_VALUE, -1));
                dialog.dismiss();
            }
        });

        dialog.show();
    }

}
