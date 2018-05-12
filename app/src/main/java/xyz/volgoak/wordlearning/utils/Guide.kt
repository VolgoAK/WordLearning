package xyz.volgoak.wordlearning.utils

import android.app.Activity
import android.content.Context
import android.preference.PreferenceManager
import android.support.design.widget.FloatingActionButton
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.takusemba.spotlight.SimpleTarget
import com.takusemba.spotlight.Spotlight
import timber.log.Timber
import xyz.volgoak.wordlearning.R
import xyz.volgoak.wordlearning.fragment.SingleSetFragment
import xyz.volgoak.wordlearning.fragment.WordCardsFragment

/**
 * Created by alex on 5/9/18.
 */
object Guide {

    private const val CARDS_FRAG_PREF = "cards_fragment_guide"
    private const val SETS_FRAG_PREF = "sets_fragment_guide"

    fun showGuide(fragment: WordCardsFragment, ignorePrefs: Boolean = false) {
        fragment.activity?.let {
            if (!ignorePrefs) {
                if (checkPref(it, CARDS_FRAG_PREF)) return
            }

            val radius = Functions.dpToPx(it, 30f)

            runGuide(it,
                    titles = listOf(R.string.translation,
                            R.string.add_to_dictionary,
                            R.string.progress),
                    description = listOf(R.string.click_to_translate,
                            R.string.click_to_add_or_delete,
                            R.string.click_to_more_info),
                    targets = listOf(R.id.btShow,
                            R.id.ivAddRemove,
                            R.id.ivShowSettings),
                    radius = radius)
        }
    }

    fun showGuide(fragment: SingleSetFragment, ignorePrefs: Boolean = false) {
        if (!ignorePrefs) {
            if (checkPref(fragment.context!!, SETS_FRAG_PREF)) return
        }

        val radius = Functions.dpToPx(fragment.context!!, 30f)

        runGuide(fragment.activity!!,
                titles = listOf(R.string.start_training,
                        R.string.add_to_dictionary,
                        R.string.words_cards),
                description = listOf(R.string.click_to_begin_training,
                        R.string.click_to_add_set,
                        R.string.click_and_open_card),
                targets = listOf(R.id.set_training_fab,
                        R.id.set_add_fab,
                        R.id.tv_word_adapter),
                radius = radius)
    }

    private fun checkPref(context: Context, pref: String): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val showed = prefs.getBoolean(pref, false)
        if (showed) return true

        prefs.edit().putBoolean(pref, true).apply()
        return false
    }

    private fun runGuide(activity: Activity, titles: List<Int>,
                         description: List<Int>, targets: List<Int>, radius: Float) {
        val targetArray: Array<SimpleTarget?> = arrayOfNulls(titles.size)

        //Some times throwing an exception
        //Can find cause. It's not a critical part of app, so just ignore it
        try {
            for (i in 0 until titles.size) {
                val point = activity.findViewById<View>(targets[i])

                val spot = SimpleTarget.Builder(activity)
                        .setPoint(point)
                        .setTitle(activity.getString(titles[i]))
                        .setDescription(activity.getString(description[i]))
                        .setRadius(radius)
                        .build()
                targetArray[i] = spot
            }

            Spotlight.with(activity)
                    .setTargets(*targetArray)
                    .setClosedOnTouchedOutside(true)
                    .start()
        } catch (ex : Exception) {
            Timber.e(ex)
        }
    }
}