package xyz.volgoak.wordlearning.fragment


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import xyz.volgoak.wordlearning.R
import com.attiladroid.data.entities.Set
import xyz.volgoak.wordlearning.screens.set.viewModel.WordsViewModel


/**
 * A simple [Fragment] subclass.
 */
class ContainerFragment : Fragment() {

    lateinit var viewModel: WordsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragmen
        val root = inflater.inflate(R.layout.fragment_container, container, false)

        //Set toolbar with back button
        val toolbar = root.findViewById<Toolbar>(R.id.toolbarContainer)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        (activity as AppCompatActivity).supportActionBar?.setHomeButtonEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { activity?.supportFragmentManager?.popBackStack() }

        /*//Observe title for toolbar
        viewModel = ViewModelProviders.of(activity!!).get(WordsViewModel::class.java)
        viewModel.currentSet.observe(this, Observer { set ->
            set?.let { toolbar.title = it.name }
        })*/

        val position = arguments!!.getInt(EXTRA_POSITION)
        childFragmentManager.beginTransaction()
                .replace(R.id.container, WordCardsFragment.newInstance(position))
                .commit()
        return root
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity!!.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = ContextCompat.getColor(context!!, R.color.colorPrimaryDark)
        }
    }

    companion object {
        const val EXTRA_POSITION = "extra_position"

        fun newInstance(position: Int): ContainerFragment {

            val args = Bundle()
            args.putInt(EXTRA_POSITION, position)

            val fragment = ContainerFragment()
            fragment.arguments = args
            return fragment
        }
    }
}
