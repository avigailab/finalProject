package com.example.avigail.lastproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PlayerActivity extends Activity implements
        TextToSpeech.OnInitListener {

    // TTS fields
    private TextToSpeech mTts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_out);

        List<TTSPlayItem> playItems = new ArrayList<TTSPlayItem>();


        TTSPlayItem playItem = new TTSPlayItem();
        playItem.locale = getLocale1();
        playItem.text = getText1();
        playItem.position = position;
        playItems.add(playItem);
        playItem = new TTSPlayItem();
        playItem.locale = getLocale2();
        playItem.text = getText2();
        playItem.position = position;
        playItems.add(playItem);

        TTSPlayItem[] passPlayItems = playItems
                .toArray(new TTSPlayItem[playItems.size()]);
        TTSAsyncTask speak = new TTSAsyncTask();
        speak.execute(passPlayItems);
    }

    /*
     * AsyncTask for TTS
     */

    private class TTSAsyncTask extends
            AsyncTask<TTSPlayItem, TTSPlayItem, String> {

        // WakeLock
        PowerManager pm;
        PowerManager.WakeLock wakeLock;

        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(TTSPlayItem... items) {
            pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My wakelook");

            wakeLock.acquire();

            for (int i = 0; i < items.length; i++) {
                TTSPlayItem[] progressList = new TTSPlayItem[1];
                progressList[0] = items[i];
                publishProgress(progressList);
                Log.i(TAG, "Play - locale: " + items[i].locale.toString()
                        + ", text: " + items[i].text);

                int treshold = 0;
                while (true) {
                    int result = mTts.setLanguage(items[i].locale);
                    Log.i(TAG, "Locale return: " + result);
                    if (result == 1)
                        break;
                    if (treshold == 100)
                        break;
                    treshold++;
                }

                mTts.speak(items[i].text, TextToSpeech.QUEUE_FLUSH, null);
                while (mTts.isSpeaking()) {
                    if (playing == false) {
                        mTts.stop();
                        return "Playback stopped.";
                    }
                }

                // wait
                android.os.SystemClock.sleep(1000);
            }
            playing = false;

            if (wakeLock.isHeld())
                wakeLock.release();

            return "Played list of " + items.length + " items.";
        }

        protected void onProgressUpdate(TTSPlayItem... result) {
        }

        protected void onPostExecute(String result) {
        }

    /*
     * TTS methods
     */

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == MY_DATA_CHECK_CODE) {
                if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                    // success, create the TTS instance
                    mTts = new TextToSpeech(this, this);
                } else {
                    // missing data, install it
                    Intent installIntent = new Intent();
                    installIntent
                            .setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installIntent);
                }
            }
        }

        public void shutdownTTS() {
            playing = false;
            // Don't forget to shutdown!
            if (mTts != null) {
                mTts.stop();
                mTts.shutdown();
            }
        }

        @Override
        public void onDestroy() {
            shutdownTTS();
            super.onDestroy();
        }

        public void onStop() {
            shutdownTTS();
            super.onStop();
        }

        public void onPause() {
            shutdownTTS();
            super.onPause();
        }
    }