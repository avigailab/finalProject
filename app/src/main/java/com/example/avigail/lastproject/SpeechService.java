package com.example.avigail.lastproject;
/*
import java.util.HashMap;
import java.util.Locale;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.util.Log;

public class SpeechService extends Service implements OnInitListener, OnUtteranceCompletedListener {
    TextToSpeech mTTS;
    int ready = 999;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("--------", "TTSService Created!");
        mTTS = new TextToSpeech(getApplicationContext(), this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(ready == 999) {
                    //wait
                }
                if(ready==1){
                    Log.d("READY","!!!!");
                    HashMap<String, String> myHashStream = new HashMap<String, String>();
                    myHashStream.put(TextToSpeech.Engine.KEY_PARAM_STREAM, String.valueOf(AudioManager.STREAM_NOTIFICATION));
                    myHashStream.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "1");

                    mTTS.setLanguage(Locale.US);
                    //mTTS.setOnUtteranceCompletedListener(this);
                    mTTS.speak("I'm saying some stuff to you!", TextToSpeech.QUEUE_ADD, myHashStream);

                } else {
                    Log.d("", "not ready");
                }
            }

        }).start();

    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        mTTS.shutdown();
        super.onDestroy();
    }
    @Override
    public void onInit(int status) {
        Log.d("", "TTSService onInit: " + String.valueOf(status));
        if (status == TextToSpeech.SUCCESS)
        {
            ready = 1;

        } else {
            ready = 0;
            Log.d("", "failed to initialize");
        }

    }

    public void onUtteranceCompleted(String uttId) {
        Log.d("", "done uttering");
        if(uttId == "1") {
            mTTS.shutdown();
        }

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
      Log.e("","onstatrt ");
        return Service.START_STICKY;
    }


}*/

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;


public class SpeechService extends Service implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    private boolean isLoaded;

    @Override
    public void onCreate() {
        super.onCreate();
        tts = new TextToSpeech(getApplicationContext(), this);
        Log.d("TimeSoundService", "onCreate() ended");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "startService", Toast.LENGTH_SHORT).show();

        Log.d("TimeSoundService", "Service started");

        String s = "String tts";

        Log.d("TimeSoundService", "String s = " + s);
        speak("test");
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        Log.d("TimeSoundService", "Service stopped");
        Toast.makeText(this,"toast_stopService", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInit(int status) {
        Log.d("SSERVICE oninit","launched");
        if(tts==null)
        {
            tts = new TextToSpeech(getApplicationContext(), this);
        }
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                isLoaded = true;
            } else {
                Log.d("TimeSoundService", "Language has missing data or is not supported");
                Toast.makeText(this, "Missing data", Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.d("TimeSoundService", "Status unsuccessful");
            Toast.makeText(this, "Status unsuccessful", Toast.LENGTH_SHORT).show();
        }

       // return Service.START_NOT_STICKY;
    }

    private void speak(String s) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (tts != null) {

            tts.speak(s, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}