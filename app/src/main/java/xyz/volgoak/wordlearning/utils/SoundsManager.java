package xyz.volgoak.wordlearning.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.preference.PreferenceManager;

import javax.inject.Inject;

import xyz.volgoak.wordlearning.R;
import xyz.volgoak.wordlearning.WordsApp;

/**
 * Created by alex on 2/2/18.
 */

public class SoundsManager {

    public enum Sound {
        TICK_SOUND(R.raw.tick),
        CORRECT_SOUND(R.raw.correct),
        WRONG_SOUND(R.raw.wrong);

        int soundId = -1;
        final int soundSource;

        Sound(int soundSource) {
            this.soundSource = soundSource;
        }
    }

    public static final String TAG = SoundsManager.class.getSimpleName();

    @Inject
    Context context;
    private SoundPool soundPool;
    private float volume = 0.05f;

    public SoundsManager() {
        WordsApp.getsComponent().inject(this);
    }

    public void play(Sound sound) {
        //don't play sound if preferences says so!
       if(!PreferenceManager.getDefaultSharedPreferences(context)
               .getBoolean(PreferenceContract.SOUNDS_ENABLED, true)) return;

       if(soundPool == null) init();
       if(sound.soundId == -1) {
           sound.soundId = soundPool.load(context, sound.soundSource, 1);
           soundPool.setOnLoadCompleteListener((pool, id, status) -> {
               if(status == 0) pool.play(id, volume, volume, 1, 0, 1);
           });
       } else {
           soundPool.play(sound.soundId, volume, volume, 1, 0, 1);
       }
    }

    public void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder().setMaxStreams(2).build();
        } else {
            soundPool = new SoundPool(2, android.media.AudioManager.STREAM_MUSIC, 1);
        }
    }

    public void release() {
        if(soundPool != null) {
            soundPool.release();
            soundPool = null;
        }

        for(Sound s: Sound.values()) {
            s.soundId = -1;
        }
    }

}
