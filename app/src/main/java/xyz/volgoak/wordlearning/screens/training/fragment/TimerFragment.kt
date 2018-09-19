package xyz.volgoak.wordlearning.screens.training.fragment


import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.fragment_timer.*
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.WordsApp
import xyz.volgoak.wordlearning.screens.training.TrainingViewModel
import xyz.volgoak.wordlearning.utils.SoundsManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * A simple [Fragment] subclass.
 */
class TimerFragment : Fragment() {

    private lateinit var viewModel: TrainingViewModel

    companion object {

        val TAG = TimerFragment::class.java.simpleName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(activity!!).get(TrainingViewModel::class.java)
        WordsApp.getsComponent().inject(this)
    }

    @Inject
    lateinit var soundsManager: SoundsManager

    private var timerDisposble: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_timer, container, false)
    }

    override fun onResume() {
        super.onResume()
        runTimerBounce()
    }

    override fun onPause() {
        super.onPause()
        timerDisposble?.dispose()
    }

    private fun runTimerBounce() {
        bt_one_timer!!.text = "3"
        timerDisposble = Observable.just(2, 1, 0, -1)
                .concatMap { Observable.just(it).delay(1000, TimeUnit.MILLISECONDS) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    soundsManager.play(SoundsManager.Sound.TICK_SOUND)
                    when (it) {
                        -1 -> viewModel.timerFinishedLd.value = true
                        0 -> {
                            bt_one_timer.setText(R.string.go)
                            bt_one_timer.background = ContextCompat.getDrawable(context!!, R.drawable.green_circle)
                        }
                        else -> bt_one_timer.text = it.toString()
                    }
                }
    }
}
