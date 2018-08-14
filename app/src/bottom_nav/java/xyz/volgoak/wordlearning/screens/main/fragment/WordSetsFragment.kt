package xyz.volgoak.wordlearning.screens.main.fragment


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_word_sets.*

import java.util.ArrayList

import xyz.volgoak.wordlearning.R

import xyz.volgoak.wordlearning.data.DatabaseContract
import xyz.volgoak.wordlearning.entities.DataEntity
import xyz.volgoak.wordlearning.entities.Set
import xyz.volgoak.wordlearning.entities.Theme
import xyz.volgoak.wordlearning.model.WordsViewModel
import xyz.volgoak.wordlearning.adapter.SetsRecyclerAdapter
import xyz.volgoak.wordlearning.recycler.SingleChoiceMode

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

/**
 * A simple [Fragment] subclass.
 */
class WordSetsFragment : Fragment() {

    private var mRecyclerAdapter: SetsRecyclerAdapter? = null

    private var mPartScreenMode = true

    private var mRvSavedPosition: Int = 0
    private var mSelectedTheme = DatabaseContract.Themes.THEME_ANY
    private var mThemes: List<Theme>? = null

    private var viewModel: WordsViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_word_sets, container, false)
        setHasOptionsMenu(true)
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        if (arguments != null) {
            mPartScreenMode = arguments!!.getBoolean(EXTRA_PARTSCREEN_MODE, false)
        }

        if (savedInstanceState != null) {
            mRvSavedPosition = savedInstanceState.getInt(SAVED_POSITION, 0)
            mSelectedTheme = savedInstanceState.getString(SAVED_THEME, DatabaseContract.Themes.THEME_ANY)
        }

        viewModel = ViewModelProviders.of(activity!!).get(WordsViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity!!.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(context!!, R.color.colorPrimaryDark)
        }

        rv_setsfrag!!.setHasFixedSize(true)
        val llm = LinearLayoutManager(context)
        llm.orientation = LinearLayoutManager.VERTICAL
        rv_setsfrag!!.layoutManager = llm

        viewModel!!.themes.observe(this, Observer{ themes -> mThemes = themes })
        initRecycler(mSelectedTheme)

    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)

        /*if (context is SetsFragmentListener) {
            mSetsFragmentListener = context
        } else {
            throw RuntimeException("Activity must implement WordsFragmentListener")
        }*/

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.sets_fragment_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.item_sort_by_theme -> {
                showThemesList()
                return true
            }
            android.R.id.home -> {
                activity!!.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showThemesList() {
        val view = activity!!.findViewById<View>(R.id.item_sort_by_theme)
        val popupMenu = PopupMenu(context!!, view)

        val allThemes = popupMenu.menu.add(1, -1, 0, R.string.all_themes)
        allThemes.isCheckable = true
        allThemes.isChecked = true

        for (i in mThemes!!.indices) {
            val theme = mThemes!![i]
            val name = theme.name
            val item = popupMenu.menu.add(1, i, 0, name)
            item.isCheckable = true
            if (mSelectedTheme == theme.code) {
                item.isChecked = true
                allThemes.isChecked = false
            }
        }

        popupMenu.setOnMenuItemClickListener { item ->
            if (item.itemId == -1) {
                mSelectedTheme = ""
            } else {
                mSelectedTheme = mThemes!![item.itemId].code
            }
            viewModel!!.changeTheme(mSelectedTheme)
            true
        }

        popupMenu.show()
    }


    override fun onResume() {
        super.onResume()
        rv_setsfrag!!.layoutManager.scrollToPosition(mRvSavedPosition)
        mRvSavedPosition = 0
    }

    override fun onPause() {
        super.onPause()
        mRvSavedPosition = (rv_setsfrag!!.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(SAVED_POSITION, mRvSavedPosition)
        outState.putString(SAVED_THEME, mSelectedTheme)
    }

    /*override fun onClick(root: View, position: Int, entity: DataEntity) {
        val loadedData = viewModel!!.changeSet(entity.id)
        loadedData.observe(this, object : Observer<Boolean> {
            override fun onChanged(aBoolean: Boolean?) {
                loadedData.removeObserver(this)
//                mSetsFragmentListener!!.startSet(entity.id, root.findViewById(R.id.civ_sets))
            }
        })
    }*/

    fun initRecycler(themeCode: String) {

        mRecyclerAdapter = SetsRecyclerAdapter(ArrayList(), rv_setsfrag)

        mRecyclerAdapter!!.setStatusChanger = { set ->
            val currentStatus = set.status
            val newStatus = if (currentStatus == DatabaseContract.Sets.IN_DICTIONARY)
                DatabaseContract.Sets.OUT_OF_DICTIONARY
            else
                DatabaseContract.Sets.IN_DICTIONARY
            val setName = set.name

            val message: String
            message = if (newStatus == DatabaseContract.Sets.IN_DICTIONARY)
                getString(R.string.set_added_message, setName)
            else
                getString(R.string.set_removed_message, setName)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()

            set.status = newStatus
            viewModel!!.updateSetStatus(set)
            mRecyclerAdapter!!.notifyEntityChanged(set)
        }

        if (mPartScreenMode) {
            mRecyclerAdapter!!.setChoiceMode(SingleChoiceMode())
        }

        rv_setsfrag!!.adapter = mRecyclerAdapter

        viewModel!!.getSets(themeCode).observe(this, Observer { list ->
            list?.let { mRecyclerAdapter!!.changeData(it) }
        })
    }

    interface SetsFragmentListener {
        fun startSet(setId: Long, shared: View)
    }

    companion object {

        val TAG = "WordSetsFragment"
        val EXTRA_PARTSCREEN_MODE = "extra_screen_mode"
        val SAVED_POSITION = "saved_position"
        val SAVED_THEME = "saved_theme"

        fun newInstance(part_mode: Boolean): WordSetsFragment {
            val args = Bundle()
            args.putBoolean(EXTRA_PARTSCREEN_MODE, part_mode)

            val fragment = WordSetsFragment()
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
