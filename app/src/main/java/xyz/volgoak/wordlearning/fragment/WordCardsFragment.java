package xyz.volgoak.wordlearning.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.entities.Word;
import xyz.volgoak.wordlearning.model.DictionaryViewModel;
import xyz.volgoak.wordlearning.recycler.CardsRecyclerAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class WordCardsFragment extends Fragment {

    public static final String EXTRA_POSITION = "position";

    private DictionaryViewModel viewModel;
    private RecyclerView recyclerView;
    private CardsRecyclerAdapter adapter;

    private int startPosition;

    public static WordCardsFragment newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt(EXTRA_POSITION, position);

        WordCardsFragment fragment = new WordCardsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public WordCardsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_cards, container, false);

        recyclerView = root.findViewById(R.id.rv_cards);

        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(manager);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        startPosition = getArguments().getInt(EXTRA_POSITION);

        viewModel = ViewModelProviders.of(getActivity()).get(DictionaryViewModel.class);
        viewModel.getDictionaryWords().observe(this, this::onWordsReady);

        FloatingActionButton fabNext = root.findViewById(R.id.fabNext);
        fabNext.setOnClickListener(v -> {
            int position = manager.findFirstCompletelyVisibleItemPosition();
            recyclerView.smoothScrollToPosition(++position);
        });

        FloatingActionButton fabPrev = root.findViewById(R.id.fabPrev);
        fabPrev.setOnClickListener(v -> {
            int position = manager.findFirstCompletelyVisibleItemPosition();
            if(position > 0) recyclerView.smoothScrollToPosition(--position);
        });
        
        return root;
    }


    private void onWordsReady(List<Word> words) {
        if(adapter == null) {
            adapter = new CardsRecyclerAdapter();
            recyclerView.setAdapter(adapter);
        }

        int startPostition = words.size() * 1000 + startPosition;
        adapter.setDataList(words);
        recyclerView.scrollToPosition(startPostition);
    }
}
