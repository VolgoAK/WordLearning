package xyz.volgoak.wordlearning.screens.set


import android.app.Dialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.transition.TransitionInflater
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.*
import com.attiladroid.data.DataContract
import com.attiladroid.data.entities.Set
import com.attiladroid.data.entities.Word
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.adapter.WordsRecyclerAdapter
import xyz.volgoak.wordlearning.data.StorageContract
import xyz.volgoak.wordlearning.databinding.FragmentSingleSetBinding
import xyz.volgoak.wordlearning.extensions.observeSafe
import xyz.volgoak.wordlearning.recycler.MultiChoiceMode
import xyz.volgoak.wordlearning.screens.set.viewModel.SingleSetViewModel
import xyz.volgoak.wordlearning.utils.Guide
import java.io.File


/**
 * A simple [Fragment] subclass.
 */
class SingleSetFragment : Fragment() {

    private lateinit var mBinding: FragmentSingleSetBinding

    private var recyclerAdapter: WordsRecyclerAdapter? = null
    private var mActionMode: ActionMode? = null
    private var mWordsCallBack: WordsActionModeCallback? = null
    private var mSetInDictionary: Boolean = false
    private var mSetName = ""

    lateinit var viewModel: SingleSetViewModel

    private val setId by lazy { arguments?.getLong(EXTRA_SET_ID) ?: -1L }

    companion object {
        const val TAG = "SingleSetFragment"

        const val EXTRA_SET_ID = "set_id"
        const val SAVED_IS_MULTI_CHOICE = "is_multi_choice"
        const val SAVED_CHOICE_MODE = "saved_choice_mode"
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

        if (savedInstanceState != null) {
            val inMultiChoice = savedInstanceState.getBoolean(SAVED_IS_MULTI_CHOICE, false)
            if (inMultiChoice) {
                mWordsCallBack = WordsActionModeCallback()
                mWordsCallBack!!.onRestoreInstanceState(savedInstanceState)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        viewModel = ViewModelProviders.of(activity!!, SingleSetViewModel.Factory(setId)).get(SingleSetViewModel::class.java)
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_single_set, container, false)
        val transitionName = arguments!!.getString(EXTRA_TRANSITION_NAME)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBinding.setIvTitle.transitionName = transitionName
        }

        manageTrainingStatusMenu()

        if (mWordsCallBack != null) {
            (activity as AppCompatActivity).startSupportActionMode(mWordsCallBack!!)
        }

        //Avoid bug with incorrect title position
        mBinding.appbarSetact.addOnOffsetChangedListener { appBarLayout, verticalOffset ->
            if (Math.abs(verticalOffset) >= appBarLayout.totalScrollRange - 35) {
                mBinding.collapsingToolbarSetAct.isTitleEnabled = false
                mBinding.setToolbar.title = mSetName
            } else {
                mBinding.collapsingToolbarSetAct.isTitleEnabled = true
                mBinding.setToolbar.title = ""
            }
        }

        if(recyclerAdapter == null) {
            initAdapter()
        }
        initRecycler()

        viewModel.wordsData
                .observeSafe(this) { onWords(it) }
        viewModel.setData.observe(this, Observer { set ->
            set?.let { loadSetInformation(it) }
        })

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

        return mBinding.root
    }

    private fun initRecycler() {
        mBinding.rvSetAc.setHasFixedSize(true)
        mBinding.rvSetAc.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mBinding.rvSetAc.adapter = recyclerAdapter
    }

    private fun initAdapter() {
        recyclerAdapter = WordsRecyclerAdapter(context!!, mutableListOf(), mBinding.rvSetAc)

        if (mWordsCallBack != null) {
            recyclerAdapter!!.setChoiceMode(mWordsCallBack!!.choiceMode)
        }
        recyclerAdapter!!.onClick = { _, position ->
            viewModel.startCardsLD.value = position
        }
        recyclerAdapter!!.onLongClick = { word, position ->
            if (mActionMode == null) {
                startActionMode(position)
            }
        }
    }

    private fun onWords(words: MutableList<Word>) {
        recyclerAdapter?.changeData(words)
    }

