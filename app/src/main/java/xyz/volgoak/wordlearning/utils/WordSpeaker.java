package xyz.volgoak.wordlearning.utils;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Volgoak on 14.04.2017.
 */

public class WordSpeaker {

    public static final String TAG = "WordSpeaker";

    private Context mContext;
    private TextToSpeech mTTS;

    public WordSpeaker(Context context){
        mContext = context;
        mTTS = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    mTTS.setLanguage(Locale.US);
                }
            }
        });

        mTTS.setSpeechRate(0.2f);
    }

    public void speakWord(String word){
        Log.d(TAG, "speakWord: " + word);
        // TODO: 14.04.2017 call difrent method for diffrent version
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            mTTS.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
        }else  mTTS.speak(word, TextToSpeech.QUEUE_FLUSH, null);

    }
}
