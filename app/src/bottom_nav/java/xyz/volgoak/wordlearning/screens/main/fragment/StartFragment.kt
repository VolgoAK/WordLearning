package xyz.volgoak.wordlearning.screens.main.fragment


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import xyz.volgoak.wordlearning.BR
import xyz.volgoak.wordlearning.FragmentListener
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.WordsApp
import xyz.volgoak.wordlearning.databinding.FragmentStartBinding
import xyz.volgoak.wordlearning.model.WordsViewModel
import xyz.volgoak.wordlearning.screens.main.viewModel.MainViewModel


/**
 * Created by Alexander Karachev on 07.05.2017.
 */

/**
 * A simple [Fragment] subclass.
 */
class StartFragment : Fragment() {

    private lateinit var mBinding: FragmentStartBinding

    lateinit var mainViewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        WordsApp.getsComponent().inject(this)
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_start, container, false)

        mainViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)

        mainViewModel.dictionaryInfoLiveData.observe(this, Observer { info ->
            mBinding.tvWordsDicStartF.text = getString(R.string.words_in_dictionary,
                    info!!.getWordsInDictionary())
            mBinding.tvWordsLearnedStartF.text = getString(R.string.words_learned,
                    info!!.learnedWords, info!!.allWords)
        })
        return mBinding.root
    }

    override fun onStart() {
        super.onStart()
        mainViewModel.setTitle(R.string.app_name)
    }
}
