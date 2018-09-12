package xyz.volgoak.wordlearning.screens.training

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.widget.LinearLayout

import xyz.volgoak.wordlearning.FragmentListener
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.admob.AdsManager
import xyz.volgoak.wordlearning.admob.Banner

import xyz.volgoak.wordlearning.screens.set.SetsActivity
import xyz.volgoak.wordlearning.screens.training.fragment.NoWordsFragment
import xyz.volgoak.wordlearning.screens.training.fragment.ResultBoolFragment
import xyz.volgoak.wordlearning.screens.training.fragment.ResultsFragment
import xyz.volgoak.wordlearning.screens.training.helpers.Results
import xyz.volgoak.wordlearning.screens.training.helpers.TrainingFabric

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

class ResultActivity : AppCompatActivity(), FragmentListener {

    private var banner: Banner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val results = intent.getSerializableExtra(EXTRA_TRAINING_RESULTS) as Results
        val fragment: Fragment = when (results.resultType) {
            Results.ResultType.NO_WORDS -> NoWordsFragment()
            else -> when (results.trainedType) {
                TrainingFabric.BOOL_TRAINING -> ResultBoolFragment.newInstance(results)
                else -> ResultsFragment.getResultFragment(results)
            }
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.result_container, fragment)
        transaction.setTransition(FragmentTransaction.TRANSIT_NONE)
        transaction.commit()

        if(AdsManager.initialized) {
            val bannerContainer : LinearLayout = findViewById(R.id.llBannerContainerResults)
            banner = Banner(this)
            banner?.loadAdRequest()
            banner?.setTargetView(bannerContainer)
        }
    }

    override fun onResume() {
        super.onResume()
        banner?.onResume()
    }

    override fun onPause() {
        super.onPause()
        banner?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        banner?.onDestroy()
    }

    override fun startTraining(trainingType: Int, setId: Long) {
        val intent = Intent(this, TrainingActivity::class.java)
        intent.putExtra(TrainingActivity.EXTRA_TRAINING_TYPE, trainingType)
        intent.putExtra(TrainingActivity.EXTRA_SET_ID, setId)
        startActivity(intent)
        finish()
    }

    override fun startDictionary() {
        /*val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(MainActivity.EXTRA_MODE, MainActivity.START_DICTIONARY)
        startActivity(intent)

        finish()*/
    }

    override fun startTraining(type: Int) {
        startTraining(type, -1)
    }

    override fun startSets() {
        val intent = Intent(this, SetsActivity::class.java)
        startActivity(intent)

        finish()
    }

    override fun setActionBarTitle(title: String) {

    }

    override fun selectTraining() {

    }

    override fun startCards(startPosition: Int) {

    }

    companion object {

        val EXTRA_TRAINING_RESULTS = "results"
    }
}
