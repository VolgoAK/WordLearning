package xyz.volgoak.wordlearning.screens.training.fragment

import android.content.Context
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.attiladroid.data.DataProvider
import com.attiladroid.data.entities.Word

import javax.inject.Inject

import io.fabric.sdk.android.services.concurrency.AsyncTask
import xyz.volgoak.wordlearning.FragmentListener
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.WordsApp
import xyz.volgoak.wordlearning.databinding.FragmentResultsBinding

import xyz.volgoak.wordlearning.screens.training.helpers.Results
import xyz.volgoak.wordlearning.screens.training.helpers.TrainingFabric

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

class ResultsFragment : Fragment() {

    private var results: Results? = null
    private var fragmentListener: FragmentListener? = null
    @Inject
    lateinit var mProvider: DataProvider

    private var mDataBinding: FragmentResultsBinding? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        WordsApp.getsComponent().inject(this)
        mDataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_results, container, false)
        return mDataBinding!!.root
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is FragmentListener) {
            fragmentListener = context
        } else
            throw RuntimeException(context!!.toString() + " must implement FragmentListener")
    }

    override fun onDetach() {
        super.onDetach()
        fragmentListener = null
    }

    override fun onStart() {
        super.onStart()

        manageFeedBack()

        mDataBinding!!.cvDictionaryResult.setOnClickListener { v -> fragmentListener!!.startDictionary() }
        mDataBinding!!.cvSetsResult.setOnClickListener { v -> fragmentListener!!.startSets() }
        mDataBinding!!.cvAgainResult.setOnClickListener { v -> fragmentListener!!.startTraining(results!!.trainedType, results!!.setId) }

        AsyncTask.execute { this.updateWordsStatus() }
    }

    private fun manageFeedBack() {
        val results = getString(R.string.correct_answers) + "  " + this.results!!.correctAnswers + "/" + this.results!!.wordCount
        mDataBinding!!.tvResultResfrag.text = results

        val opinion: String
        val percentage = this.results!!.correctAnswers * 1.0 / this.results!!.wordCount

        if (percentage == 1.0) {
            opinion = getString(R.string.perfect_result)
        } else if (percentage >= 0.8) {
            opinion = getString(R.string.good_result)
        } else if (percentage >= 0.4) {
            opinion = getString(R.string.bad_result)
        } else
            opinion = getString(R.string.disaster_result)

        mDataBinding!!.tvResultOpinion.text = opinion
    }

    private fun updateWordsStatus() {
        if (results!!.idsForUpdate.size == 0) return

        val trainedTime = System.currentTimeMillis()

        val updater: (Word) -> Unit
        if (results!!.trainedType == TrainingFabric.TRANSLATION_WORD) {
            updater = { w ->
                w.trainedTw = w.trainedTw + 1
                w.lastTrained = trainedTime
            }
        } else if (results!!.trainedType == TrainingFabric.WORD_TRANSLATION) {
            updater = { w ->
                w.trainedWt = w.trainedWt + 1
                w.lastTrained = trainedTime
            }
        } else
            return

        val words = mutableListOf<Word>()
        for (i in results!!.idsForUpdate.indices) {
            val word = mProvider!!.getWordById(results!!.idsForUpdate[i])
            updater(word)
            words.add(word)
        }

        mProvider!!.updateWords(words)
    }

    companion object {

        val TAG = "ResultFragment"

        fun getResultFragment(results: Results): ResultsFragment {
            val fragment = ResultsFragment()
            fragment.results = results
            return fragment
        }
    }
}
