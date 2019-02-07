package xyz.volgoak.wordlearning.screens.training

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.LinearLayout
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.admob.AdsManager
import xyz.volgoak.wordlearning.admob.Banner
import xyz.volgoak.wordlearning.extensions.observeSafe
import xyz.volgoak.wordlearning.screens.training.fragment.BoolTrainingFragment
import xyz.volgoak.wordlearning.screens.training.fragment.TimerFragment
import xyz.volgoak.wordlearning.screens.training.fragment.TrainingFragment
import xyz.volgoak.wordlearning.screens.training.helpers.Results
import xyz.volgoak.wordlearning.screens.training.helpers.TrainingFabric


class TrainingActivity : AppCompatActivity() {

    companion object {
        val EXTRA_TRAINING_TYPE = "training_type"
        val EXTRA_SET_ID = "set_id"

        fun getIntent(context: Context, trainingType: Int, setId: Long) =
                Intent(context, TrainingActivity::class.java).apply {
                    putExtra(EXTRA_TRAINING_TYPE, trainingType)
                    putExtra(EXTRA_SET_ID, setId)
                }
    }

    private val trainingType by lazy { intent.getIntExtra(EXTRA_TRAINING_TYPE, TrainingFabric.WORD_TRANSLATION) }
    private val setId by lazy { intent.getLongExtra(EXTRA_SET_ID, -1) }

    private var banner: Banner? = null

    private lateinit var viewModel: TrainingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_training)

        viewModel = ViewModelProviders
                .of(this, TrainingViewModel.Factory(setId, trainingType, application))
                .get(TrainingViewModel::class.java)
        viewModel.timerFinishedLd.observeSafe(this) {
            onTimerFinished()
        }
        viewModel.resultsLD.observeSafe(this) {
            showResults(it)
        }

        supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setHomeButtonEnabled(true)
        }

        if (savedInstanceState == null) {
            if (trainingType == TrainingFabric.BOOL_TRAINING) {
                val trainingFragment = TimerFragment()
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.container_training, trainingFragment)
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                transaction.commit()
            } else
                onTimerFinished()
        }

        if (AdsManager.initialized) {
            val bannerContainer = findViewById<LinearLayout>(R.id.llBannerContainerTraining)
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

    fun showResults(results: Results) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_TRAINING_RESULTS, results)
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun onTimerFinished() {
        val trainingFragment = if (trainingType == TrainingFabric.BOOL_TRAINING) {
            BoolTrainingFragment.newInstance(setId)
        } else {
            TrainingFragment()
        }

        supportFragmentManager.beginTransaction()
                .replace(R.id.container_training, trainingFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
    }
}
