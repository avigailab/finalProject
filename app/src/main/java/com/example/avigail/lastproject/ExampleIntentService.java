package com.example.avigail.lastproject;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by avigail on 26/01/17.
 */
public class ExampleIntentService extends IntentService implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {
    private static final String TAG = ExampleIntentService.class.getName();
    public static final String EXTRA_DATA = "WORD";
    TextToSpeech tts;
    String spokenText;
    public ExampleIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //spokenText = intent.getStringExtra(EXTRA_DATA);
        // The code you want to execute
        Log.d("onHandleIntent=====",intent.getStringExtra("spoken_txt"));
    }
    public void onCreate() {
        tts = new TextToSpeech(this, this);
    }

    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.ENGLISH);

            // tts.setPitch(5); // set pitch level
            // tts.setSpeechRate(2); // set speech speed rate

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Language is not supported");
            } else {
                speakOut(spokenText);
            }

        /*if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
            tts.speak(c, TextToSpeech.QUEUE_FLUSH, null);
        }*/
        } else {
            Log.e("TTS", "Initilization Failed");
        }
    }

    private void speakOut(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void onUtteranceCompleted(String uttId) {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



}
