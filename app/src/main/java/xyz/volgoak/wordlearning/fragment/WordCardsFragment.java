package xyz.volgoak.wordlearning.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.WordsApp;
import xyz.volgoak.wordlearning.data.DataProvider;
import xyz.volgoak.wordlearning.entities.Word;
import xyz.volgoak.wordlearning.recycler.CardsRecyclerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class WordCardsFragment extends Fragment {

    @Inject
    DataProvider dataProvider;


    public WordCardsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        WordsApp.getsComponent().inject(this);
        View root = inflater.inflate(R.layout.fragment_blank, container, false);
        RecyclerView rv = root.findViewById(R.id.rv_cards);
        List<Word> wordList = dataProvider.getDictionaryWords();
        CardsRecyclerAdapter adapter = new CardsRecyclerAdapter(getContext(), wordList, rv);
        rv.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv.setLayoutManager(manager);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rv);
        
        return root;
    }

}
