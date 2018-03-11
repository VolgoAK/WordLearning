package xyz.volgoak.wordlearning.fragment;


import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import xyz.volgoak.wordlearning.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContainerFragment extends Fragment {

    public static final String EXTRA_POSITION = "extra_position";

    public static ContainerFragment newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt(EXTRA_POSITION, position);

        ContainerFragment fragment = new ContainerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public ContainerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmen
        View root = inflater.inflate(R.layout.fragment_container, container, false);
        Toolbar toolbar = root.findViewById(R.id.toolbarContainer);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> getActivity().getSupportFragmentManager().popBackStack());


        int position = getArguments().getInt(EXTRA_POSITION);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.container, WordCardsFragment.newInstance(position))
                .commit();
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        }
    }
}
