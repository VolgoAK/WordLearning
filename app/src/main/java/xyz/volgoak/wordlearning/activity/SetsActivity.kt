package xyz.volgoak.wordlearning.activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import xyz.volgoak.wordlearning.FragmentListener
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.admob.AdsManager
import xyz.volgoak.wordlearning.admob.Banner
import xyz.volgoak.wordlearning.fragment.ContainerFragment
import xyz.volgoak.wordlearning.fragment.SingleSetFragment
import xyz.volgoak.wordlearning.screens.main.fragment.WordSetsFragment
import xyz.volgoak.wordlearning.model.WordsViewModel
import xyz.volgoak.wordlearning.screens.main.MainActivity

class SetsActivity : AppCompatActivity(), FragmentListener, WordSetsFragment.SetsFragmentListener {

    private var viewModel: WordsViewModel? = null

    private var isMultiFrag: Boolean = false
    private var mSelectedSetId: Long = -1

    private var banner: Banner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sets)

        isMultiFrag = findViewById<View>(R.id.container_detail_sets_activity) != null

        viewModel = ViewModelProviders.of(this).get(WordsViewModel::class.java)

        if (savedInstanceState != null)
            mSelectedSetId = savedInstanceState.getLong(SAVED_SET_ID, -1)

        if (mSelectedSetId != -1L && !viewModel!!.isSetLoaded) {
            viewModel!!.changeSet(mSelectedSetId)
        }

        // TODO: 3/11/18 cast exception here
        var fragment: Fragment? = supportFragmentManager.findFragmentById(R.id.container_master_sets_activity)
        if (fragment == null) {
            fragment = WordSetsFragment.newInstance(isMultiFrag)
            supportFragmentManager.beginTransaction()
                    .add(R.id.container_master_sets_activity, fragment)
                    .commit()
        }

        var detailFragment: Fragment? = supportFragmentManager.findFragmentById(R.id.container_detail_sets_activity)
        if (detailFragment == null && isMultiFrag) {
            // TODO: 17.06.2017 change test id to real
            detailFragment = SingleSetFragment.newInstance(1, false)
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container_detail_sets_activity, detailFragment)
                    .commit()
        }

        if (AdsManager.initialized) {
            val bannerContainer = findViewById<LinearLayout>(R.id.llBannerContainerSets)
            banner = Banner(this)
            banner!!.loadAdRequest()
            banner!!.setTargetView(bannerContainer)
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

    override fun startSet(setId: Long, shared: View) {
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
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putLong(SAVED_SET_ID, mSelectedSetId)
    }

    override fun startTraining(type: Int) {
        startTraining(type, mSelectedSetId)
    }

    override fun startTraining(type: Int, setId: Long) {
        val intent = Intent(this, TrainingActivity::class.java)
        intent.putExtra(TrainingActivity.EXTRA_SET_ID, setId)
        intent.putExtra(TrainingActivity.EXTRA_TRAINING_TYPE, type)
        startActivity(intent)
    }

    override fun startDictionary() {
       /* val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_MODE, MainActivity.START_DICTIONARY)
        startActivity(intent)*/
    }

    override fun startSets() {
        //do nothing sets already started
    }

    override fun startCards(startPosition: Int) {
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
       /* val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_MODE, MainActivity.SELECT_TRAINING)
        startActivity(intent)*/
    }

    companion object {

        val SAVED_SET_ID = "saved_set_id"
    }
}
