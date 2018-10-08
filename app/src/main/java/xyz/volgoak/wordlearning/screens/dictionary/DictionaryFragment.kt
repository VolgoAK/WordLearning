package xyz.volgoak.wordlearning.screens.dictionary

import android.app.AlertDialog
import android.app.Dialog
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.attiladroid.data.DataContract
import com.attiladroid.data.entities.Word
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.adapter.WordsRecyclerAdapter
import xyz.volgoak.wordlearning.databinding.FragmentRedactorBinding
import xyz.volgoak.wordlearning.extensions.observeSafe
import java.util.*

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

/**
 * A simple [Fragment] subclass.
 */
class DictionaryFragment : Fragment() {

    private var mRecyclerAdapter: WordsRecyclerAdapter? = null

    private var mBinding: FragmentRedactorBinding? = null
    private lateinit var viewModel: DictionaryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(DictionaryViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_redactor, container, false)

        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        mBinding!!.rvRedactor.layoutManager = layoutManager

        mRecyclerAdapter = WordsRecyclerAdapter(context!!, ArrayList(), mBinding!!.rvRedactor).apply {
            onClick = { word, position, _ -> onWordClicked(position, word) }
            onLongClick = { word, _ -> fireDeleteWordDialog(word) }
        }

        mBinding!!.rvRedactor.adapter = mRecyclerAdapter

        viewModel.wordsLd.observeSafe(this) {
            mRecyclerAdapter!!.changeData(it.sortedByDescending { it.addedTime }.toMutableList())
        }

        mBinding!!.fabAddRedactor.setOnClickListener { v -> fireAddWordDialog() }

        return mBinding!!.root
    }

    private fun onWordClicked(position: Int, word: Word) {
//        mFragmentListener!!.startCards(position)
    }

    fun fireAddWordDialog() {
        val dialog = Dialog(context!!)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_add_word)

        val wordEt = dialog.findViewById<EditText>(R.id.et_word_redactor_dialog)
        val translationEt = dialog.findViewById<EditText>(R.id.et_translation_redactor_dialog)

        val addButton = dialog.findViewById<Button>(R.id.bt_add_redactor_dialog)
        addButton.setOnClickListener { v ->
            val word = wordEt.text.toString()
            val translation = translationEt.text.toString()
            if (!word.isEmpty() && !translation.isEmpty()) {
                viewModel.insertWord(Word(word = word,
                        translation = translation,
                        status = DataContract.Words.IN_DICTIONARY,
                        addedTime = System.currentTimeMillis()))
            } else {
                Toast.makeText(context, R.string.fields_empty_message, Toast.LENGTH_LONG).show()
            }
            dialog.dismiss()
        }

        val cancelButton = dialog.findViewById<Button>(R.id.bt_cancel_redactor_dialog)
        cancelButton.setOnClickListener { v -> dialog.dismiss() }

        val toolbar = dialog.findViewById<Toolbar>(R.id.dialog_add_word_toolbar)
        toolbar.setTitle(R.string.new_word)

        dialog.show()
    }

    fun fireDeleteWordDialog(word: Word) {
        AlertDialog.Builder(context!!)
                .setTitle(R.string.choice_action)
                .setPositiveButton(R.string.reset_progress) { _, _ ->
                    viewModel.resetWordProgress(word)
                }
                .setNegativeButton(R.string.remove) { _, _ ->
                    viewModel.deleteOrHideWord(word)
                }
                .show()
    }

    companion object {

        val TAG = DictionaryFragment::class.java.simpleName
    }

}// Required empty public constructor
