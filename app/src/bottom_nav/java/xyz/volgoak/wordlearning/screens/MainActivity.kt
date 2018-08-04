package xyz.volgoak.wordlearning.screens

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v4.view.GravityCompat
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import xyz.volgoak.wordlearning.AppRater
import xyz.volgoak.wordlearning.FragmentListener
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.activity.NavigationActivity
import xyz.volgoak.wordlearning.activity.SetsActivity
import xyz.volgoak.wordlearning.activity.SettingsActivity
import xyz.volgoak.wordlearning.activity.TrainingActivity
import xyz.volgoak.wordlearning.admob.AdsManager
import xyz.volgoak.wordlearning.admob.Banner
import xyz.volgoak.wordlearning.fragment.DictionaryFragment
import xyz.volgoak.wordlearning.fragment.StartFragment
import xyz.volgoak.wordlearning.fragment.TrainingSelectFragment
import xyz.volgoak.wordlearning.fragment.WordCardsFragment

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

class MainActivity : NavigationActivity(), FragmentListener {

    private var exitPressed: Boolean = false

    private var banner: Banner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_new)
        super.onCreateNavigationDrawer()

        val extraTask = intent.getStringExtra(EXTRA_MODE)
        if (extraTask != null) {
            if (extraTask == START_DICTIONARY) {
                startDictionary()
            } else if (extraTask == SELECT_TRAINING) {
                selectTraining()
            }
        } else if (savedInstanceState == null)
            startHomeFragment()

        if (AdsManager.initialized) {
            val bannerLayout = findViewById<LinearLayout>(R.id.llBannerContainer)
            banner = Banner(this)
            banner!!.loadAdRequest()
            banner!!.setTargetView(bannerLayout)
        }
    }

    override fun onResume() {
        super.onResume()
        if (banner != null) banner!!.onResume()
    }

    override fun onPause() {
        super.onPause()
        if (banner != null) banner!!.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (banner != null) banner!!.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        AppRater.app_launched(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_activity_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        val current = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if (current != null && current is StartFragment) {
            if (exitPressed)
                finish()
            else {
                exitPressed = true
                Toast.makeText(this, R.string.press_exit_again, Toast.LENGTH_LONG).show()
                Handler().postDelayed({ exitPressed = false }, 2000)
            }
        } else
            super.onBackPressed()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_main_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId) {
            R.id.navigation_menu_to_home -> startHomeFragment()
            R.id.navigation_menu_training -> selectTraining()
            R.id.navigation_menu_redactor -> startDictionary()
            R.id.navigation_menu_sets -> startSets()
            R.id.navigation_menu_rate -> AppRater.rateApp(this)
            R.id.navigation_menu_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        mDrawerLayout.closeDrawer(GravityCompat.START)
        return false
    }

    fun startHomeFragment() {
        val fragment = StartFragment()
        startFragment(fragment)
    }

    override fun startDictionary() {
        val redactorFragment = DictionaryFragment()
        startFragment(redactorFragment)
    }

    override fun startTraining(type: Int) {
        startTraining(type, -1)
    }

    override fun startTraining(type: Int, setId: Long) {
        val intent = Intent(this, TrainingActivity::class.java)
        intent.putExtra(TrainingActivity.EXTRA_TRAINING_TYPE, type)
        intent.putExtra(TrainingActivity.EXTRA_SET_ID, setId)
        startActivity(intent)
    }

    override fun startSets() {
        val intent = Intent(this, SetsActivity::class.java)
        startActivity(intent)
    }

    override fun selectTraining() {
        val fragment = TrainingSelectFragment()
        startFragment(fragment)
    }

    override fun startCards(startPosition: Int) {
        val fragment = WordCardsFragment.newInstance(startPosition)
        startFragment(fragment)
    }

    fun startFragment(fragment: Fragment) {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        var addToBackStack = false
        if (currentFragment != null) {
            addToBackStack = true
            val klass = currentFragment.javaClass
            if (klass.isInstance(fragment)) return
        }

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragment_container, fragment)
        if (addToBackStack) ft.addToBackStack(null)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
    }

    override fun setActionBarTitle(title: String) {
        if (supportActionBar != null)
            supportActionBar!!.title = title
    }

    companion object {

        val TAG = "MainActivity"
        val EXTRA_MODE = "extra_mode"
        val START_DICTIONARY = "dictionary"
        val SELECT_TRAINING = "select_training"
    }
}
