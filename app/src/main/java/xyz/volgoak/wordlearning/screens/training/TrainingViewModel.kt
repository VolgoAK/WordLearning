package xyz.volgoak.wordlearning.screens.training

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.graphics.drawable.Drawable
import android.support.v4.content.ContextCompat
import android.support.v7.preference.PreferenceManager
import com.attiladroid.data.DataProvider
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.WordsApp
import xyz.volgoak.wordlearning.screens.training.helpers.Results
import xyz.volgoak.wordlearning.screens.training.helpers.Training
import xyz.volgoak.wordlearning.screens.training.helpers.TrainingFabric
import xyz.volgoak.wordlearning.screens.training.helpers.TrainingWord
import xyz.volgoak.wordlearning.utils.SingleLiveEvent
import xyz.volgoak.wordlearning.utils.SoundsManager
import javax.inject.Inject

class TrainingViewModel(
        private val setId: Long,
        val type: Int,
        application: Application) : AndroidViewModel(application) {

    @Inject
    lateinit var dataProvider: DataProvider
    @Inject
    lateinit var soundsManager: SoundsManager

    private val defaultBackground by lazy { ContextCompat.getDrawable(application, R.drawable.blue_button)!! }
    private val unavailableBackground by lazy { ContextCompat.getDrawable(application, R.drawable.blue_button_unavailable)!! }
    private val wrongAnswerBackground by lazy { ContextCompat.getDrawable(application, R.drawable.orange_button)!! }
    private val correctAnswerBackground by lazy { ContextCompat.getDrawable(application, R.drawable.green_button)!! }
    private val defaultBackgrounds by lazy { Array(4) { defaultBackground } }

    val timerFinishedLd = SingleLiveEvent<Boolean>()
    val noWordsLd = SingleLiveEvent<Boolean>()
    val resultsLD = MutableLiveData<Results>()
    val progressLD = MutableLiveData<Float>().apply { value = 0F }
    val progressTextLD = MutableLiveData<String>()
    val currentWord = MutableLiveData<TrainingWord>()
    val accessible = MutableLiveData<Boolean>().apply { value = true }
    val nextVisible = MutableLiveData<Boolean>()
    val buttonBackgroundsLD by lazy {
        MutableLiveData<Array<Drawable>>().apply { value = defaultBackgrounds }
    }


    private var training: Training? = null

    init {
        WordsApp.getsComponent().inject(this)
        createTraining()
    }

    fun nextWord() {
        val trainingWord = training?.nextWord
        if (trainingWord == null) {
            resultsLD.value = training!!.results.apply {
                setId = this@TrainingViewModel.setId
            }
        } else {
            accessible.value = true
            currentWord.value = trainingWord
            progressLD.value = training!!.progressInPercents
            progressTextLD.value = training!!.progressString
            nextVisible.value = false
            resetDrawables()
        }
    }

    fun checkAnswer(num: Int) {
        accessible.value = false
        val correct = training!!.checkAnswer(num)
        manageDrawables(num, correct)
        nextVisible.value = true
        soundsManager.play(if (correct) SoundsManager.Sound.CORRECT_SOUND else SoundsManager.Sound.WRONG_SOUND)
    }

    private fun manageDrawables(num: Int, correct: Boolean) {
        val drawables = Array(4) { unavailableBackground }
        drawables[num] = if (correct) correctAnswerBackground else wrongAnswerBackground
        buttonBackgroundsLD.value = drawables
    }

    private fun resetDrawables() {
        buttonBackgroundsLD.value = defaultBackgrounds
    }

    private fun createTraining() = launch(UI) {
        val task = async {
            //todo move to preferences provider
            val preferences = PreferenceManager.getDefaultSharedPreferences(getApplication())
            val wordsLimit = preferences.getString(getApplication<WordsApp>().getString(R.string.preference_words_in_training_key), "10")
            if(type == TrainingFabric.BOOL_TRAINING) {
                TrainingFabric.getBoolTraining(setId, dataProvider)
            } else {
                TrainingFabric.getSimpleTraining(type, setId, Integer.parseInt(wordsLimit), dataProvider)
            }
        }

        task.await()?.apply {
            onTrainingReady(this)
        } ?: kotlin.run {
            noWordsLd.value = true
        }
    }


fun pronounceWord() {
    /* if (PreferenceManager.getDefaultSharedPreferences(context!!)
                     .getBoolean(PreferenceContract.AUTO_PLAY_PRONOUN, true)) {
         pronounceWord()
     }
     if (trainingType == TrainingFabric.WORD_TRANSLATION)
         WordSpeaker.speakWord(mTrainingWord!!.word)*/
}

private fun onTrainingReady(training: Training) {
    this.training = training
    currentWord.value = training.firstWord
    progressTextLD.value = training.progressString
}

class Factory(val setId: Long, val type: Int, val application: Application) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>) = TrainingViewModel(setId, type, application) as T
}
}