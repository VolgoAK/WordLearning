package xyz.volgoak.wordlearning.screens.set


import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.support.transition.Fade
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import com.attiladroid.data.DataContract
import com.attiladroid.data.entities.Word
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.adapter.WordsRecyclerAdapter
import xyz.volgoak.wordlearning.databinding.FragmentSingleSetBinding
import xyz.volgoak.wordlearning.extensions.observeSafe
import xyz.volgoak.wordlearning.extensions.onPreDraw
import xyz.volgoak.wordlearning.extensions.sinceLollipop
import xyz.volgoak.wordlearning.adapter.choice_mode.MultiChoiceMode
import xyz.volgoak.wordlearning.screens.set.viewModel.SingleSetViewModel
import xyz.volgoak.wordlearning.utils.Guide
import xyz.volgoak.wordlearning.utils.transitions.DetailsSupportTransition


/**
 * A simple [Fragment] subclass.
 */
class SingleSetFragment : Fragment() {

    private lateinit var mBinding: FragmentSingleSetBinding

    private var recyclerAdapter: WordsRecyclerAdapter? = null
    private var mActionMode: ActionMode? = null
    private var mWordsCallBack: WordsActionModeCallback? = null

    lateinit var viewModel: SingleSetViewModel

    private val setId by lazy { arguments?.getLong(EXTRA_SET_ID) ?: -1L }

    companion object {
        const val TAG = "SingleSetFragment"

        const val EXTRA_SET_ID = "set_id"
        const val SAVED_IS_MULTI_CHOICE = "is_multi_choice"
        const val EXTRA_TRANSITION_NAME = "extra_transition"

        fun newInstance(setId: Long, transitionName: String = ""): SingleSetFragment {
            val fragment = SingleSetFragment()
            val args = Bundle()
            args.putLong(EXTRA_SET_ID, setId)
            args.putString(EXTRA_TRANSITION_NAME, transitionName)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sinceLollipop {
            exitTransition = Fade()
            sharedElementEnterTransition = DetailsSupportTransition()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(activity!!, SingleSetViewModel.Factory(setId)).get(SingleSetViewModel::class.java)
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_single_set, container, false)

        if (recyclerAdapter == null) {
            initAdapter()
        }
        initRecycler()
        viewModel.wordsData.observeSafe(this) { onWords(it) }

        if (savedInstanceState != null) {
            val inMultiChoice = savedInstanceState.getBoolean(SAVED_IS_MULTI_CHOICE, false)
            if (inMultiChoice) {
                mWordsCallBack = WordsActionModeCallback()
                mWordsCallBack!!.onRestoreInstanceState(savedInstanceState)
            }
        }

        if (mWordsCallBack != null) {
            (activity as AppCompatActivity).startSupportActionMode(mWordsCallBack!!)
        }

        mBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                mBinding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
                Handler().postDelayed({
                    if (this@SingleSetFragment.isResumed) {
                        Guide.showGuide(this@SingleSetFragment, false)
                    }
                }, 500)
            }
        })

        sinceLollipop {
            postponeEnterTransition()
        }

        return mBinding.root
    }

    private fun initRecycler() {
        mBinding.rvWords.setHasFixedSize(true)
        mBinding.rvWords.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mBinding.rvWords.adapter = recyclerAdapter
    }

    private fun initAdapter() {
        recyclerAdapter = WordsRecyclerAdapter(context!!, mutableListOf(), mBinding.rvWords)

        if (mWordsCallBack != null) {
            recyclerAdapter!!.setChoiceMode(mWordsCallBack!!.choiceMode)
        }
        recyclerAdapter!!.onClick = { _, position, shared ->
            if (mWordsCallBack == null) {
                (activity as SetsActivity).showCards(position, shared)
            }
        }
        recyclerAdapter!!.onLongClick = { word, position ->
            if (mActionMode == null) {
                startActionMode(position)
            }
        }
    }

    private fun onWords(words: MutableList<Word>) {
        recyclerAdapter?.changeData(words)
        mBinding.root.onPreDraw {
            startPostponedEnterTransition()
        }
    }

    private fun startActionMode(position: Int) {
        viewModel.actionModeLD.value = true
        mWordsCallBack = WordsActionModeCallback()
        mWordsCallBack!!.choiceMode.setChecked(position, true)
        (activity as AppCompatActivity).startSupportActionMode(mWordsCallBack!!)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        mWordsCallBack?.let {
            outState.putBoolean(SAVED_IS_MULTI_CHOICE, true)
            it.onSaveInstanceState(outState)
        }
    }

    internal inner class WordsActionModeCallback : ActionMode.Callback {

        var choiceMode = MultiChoiceMode()

        fun onSaveInstanceState(instanceState: Bundle) {
            choiceMode.onSaveInstanceState(instanceState)
        }

        fun onRestoreInstanceState(savedInstanceState: Bundle) {
            choiceMode.restoreInstanceState(savedInstanceState)
        }

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_set_frag_action_mode, menu)
            recyclerAdapter?.setChoiceMode(choiceMode)
            mActionMode = mode
            choiceMode.checkedCountLD.observeSafe(this@SingleSetFragment) { count ->
                mode.title = count.toString()
            }

            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            val itemId = item.itemId
            when (itemId) {
                R.id.menu_add_setfrag_action -> {
                    viewModel.changeWordsStatus(choiceMode.checkedList, DataContract.Words.IN_DICTIONARY)
                    mActionMode?.finish()
                    return true
                }
                R.id.menu_remove_setfrag_action -> {
                    viewModel.changeWordsStatus(choiceMode.checkedList, DataContract.Words.OUT_OF_DICTIONARY)
                    mActionMode?.finish()
                    return true
                }
                R.id.menu_reset_setfrag_action -> {
                    viewModel.resetWordsProgress(choiceMode.checkedList)
                    mActionMode?.finish()
                    return true
                }
            }
            mActionMode?.finish()
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            viewModel.actionModeLD.value = false
            choiceMode.clearChecks()
            recyclerAdapter!!.setChoiceMode(null)
            mActionMode = null
            mWordsCallBack = null
        }
    }
}
