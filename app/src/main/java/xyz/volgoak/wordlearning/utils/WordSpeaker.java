package xyz.volgoak.wordlearning.utils;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;
/**
 * Created by Alexander Karachev on 07.05.2017.
 */

public class WordSpeaker {

    public static final String TAG = "WordSpeaker";

    private TextToSpeech mTTS;
    private boolean mInitilized;

    //save first word and speak it when initialized
    //probably it's bad idea, but it's best what I can think out for now
    String mSavedWord;

    public WordSpeaker(Context context){
        mTTS = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    mTTS.setLanguage(Locale.US);
                }

                if(status == TextToSpeech.SUCCESS){
                    mInitilized = true;
                    if(mSavedWord != null) {
                        speakWord(mSavedWord);
                    }
                }
            }
        });

        mTTS.setSpeechRate(0.8f);

    }

    public void speakWord(String word){
//        Log.d(TAG, "speakWord: " + word);

        if(mInitilized) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                mTTS.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
            } else mTTS.speak(word, TextToSpeech.QUEUE_FLUSH, null);
        }else{
            mSavedWord = word;
        }
    }

    public void close(){
        mTTS.stop();
        mTTS.shutdown();
    }
}
