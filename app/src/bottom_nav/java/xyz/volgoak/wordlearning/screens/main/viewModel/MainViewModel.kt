package xyz.volgoak.wordlearning.screens.main.viewModel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.IdRes
import xyz.volgoak.wordlearning.utils.SingleLiveEvent

class MainViewModel(val app: Application) : AndroidViewModel(app){

    val titleLiveData = MutableLiveData<String>()
    val startTrainingLiveData = SingleLiveEvent<Pair<Int,Long>>()

    fun setTitle(titleId: Int) {
        val title = app.getString(titleId)
        titleLiveData.value = title
    }

    fun startTraining(type: Int, setId: Long) {
        startTrainingLiveData.value = Pair(type, setId)
    }
}