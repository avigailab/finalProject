package com.example.avigail.lastproject;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by User on 1/25/2017.
 */

 class AsyncCall extends AsyncTask<String, Void, Void> {
    private static final String TAG = "AsyncCall";

    @Override
    protected Void doInBackground(String... params) {
        Log.i(TAG, "doInBackground");
        AppAdapter appAdapter=new AppAdapter();
        Log.e("call tts",appAdapter.callTTS());
        //getFahrenheit(celcius);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.i(TAG, "onPostExecute");
    }

    @Override
    protected void onPreExecute() {
        Log.i(TAG, "onPreExecute");
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        Log.i(TAG, "onProgressUpdate");
    }

}