    private fun startActionMode(position: Int) {
        mBinding.appbarSetact.setExpanded(false, true)
        mBinding.setAddFab.hide()
        mBinding.setResetFab.hide()
        mBinding.setTrainingFab.hide()
        mWordsCallBack = WordsActionModeCallback()
        mWordsCallBack!!.choiceMode.setChecked(position, true)
        (activity as AppCompatActivity).startSupportActionMode(mWordsCallBack!!)
    }

    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).setSupportActionBar(mBinding!!.setToolbar)
        (activity as AppCompatActivity).supportActionBar!!.setHomeButtonEnabled(true)
        (activity as AppCompatActivity).supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mBinding.setToolbar.setNavigationIcon(R.drawable.ic_back_toolbar)
        mBinding.setToolbar.setNavigationOnClickListener { v -> activity!!.onBackPressed() }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity!!.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (mWordsCallBack != null) {
            outState.putBoolean(SAVED_IS_MULTI_CHOICE, true)
            mWordsCallBack!!.onSaveInstanceState(outState)
        }
    }

    private fun loadSetInformation(set: Set) {
        //set title
        mSetName = set.name
        mBinding.collapsingToolbarSetAct.title = mSetName

        //load title image
        val imageRes = set.imageUrl

        val imageDir = File(activity!!.filesDir, StorageContract.IMAGES_FOLDER)
        val imageFile = File(imageDir, imageRes)

        Picasso.get()
                .load(imageFile)
                .into(mBinding.setIvTitle, object : Callback {
                    override fun onSuccess() {
                        startPostponedEnterTransition()
                    }

                    override fun onError(e: Exception) {

                    }
                })

        mSetInDictionary = set.status == DataContract.Sets.IN_DICTIONARY

        prepareSetStatusFabs()

        mBinding.setAddFab.setOnClickListener { viewModel.changeCurrentSetStatus() }
        mBinding.setResetFab.setOnClickListener { resetSetProgress() }
        mBinding.setTrainingFab.setOnClickListener { showCoolDialog() }
    }

    private fun prepareSetStatusFabs() {
        mBinding.setResetFab.visibility = if (mSetInDictionary) View.VISIBLE else View.INVISIBLE
        val addDrawableId = if (mSetInDictionary) R.drawable.ic_remove_white_24dp else R.drawable.ic_add_white_24dp
        mBinding.setAddFab.setImageResource(addDrawableId)
    }

    private fun manageTrainingStatusMenu() {

    }

    private fun showCoolDialog() {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.start_training_dialog)

        val titleToolbar = dialog.findViewById<Toolbar>(R.id.dialog_toolbar)
        titleToolbar.setTitle(R.string.training)
        titleToolbar.setNavigationIcon(R.drawable.ic_training_24dp)

        val wtCard = dialog.findViewById<CardView>(R.id.cv_word_trans_dialog)
        wtCard.setOnClickListener {
            viewModel.navigationLD.value = SingleSetViewModel.TrainingNavigation.START_TRAINING_WT
            dialog.dismiss()
        }

        val twCard = dialog.findViewById<CardView>(R.id.cv_trans_word_dialog)
        twCard.setOnClickListener {
            viewModel.navigationLD.value = SingleSetViewModel.TrainingNavigation.START_TRAINING_TW
            dialog.dismiss()
        }

        val boolCard = dialog.findViewById<CardView>(R.id.cv_bool_dialog)
        boolCard.setOnClickListener {
            viewModel.navigationLD.value = SingleSetViewModel.TrainingNavigation.START_TRAINING_BOOL
            dialog.dismiss()
        }

        dialog.show()
    }

    fun onStatusChanged(status: Int) {
        val messageId = if (status == DataContract.Sets.IN_DICTIONARY)
            R.string.set_added_message
        else
            R.string.set_removed_message
        val message = getString(messageId, mSetName)
        Snackbar.make(view!!.findViewById(R.id.coordinator_setact), message, BaseTransientBottomBar.LENGTH_LONG).show()
    }

    private fun resetSetProgress() {
        Snackbar.make(view!!.findViewById(R.id.coordinator_setact),
                R.string.reset_progress_question,
                BaseTransientBottomBar.LENGTH_LONG)
                .setAction(R.string.reset) {
                    viewModel.resetCurrentSetProgress() }.show()
    }

    /*fun changeWordsStatus(positions: List<Int>, newStatus: Int) {
        if (positions.size == 0) return
        val time = System.currentTimeMillis()
        val wordsArray = arrayOfNulls<Word>(positions.size)
        for (a in positions.indices) {
            val word = mWords!![positions[a]]
            word.status = newStatus
            if (newStatus == DataContract.Words.IN_DICTIONARY) word.addedTime = time
            wordsArray[a] = word
        }

//        viewModel!!.updateWords(wordsArray)
    }

    fun resetWordsProgress(positions: List<Int>) {
        if (positions.size == 0) return
        val wordsArray = arrayOfNulls<Word>(positions.size)
        for (a in positions.indices) {
            val word = mWords!![positions[a]]
            word.resetProgress()
            wordsArray[a] = word
        }

//        viewModel!!.updateWords(wordsArray)
    }*/

    internal inner class WordsActionModeCallback : ActionMode.Callback {

        var choiceMode = MultiChoiceMode()
        //        TextView counter;


        fun onSaveInstanceState(instanceState: Bundle) {
            choiceMode.onSaveInstanceState(instanceState)
        }

        fun onRestoreInstanceState(savedInstanceState: Bundle) {
            choiceMode.restoreInstanceState(savedInstanceState)
        }

        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.menuInflater.inflate(R.menu.menu_set_frag_action_mode, menu)
            recyclerAdapter!!.setChoiceMode(choiceMode)
            mActionMode = mode
            //            counter = new TextView(getContext());
            mode.title = choiceMode.checkedCount.toString() + ""
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            mode.title = choiceMode.checkedCount.toString() + ""
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
            choiceMode.clearChecks()
            recyclerAdapter!!.setChoiceMode(null)
            mActionMode = null
            mWordsCallBack = null
            mBinding.appbarSetact.setExpanded(true, true)
            mBinding.setTrainingFab.show()
            mBinding.setResetFab.show()
            mBinding.setAddFab.show()
        }
    }
}
