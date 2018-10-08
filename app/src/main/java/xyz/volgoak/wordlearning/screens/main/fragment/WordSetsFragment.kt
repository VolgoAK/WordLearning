package xyz.volgoak.wordlearning.screens.main.fragment


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.util.Pair
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.view.*
import com.attiladroid.data.DataContract
import com.attiladroid.data.entities.Set
import com.attiladroid.data.entities.Theme
import kotlinx.android.synthetic.main.fragment_word_sets.*
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.adapter.SetsRecyclerAdapter
import xyz.volgoak.wordlearning.extensions.observeSafe
import xyz.volgoak.wordlearning.screens.dictionary.DictionaryActivity
import xyz.volgoak.wordlearning.screens.main.viewModel.MainViewModel
import xyz.volgoak.wordlearning.screens.set.SetsActivity

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

/**
 * A simple [Fragment] subclass.
 */
class WordSetsFragment : Fragment() {

    private var mRecyclerAdapter: SetsRecyclerAdapter? = null

    private var mSelectedTheme = DataContract.Themes.THEME_ANY
    private var mThemes: List<Theme>? = null

    private lateinit var viewModel: MainViewModel

    private var savedLLState: Parcelable? = null

    companion object {
        const val TAG = "WordSetsFragment"
        const val EXTRA_PARTSCREEN_MODE = "extra_screen_mode"
        const val SAVED_LL_STATE = "saved_ll_state"
        const val SAVED_THEME = "saved_theme"

        fun newInstance(part_mode: Boolean): WordSetsFragment {
            val args = Bundle()
            args.putBoolean(EXTRA_PARTSCREEN_MODE, part_mode)

            val fragment = WordSetsFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_word_sets, container, false)
        setHasOptionsMenu(true)
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        if (savedInstanceState != null) {
            mSelectedTheme = savedInstanceState.getString(SAVED_THEME, DataContract.Themes.THEME_ANY)
            savedLLState = savedInstanceState.getParcelable(SAVED_LL_STATE)
        }

        viewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity!!.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(context!!, R.color.colorPrimaryDark)
        }

        viewModel.themesLD.observe(this, Observer { themes -> mThemes = themes })
        viewModel.setsLD.observeSafe(this) { onSetsReady(it)}

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

    private fun showThemesList() {
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
                mSelectedTheme = mThemes!![item.itemId].code!!
            }
            viewModel.changeTheme(mSelectedTheme)
            true
        }

        popupMenu.show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SAVED_THEME, mSelectedTheme)
        rv_setsfrag.layoutManager?.let {
            outState.putParcelable(SAVED_LL_STATE, it.onSaveInstanceState())
        }
    }

    private fun onSetsReady(data: MutableList<Set>) {
        if (mRecyclerAdapter == null) {
            LinearLayoutManager(context).apply {
                orientation = LinearLayoutManager.VERTICAL
                savedLLState?.let { state ->
                    onRestoreInstanceState(state) }
                rv_setsfrag.layoutManager = this
            }

            rv_setsfrag.setHasFixedSize(true)

            mRecyclerAdapter = SetsRecyclerAdapter(data, rv_setsfrag)
            mRecyclerAdapter!!.setStatusChanger = { set ->
                viewModel.changeSetStatus(set)
            }

            mRecyclerAdapter!!.onClick = { set, _ , shared->
                if(set.id == DataContract.DICTIONARY_ID) {
                    startActivity(Intent(context!!, DictionaryActivity::class.java))
                } else {
                    startSetActivity(set.id, shared)
                }
            }

            rv_setsfrag!!.adapter = mRecyclerAdapter
        } else {
            mRecyclerAdapter?.changeData(data)
        }
    }

    private fun startSetActivity(id: Long, shared: List<View>?) {
        val pairs = shared?.map {
            Pair(it, ViewCompat.getTransitionName(it))
        }?.toTypedArray() ?: arrayOf()

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity!!, *pairs)
        startActivity(SetsActivity.getIntent(activity!!, id), options.toBundle())
    }
}
