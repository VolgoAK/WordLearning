package xyz.volgoak.wordlearning.screens.set


import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.transition.TransitionInflater
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PagerSnapHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver

import com.attiladroid.data.DataContract
import com.attiladroid.data.entities.Word

import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.databinding.FragmentCardsBinding
import xyz.volgoak.wordlearning.extensions.observeSafe
import xyz.volgoak.wordlearning.extensions.sinceLollipop
import xyz.volgoak.wordlearning.screens.set.viewModel.SingleSetViewModel
import xyz.volgoak.wordlearning.screens.set.adapter.CardsRecyclerAdapter
import xyz.volgoak.wordlearning.utils.Guide


/**
 * A simple [Fragment] subclass.
 */
class WordCardsFragment : Fragment() {

    private lateinit var viewModel: SingleSetViewModel
    private lateinit var binding: FragmentCardsBinding
    private lateinit var adapter: CardsRecyclerAdapter

    private val startPosition: Int by lazy {
        arguments?.getInt(EXTRA_POSITION , 0) ?: 0
    }
    private var useStartPosition = true

    companion object {
        val EXTRA_POSITION = "position"

        fun newInstance(position: Int): WordCardsFragment {
            val args = Bundle()
            args.putInt(EXTRA_POSITION, position)

            val fragment = WordCardsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sinceLollipop {
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
//            enterTransition = Fade()
//            exitTransition = Fade()
            postponeEnterTransition()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cards, container, false)
        initAdapter()

        viewModel = ViewModelProviders.of(activity!!).get(SingleSetViewModel::class.java)
        viewModel.wordsData.observeSafe(this) { words -> onWordsReady(words)}

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                Guide.showGuide(this@WordCardsFragment, false)
            }
        })
        return binding.root
    }

    private fun initAdapter() {
        binding.rvCards.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvCards)

        adapter = CardsRecyclerAdapter()

        adapter.progressResetListener =  { word ->
            word.resetProgress()
            viewModel.updateWords(arrayOf(word))
        }

        adapter.removeListener = { word ->
            val status = if (word.isInDictionary)
                DataContract.Words.OUT_OF_DICTIONARY
            else
                DataContract.Words.IN_DICTIONARY
            word.status = status
            viewModel.updateWords(arrayOf(word))
        }

        binding.rvCards.adapter = adapter

        binding.fabNext.setOnClickListener { v ->
            var position = (binding.rvCards.layoutManager as LinearLayoutManager)
                    .findFirstCompletelyVisibleItemPosition()
            binding.rvCards.smoothScrollToPosition(++position)
        }

        binding.fabPrev.setOnClickListener { v ->
            var position = (binding.rvCards.layoutManager as LinearLayoutManager)
                    .findFirstCompletelyVisibleItemPosition()
            if (position > 0) binding.rvCards.smoothScrollToPosition(--position)
        }
    }

    private fun onWordsReady(words: List<Word>) {
        if (useStartPosition) {
            val startPostition = words.size * 1000 + startPosition
            binding.rvCards.scrollToPosition(startPostition)
            useStartPosition = false
        }

        adapter.setDataList(words)

        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                startPostponedEnterTransition()
            }
        })
    }
}
