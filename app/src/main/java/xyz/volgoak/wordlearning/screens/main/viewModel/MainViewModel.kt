package xyz.volgoak.wordlearning.screens.main.viewModel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import com.attiladroid.data.DataProvider
import com.attiladroid.data.DataContract
import com.attiladroid.data.entities.DictionaryInfo
import com.attiladroid.data.entities.Theme
import com.attiladroid.data.entities.Set
import kotlinx.coroutines.experimental.CommonPool.executor
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import timber.log.Timber
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.WordsApp

import xyz.volgoak.wordlearning.extensions.toast
import xyz.volgoak.wordlearning.utils.SingleLiveEvent
import java.util.concurrent.Executors
import javax.inject.Inject

class MainViewModel(val app: Application) : AndroidViewModel(app) {

    @Inject
    lateinit var dataProvider: DataProvider

    val titleLD = MutableLiveData<String>()
    val startTrainingLD = SingleLiveEvent<Pair<Int, Long>>()
    val themesLD: LiveData<List<Theme>> by lazy { dataProvider.allThemes }

    private val allSetsLiveData by lazy {
        MediatorLiveData<List<Set>>().apply {
            addSource(dataProvider.allSetsLd) {
                it?.let {
                    value = it.toMutableList().apply { add(0, getDictionary()) }
                }
            }
        }
    }
    val setsLD by lazy { createSetsLiveData() }
    val dictionaryInfoLiveData: LiveData<DictionaryInfo> by lazy { dataProvider.dictionaryInfo }

    private var themesFilter: (Set) -> Boolean = { true }

    init {
        WordsApp.getsComponent().inject(this)
    }

    fun setTitle(titleId: Int) {
        val title = app.getString(titleId)
        titleLD.value = title
    }

    fun startTraining(type: Int, setId: Long) {
        startTrainingLD.value = Pair(type, setId)
    }

    fun changeTheme(theme: String) {
        themesFilter = { set -> set.themeCodes.contains(theme) }
        launch(UI) {
            val result = async {
                allSetsLiveData.value
                        ?.filter { themesFilter(it) }
                        ?.toMutableList()
            }
            setsLD.value = result.await()
        }
    }

    fun changeSetStatus(set: Set) {
        var status: Int
        val message = if (set.status == DataContract.Sets.IN_DICTIONARY) {
            status = DataContract.Sets.OUT_OF_DICTIONARY
            app.getString(R.string.set_removed_message, set.name)
        } else {
            status = DataContract.Sets.IN_DICTIONARY
            app.getString(R.string.set_added_message, set.name)
        }

        app.toast(message)
        async {
            dataProvider.updateSetStatus(set.copy(status = status))
        }
    }

    private fun createSetsLiveData() =
            MediatorLiveData<MutableList<Set>>().apply {
                addSource(allSetsLiveData) { setList ->
                    setList?.let {
                        postValue(it.filter { themesFilter(it) }.toMutableList())
                    }
                }
            }

    private fun getDictionary() = Set(DataContract.DICTIONARY_ID,
            app.getString(R.string.dictionary),
            app.getString(R.string.dictionary_description),
            imageUrl = "dictionary.jpg")

}