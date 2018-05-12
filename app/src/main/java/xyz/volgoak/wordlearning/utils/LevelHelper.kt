package xyz.volgoak.wordlearning.utils

import android.content.Context
import android.preference.PreferenceManager

/**
 * Created by alex on 5/13/18.
 */
object LevelHelper {

    const val EXP_PREF = "expirience_pref"
    const val LEVEL_MULTIPLY = 1.5

    fun getExp(context: Context) : Int {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        return preferences.getInt(EXP_PREF, 0)
    }
}

data class Level(val level : Int, val progress : Int, val exp : Int)