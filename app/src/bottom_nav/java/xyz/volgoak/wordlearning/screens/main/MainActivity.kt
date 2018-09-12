package xyz.volgoak.wordlearning.screens.main

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationAdapter
import kotlinx.android.synthetic.bottom_nav.activity_main_new.*
import xyz.volgoak.wordlearning.utils.AppRater
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.screens.base.NavigationActivity
import xyz.volgoak.wordlearning.screens.settings.SettingsActivity
import xyz.volgoak.wordlearning.screens.training.TrainingActivity
import xyz.volgoak.wordlearning.admob.AdsManager
import xyz.volgoak.wordlearning.admob.Banner
import xyz.volgoak.wordlearning.extensions.observeSafe
import xyz.volgoak.wordlearning.screens.main.adapter.PagerAdapter
import xyz.volgoak.wordlearning.screens.main.fragment.StartFragment
import xyz.volgoak.wordlearning.screens.main.fragment.TrainingSelectFragment
import xyz.volgoak.wordlearning.screens.main.fragment.WordSetsFragment
import xyz.volgoak.wordlearning.screens.main.viewModel.MainViewModel

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

class MainActivity : NavigationActivity() {

    private var exitPressed: Boolean = false

    private var banner: Banner? = null

    lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_new)
        super.onCreateNavigationDrawer()

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        initSubscriptions()

        if (AdsManager.initialized) {
            val bannerLayout = findViewById<LinearLayout>(R.id.llBannerContainer)
            banner = Banner(this)
            banner!!.loadAdRequest()
            banner!!.setTargetView(bannerLayout)
        }

        initBottomNavigation()
    }

    private fun initSubscriptions() {
        viewModel.titleLD.observeSafe(this) { title = it }
        viewModel.startTrainingLD.observeSafe(this) { startTraining(it.first, it.second) }
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
        if (exitPressed)
            finish()
        else {
            exitPressed = true
            Toast.makeText(this, R.string.press_exit_again, Toast.LENGTH_LONG).show()
            Handler().postDelayed({ exitPressed = false }, 2000)
        }
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
            R.id.navigation_menu_rate -> AppRater.rateApp(this)
            R.id.navigation_menu_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        mDrawerLayout.closeDrawer(GravityCompat.START)
        return false
    }


    private fun startTraining(type: Int, setId: Long) {
        val intent = Intent(this, TrainingActivity::class.java)
        intent.putExtra(TrainingActivity.EXTRA_TRAINING_TYPE, type)
        intent.putExtra(TrainingActivity.EXTRA_SET_ID, setId)
        startActivity(intent)
    }

    private fun initBottomNavigation() {
        val adapter = PagerAdapter(supportFragmentManager)
        adapter.addFragment(StartFragment())
        adapter.addFragment(TrainingSelectFragment())
        adapter.addFragment(WordSetsFragment())
        pager.offscreenPageLimit = 2
        pager.adapter = adapter

        val bottomAdapter = AHBottomNavigationAdapter(this, R.menu.menu_bottom_navigation)
        bottomAdapter.setupWithBottomNavigation(vNavigation)
        vNavigation.inactiveColor = ContextCompat.getColor(this, R.color.gray)
        vNavigation.defaultBackgroundColor = ContextCompat.getColor(this, R.color.colorPrimaryDark)
        vNavigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
        vNavigation.isForceTint = false
        vNavigation.accentColor = ContextCompat.getColor(this, R.color.yellow)

        vNavigation.setOnTabSelectedListener { position, wasSelected ->
            if (pager.currentItem != position) {
                pager.currentItem = position
                true
            } else false
        }
        vNavigation.currentItem = 0
    }
}
