package xyz.volgoak.wordlearning.screens.set

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.attiladroid.data.DataContract
import com.attiladroid.data.entities.Set
import com.attiladroid.data.entities.Word
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_sets.*
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.R.id.*
import xyz.volgoak.wordlearning.adapter.WordsRecyclerAdapter
import xyz.volgoak.wordlearning.admob.AdsManager
import xyz.volgoak.wordlearning.admob.Banner
import xyz.volgoak.wordlearning.data.StorageContract
import xyz.volgoak.wordlearning.extensions.setVisibility
import xyz.volgoak.wordlearning.extensions.sinceLollipop
import xyz.volgoak.wordlearning.screens.set.viewModel.WordsViewModel
import java.io.File

class SetsActivity : AppCompatActivity() {

    lateinit var viewModel: WordsViewModel

    private var banner: Banner? = null

    private val setId by lazy { intent.getLongExtra(EXTRA_SET_ID, 0L) }
    private var setInDictionary = false

    private var rvAdapter: WordsRecyclerAdapter? = null

    companion object {
        private val EXTRA_SET_ID = "saved_set_id"

        fun getIntent(context: Context, setId: Long): Intent {
            return Intent(context, SetsActivity::class.java).apply {
                putExtra(EXTRA_SET_ID, setId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sets)

        manageActionBar()
        viewModel = ViewModelProviders.of(this, WordsViewModel.Factory(setId))
                .get(WordsViewModel::class.java)
        initSubscriptions()

        if (AdsManager.initialized) {
            banner = Banner(this)
            banner!!.loadAdRequest()
            banner!!.setTargetView(llBannerContainerSets)
        }
    }

    private fun manageActionBar() {
        setSupportActionBar(toolbarSet)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    private fun initSubscriptions() {
        viewModel.setData.observe(this, Observer { set ->
            set?.let { onWordsSet(it) }
        })
        viewModel.wordsData.observe(this, Observer { words ->
            words?.let { onWords(it)}
        })
    }

    @SuppressLint("NewApi")
    private fun onWordsSet(set: Set) {
        //set title
        ctSetAct.title = set.name

        //load title image
        val imageRes = set.imageUrl

        val imageDir = File(getFilesDir(), StorageContract.IMAGES_FOLDER)
        val imageFile = File(imageDir, imageRes)

        sinceLollipop { ivSetTitle.transitionName = set.name }

        Picasso.get()
                .load(imageFile)
                .into(ivSetTitle, object : Callback {
                    override fun onSuccess() {
                        sinceLollipop { startPostponedEnterTransition() }
                    }

                    override fun onError(e: Exception) {

                    }
                })


        val setStatus = set.status
        setInDictionary = setStatus == DataContract.Sets.IN_DICTIONARY

        prepareSetStatusFabs()

        fabAddSet.setOnClickListener { viewModel.changeCurrentSetStatus() }
        fabResetSet.setOnClickListener { viewModel.resetCurrentSetProgress() }
        fabTrainingSet.setOnClickListener { showCoolDialog() }
    }

    private fun showCoolDialog() {

    }

    private fun prepareSetStatusFabs() {
        fabResetSet.setVisibility(setInDictionary)
        val addDrawableId = if (setInDictionary) R.drawable.ic_remove_white_24dp else R.drawable.ic_add_white_24dp
        fabAddSet.setImageResource(addDrawableId)
    }

    private fun onWords(wordList: MutableList<Word>) {
        if(rvAdapter == null) {
            rvAdapter = WordsRecyclerAdapter(this, wordList, rvSet)
            rvSet.layoutManager = LinearLayoutManager(this)
            rvSet.adapter = rvAdapter
        } else {
            rvAdapter!!.changeData(wordList)
        }
    }

    override fun onResume() {
        super.onResume()
        if (banner != null) {
            banner!!.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (banner != null) {
            banner!!.onPause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (banner != null) {
            banner!!.onDestroy()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /*override fun startSet(setId: Long, shared: View) {
        val transaction = supportFragmentManager.beginTransaction()
        val singleMode = findViewById<View>(R.id.container_detail_sets_activity) == null
        val container = if (singleMode) R.id.container_master_sets_activity else R.id.container_detail_sets_activity

        val setFragment = SingleSetFragment.newInstance(setId, singleMode)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val transitionName = ViewCompat.getTransitionName(shared)
            setFragment.arguments!!.putString(SingleSetFragment.EXTRA_TRANSITION_NAME, transitionName)
            transaction.addSharedElement(shared, transitionName)
        }

        transaction.replace(container, setFragment)
                .addToBackStack(null)
                .commit()

        mSelectedSetId = setId
    }*/

    /*override fun startTraining(type: Int) {
        startTraining(type, mSelectedSetId)
    }

    override fun startTraining(type: Int, setId: Long) {
        val intent = Intent(this, TrainingActivity::class.java)
        intent.putExtra(TrainingActivity.EXTRA_SET_ID, setId)
        intent.putExtra(TrainingActivity.EXTRA_TRAINING_TYPE, type)
        startActivity(intent)
    }

    override fun startDictionary() {
       *//* val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_MODE, MainActivity.START_DICTIONARY)
        startActivity(intent)*//*
    }

    override fun startSets() {
        //do nothing sets already started
    }*/

    /*override fun startCards(startPosition: Int) {
        val singleMode = findViewById<View>(R.id.container_detail_sets_activity) == null
        val container = if (singleMode) R.id.container_master_sets_activity else R.id.container_detail_sets_activity
        val fragment = ContainerFragment.newInstance(startPosition)
        supportFragmentManager.beginTransaction()
                .replace(container, fragment)
                .addToBackStack(null)
                .commit()
    }

    override fun setActionBarTitle(title: String) {
        //do nothing. Just part of interface
    }

    override fun selectTraining() {
       *//* val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_MODE, MainActivity.SELECT_TRAINING)
        startActivity(intent)*//*
    }*/
}
