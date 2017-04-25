package xyz.volgoak.wordlearning;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xyz.volgoak.wordlearning.databinding.FragmentStartBinding;


/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends Fragment {

    private FragmentListener mListener;
    private FragmentStartBinding mBinding;

    public StartFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_start, container, false);
        return mBinding.getRoot();
    }

    @Override
    public void onStart(){
        super.onStart();
        mBinding.setListener(mListener);
        mBinding.notifyPropertyChanged(BR._all);
        mListener.setActionBarTitle(getString(R.string.app_name));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof FragmentListener){
            mListener = (FragmentListener) context;
        }else throw new RuntimeException(context.toString() + " must implement FragmentListener");
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mListener = null;
    }
}
