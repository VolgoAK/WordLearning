package xyz.volgoak.wordlearning.fragment;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.attiladroid.data.entities.Word;

import java.util.List;

import timber.log.Timber;
import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.data.DatabaseContract;
import xyz.volgoak.wordlearning.model.WordsViewModel;
import xyz.volgoak.wordlearning.recycler.CardsRecyclerAdapter;
import xyz.volgoak.wordlearning.utils.Guide;

/**
 * A simple {@link Fragment} subclass.
 */
public class WordCardsFragment extends Fragment {

    public static final String EXTRA_POSITION = "position";

    private WordsViewModel viewModel;
    private RecyclerView recyclerView;
    private CardsRecyclerAdapter adapter;

    private int startPosition;
    private boolean useStartPosition = true;

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
        if (savedInstanceState != null) {
            useStartPosition = false;
        }
        recyclerView.setLayoutManager(manager);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        adapter = new CardsRecyclerAdapter();

        adapter.setProgressResetListener(word -> {
            Timber.d("reset progress " + word.getWord());
            word.resetProgress();
            viewModel.updateWords(new Word[]{word});
        });

        adapter.setRemoveListener(word -> {
            int status = word.isInDictionary() ? DatabaseContract.Words.OUT_OF_DICTIONARY
                    : DatabaseContract.Words.IN_DICTIONARY;
            word.setStatus(status);
            viewModel.updateWords(new Word[]{word});
        });

        recyclerView.setAdapter(adapter);

        FloatingActionButton fabNext = root.findViewById(R.id.fabNext);
        fabNext.setOnClickListener(v -> {
            int position = manager.findFirstCompletelyVisibleItemPosition();
            recyclerView.smoothScrollToPosition(++position);
        });

        FloatingActionButton fabPrev = root.findViewById(R.id.fabPrev);
        fabPrev.setOnClickListener(v -> {
            int position = manager.findFirstCompletelyVisibleItemPosition();
            if (position > 0) recyclerView.smoothScrollToPosition(--position);
        });

        startPosition = getArguments().getInt(EXTRA_POSITION);

        viewModel = ViewModelProviders.of(getActivity()).get(WordsViewModel.class);
        viewModel.getWordsForSet().observe(this, this::onWordsReady);

        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                Guide.INSTANCE.showGuide(WordCardsFragment.this , false);
            }
        });

        return root;
    }

    private void onWordsReady(List<Word> words) {
        Timber.d("onWordsReady: ");
        if (useStartPosition) {
            int startPostition = words.size() * 1000 + startPosition;
            recyclerView.scrollToPosition(startPostition);
            useStartPosition = false;
        }

        adapter.setDataList(words);
    }
}
