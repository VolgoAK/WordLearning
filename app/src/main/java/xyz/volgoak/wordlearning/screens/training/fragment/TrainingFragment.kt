package xyz.volgoak.wordlearning.screens.training.fragment


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Application
import android.arch.lifecycle.ViewModelProviders
import android.databinding.DataBindingUtil
import android.databinding.ObservableBoolean
import android.databinding.ObservableField
import android.graphics.drawable.Drawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.preference.PreferenceManager
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.Button
import android.widget.Toast
import com.attiladroid.data.DataProvider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.R.string.word
import xyz.volgoak.wordlearning.WordsApp
import xyz.volgoak.wordlearning.databinding.FragmentTrainingBinding
import xyz.volgoak.wordlearning.extensions.observeSafe
import xyz.volgoak.wordlearning.screens.training.TrainingViewModel
import xyz.volgoak.wordlearning.screens.training.helpers.Results
import xyz.volgoak.wordlearning.screens.training.helpers.Training
import xyz.volgoak.wordlearning.screens.training.helpers.TrainingFabric
import xyz.volgoak.wordlearning.screens.training.helpers.TrainingWord
import xyz.volgoak.wordlearning.utils.Optional
import xyz.volgoak.wordlearning.utils.PreferenceContract
import xyz.volgoak.wordlearning.utils.SoundsManager
import xyz.volgoak.wordlearning.utils.WordSpeaker
import xyz.volgoak.wordlearning.utils.animation.MetallBounceInterpoltor
import javax.inject.Inject


/**
 * Created by Alexander Karachev on 07.05.2017.
 */

/**
 * A simple [Fragment] subclass.
 */
class TrainingFragment : Fragment() {

    var trainingType: Int = 0
    var mSetId: Long = 0

    private lateinit var viewModel: TrainingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WordsApp.getsComponent().inject(this)
        viewModel = ViewModelProviders.of(activity!!,
                TrainingViewModel.Factory(mSetId, trainingType, context!!.applicationContext as Application))
                .get(TrainingViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        val mBinding: FragmentTrainingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_training, container, false)
        mBinding.setLifecycleOwner(this)
        mBinding.viewModel = viewModel

        return mBinding.root
    }

   /* private fun hideShowNextButton(show: Boolean) {
        //show a next button
        if (mNextButtonPath == 0f) {
            val metrics = DisplayMetrics()
            activity!!.windowManager.defaultDisplay.getMetrics(metrics)
            mNextButtonPath = metrics.widthPixels - mBinding!!.btNextTf.x
            mBinding!!.btNextTf.visibility = View.VISIBLE
        }
        val animator: ObjectAnimator
        if (show) {
            animator = ObjectAnimator.ofFloat(mBinding!!.btNextTf, "TranslationX", mNextButtonPath, 0F)
            animator.interpolator = MetallBounceInterpoltor()
        } else {
            animator = ObjectAnimator.ofFloat(mBinding!!.btNextTf, "TranslationX", 0F, mNextButtonPath)
            animator.interpolator = AccelerateInterpolator()
        }

        animator.duration = 500
        animator.start()
    }*/

    companion object {

        val TAG = TrainingFragment::class.java.simpleName
        const val EXTRA_SET_ID = "extra_set_id"
        const val EXTRA_TRAINING_TYPE = "extra_training_type"

        fun getWordTrainingFragment(trainingType: Int, setId: Long): TrainingFragment {
            val fragment = TrainingFragment()
            fragment.trainingType = trainingType
            fragment.mSetId = setId
            return fragment
        }
    }
}// Required empty public constructor
