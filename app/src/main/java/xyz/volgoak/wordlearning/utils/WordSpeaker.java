package xyz.volgoak.wordlearning.utils;

import android.content.Context;
import android.os.Build;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

import xyz.volgoak.wordlearning.WordsApp;

/**
 * Created by Alexander Karachev on 07.05.2017.
 */

public abstract class WordSpeaker {

    public static final String TAG = "WordSpeaker";

    private static TextToSpeech sTts;
    private static boolean sInitilized = false;

    //save first word and speak it when initialized
    //probably it's bad idea, but it's best what I can think out for now
    private static String mSavedWord;

    private static void init(){
        Context context = WordsApp.getContext();
        sTts = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    sTts.setLanguage(Locale.US);
                }

                if(status == TextToSpeech.SUCCESS){
                    sInitilized = true;
                    if(mSavedWord != null) {
                        speakWord(mSavedWord);
                    }
                }
            }
        });

        sTts.setSpeechRate(0.8f);
    }

    public static void speakWord(String word){
//        Log.d(TAG, "speakWord: " + word);

        if(sInitilized) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                sTts.speak(word, TextToSpeech.QUEUE_FLUSH, null, null);
            } else sTts.speak(word, TextToSpeech.QUEUE_FLUSH, null);
        }else{
            mSavedWord = word;
            init();
        }
    }

    public static void close(){
        if(sTts != null) {
            sTts.stop();
            sTts.shutdown();
        }
    }
}
