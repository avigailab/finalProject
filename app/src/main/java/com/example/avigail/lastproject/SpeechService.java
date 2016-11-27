package com.example.avigail.lastproject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

public class SpeechService extends Service implements TextToSpeech.OnInitListener{

    private String str;
    private TextToSpeech mTts;
    private static final String TAG="TTSService";
    private static final String DEFULT_WORD="empty";


    @Override

    public IBinder onBind(Intent arg0) {

        return null;
    }


    @Override
    public void onCreate() {

        mTts = new TextToSpeech(this,
                this  // OnInitListener
        );
        mTts.setSpeechRate(0.5f);
        Log.v(TAG, "oncreate_service");
        str =DEFULT_WORD;
        super.onCreate();
    }


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        str=intent.getStringExtra("WORD");
        speak(str);
        Log.v(TAG, "onstart_service");

    }

    @Override
    public void onInit(int status) {
        Log.v(TAG, "oninit");
        if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(Locale.US);
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.v(TAG, "Language is not available.");
            } else {

                speak(str);

            }
        } else {
            Log.v(TAG, "Could not initialize TextToSpeech.");
        }
    }
    private void speak(String str) {
        mTts.speak(str,
                TextToSpeech.QUEUE_FLUSH,
                null);
    }

}
