package xyz.volgoak.wordlearning.screens.main.fragment


import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.databinding.FragmentTrainingSelectBinding
import xyz.volgoak.wordlearning.screens.main.viewModel.MainViewModel


/**
 * A simple [Fragment] subclass.
 */
class TrainingSelectFragment : Fragment() {

    private lateinit var dataBinding: FragmentTrainingSelectBinding

    private lateinit var viewModel: MainViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_training_select, container, false)
        return dataBinding.root
    }

    override fun onStart() {
        super.onStart()
        dataBinding.viewModel = viewModel
    }
}// Required empty public constructor
