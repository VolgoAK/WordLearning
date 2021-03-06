package xyz.volgoak.wordlearning.screens.set

import android.annotation.SuppressLint
import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.design.widget.BaseTransientBottomBar
import android.support.design.widget.Snackbar
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.view.Window
import com.attiladroid.data.DataContract
import com.attiladroid.data.entities.Set
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.screens.training.TrainingActivity
import xyz.volgoak.wordlearning.admob.AdsManager
import xyz.volgoak.wordlearning.admob.Banner
import xyz.volgoak.wordlearning.StorageContract
import xyz.volgoak.wordlearning.databinding.ActivitySetsBinding
import xyz.volgoak.wordlearning.extensions.*
import xyz.volgoak.wordlearning.screens.set.viewModel.SingleSetViewModel
import xyz.volgoak.wordlearning.screens.training.helpers.TrainingFabric
import xyz.volgoak.wordlearning.utils.round_bitmap.MyRoundBitmapFactory
import xyz.volgoak.wordlearning.utils.transitions.DetailsTransition
import java.io.File

class SetsActivity : AppCompatActivity() {

    lateinit var viewModel: SingleSetViewModel

    private var banner: Banner? = null

    private val setId by lazy { intent.getLongExtra(EXTRA_SET_ID, 0L) }
    private lateinit var binding: ActivitySetsBinding
    private var isSetInDictionary = false
    private var setName = ""

    companion object {
        private val EXTRA_SET_ID = "saved_set_id"
        const val CONTAINER = R.id.fSetContainer

        fun getIntent(context: Context, setId: Long): Intent {
            return Intent(context, SetsActivity::class.java).apply {
                putExtra(EXTRA_SET_ID, setId)
            }
        }
    }

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sets)
        manageActionBar()

        viewModel = ViewModelProviders.of(this, SingleSetViewModel.Factory(setId))
                .get(SingleSetViewModel::class.java)
        initSubscriptions()

        if (supportFragmentManager.findFragmentById(R.id.fSetContainer) == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fSetContainer, SingleSetFragment.newInstance(setId))
                    .commit()
        }

        if (AdsManager.initialized) {
            banner = Banner(this)
            banner!!.loadAdRequest()
            banner!!.setTargetView(binding.llBannerContainerSets)
        }


        sinceLollipop {
            postponeEnterTransition()
            window.sharedElementEnterTransition = DetailsTransition().apply {
                onTransitionEnd {
                    binding.setAddFab.show()
                    binding.setTrainingFab.show()
                    if(isSetInDictionary) binding.setResetFab.show()
                }
                onTransitionStart {
                    binding.setAddFab.setVisibility(false)
                    binding.setResetFab.setVisibility(false)
                    binding.setTrainingFab.setVisibility(false)
                }
            }

            window.sharedElementExitTransition.apply {
                onTransitionStart {
                    binding.setResetFab.hide()
                    binding.setTrainingFab.hide()
                    binding.setAddFab.hide()
                }
            }
        }
    }

    private fun manageActionBar() {
        setSupportActionBar(binding.setToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    private fun initSubscriptions() {
        viewModel.startCardsLD.observeSafe(this) { position -> startCards(position) }
        viewModel.setData.observeSafe(this) { set -> loadSetInformation(set) }
        viewModel.actionModeLD.observeSafe(this) { started -> onActionMode(started) }
        viewModel.setStatusChangedLD.observeSafe(this) { status -> onStatusChanged(status) }
    }

    private fun onActionMode(started: Boolean) {
        if (started) {
            binding.appbarSetact.setExpanded(false, true)
            binding.setAddFab.hide()
            binding.setResetFab.hide()
            binding.setTrainingFab.hide()
        } else {
            binding.setAddFab.show()
            binding.setResetFab.show()
            binding.setTrainingFab.show()
        }
    }


    @SuppressLint("NewApi")
    private fun loadSetInformation(set: Set) {
        //set title
        binding.collapsingToolbarSetAct.title = set.name
        ViewCompat.setTransitionName(binding.setIvTitle, set.name)

        loadImage(set.imageUrl)

        isSetInDictionary = set.status == DataContract.Sets.IN_DICTIONARY

        prepareSetStatusFabs()

        binding.setAddFab.setOnClickListener { viewModel.changeCurrentSetStatus() }
        binding.setResetFab.setOnClickListener { resetSetProgress() }
        binding.setTrainingFab.setOnClickListener { showCoolDialog() }
    }

    @SuppressLint("NewApi")
    private fun loadImage(imageUri: String) {
        //load title image
        val imageDir = File(filesDir, StorageContract.IMAGES_FOLDER)
        val imageFile = File(imageDir, imageUri)

        val target = object : Target {
            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

            }

            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {

            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                val drawable = MyRoundBitmapFactory.create(resources, bitmap)
                binding.setIvTitle.setImageDrawable(drawable)
                sinceLollipop {
                    startPostponedEnterTransition()
                }
            }
        }

        Picasso.get()
                .load(imageFile)
                .into(target)
    }

    private fun prepareSetStatusFabs() {
        binding.setResetFab.visibility = if (isSetInDictionary) View.VISIBLE else View.INVISIBLE
        val addDrawableId = if (isSetInDictionary) R.drawable.ic_remove_white_24dp else R.drawable.ic_add_white_24dp
        binding.setAddFab.setImageResource(addDrawableId)
    }

    private fun startCards(position: Int) {
        binding.appbarSetact.setExpanded(false, true)
        val cardsFragment = WordCardsFragment.newInstance(position)
        supportFragmentManager.beginTransaction()
                .add(R.id.fSetContainer, cardsFragment)
                .addToBackStack(null)
                .commit()
    }

    fun showCards(position: Int, shared: List<View>?) {
        binding.appbarSetact.setExpanded(false, true)
        supportFragmentManager?.beginTransaction()?.apply {
            setReorderingAllowed(true)
                    .replace(SetsActivity.CONTAINER, WordCardsFragment.newInstance(position))
                    .apply {
                        shared?.forEach {
                            addSharedElement(it)
                        }
                    }
                    .addToBackStack(null)
                    .commit()
        }
    }

    private fun showCoolDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.start_training_dialog)

        val titleToolbar = dialog.findViewById<Toolbar>(R.id.dialog_toolbar)
        titleToolbar.setTitle(R.string.training)
        titleToolbar.setNavigationIcon(R.drawable.ic_training_24dp)

        val listener = View.OnClickListener {
            startTraining(when (it.id) {
                R.id.cv_word_trans_dialog -> TrainingFabric.WORD_TRANSLATION
                R.id.cv_trans_word_dialog -> TrainingFabric.TRANSLATION_WORD
                R.id.cv_bool_dialog -> TrainingFabric.BOOL_TRAINING
                else -> throw RuntimeException("WTF it shouldn't exist!")
            })
            dialog.dismiss()
        }

        dialog.findViewById<View>(R.id.cv_word_trans_dialog).setOnClickListener(listener)
        dialog.findViewById<View>(R.id.cv_trans_word_dialog).setOnClickListener(listener)
        dialog.findViewById<View>(R.id.cv_bool_dialog).setOnClickListener(listener)

        dialog.show()
    }

    fun onStatusChanged(status: Int) {
        val messageId = if (status == DataContract.Sets.IN_DICTIONARY)
            R.string.set_added_message
        else
            R.string.set_removed_message
        val message = getString(messageId, setName)
        Snackbar.make(binding.coordinatorSetact, message, BaseTransientBottomBar.LENGTH_LONG).show()
    }

    private fun resetSetProgress() {
        Snackbar.make(findViewById(R.id.coordinator_setact),
                R.string.reset_progress_question,
                BaseTransientBottomBar.LENGTH_LONG)
                .setAction(R.string.reset) {
                    viewModel.resetCurrentSetProgress()
                }.show()
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
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startTraining(type: Int) {
        val intent = Intent(this, TrainingActivity::class.java).apply {
            putExtra(TrainingActivity.EXTRA_SET_ID, setId)
            putExtra(TrainingActivity.EXTRA_TRAINING_TYPE, type)
        }
        startActivity(intent)
    }
}
