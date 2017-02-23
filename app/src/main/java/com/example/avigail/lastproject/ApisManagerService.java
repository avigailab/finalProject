package com.example.avigail.lastproject;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static java.net.Proxy.Type.HTTP;

public class ApisManagerService extends Service {
    private static String TAG = "AudioRecordService";
    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    AudioRecord audioRecorder;
    int bufferSizeInBytes;
    public ApisManagerService() {
    }

    private static String LOG_TAG = "BoundService";
    private IBinder mBinder = new ApisManagerService.MyBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOG_TAG, "in onCreate");
        bufferSizeInBytes = AudioRecord.getMinBufferSize( RECORDER_SAMPLERATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING
        );
        // Initialize Audio Recorder.
        audioRecorder = new AudioRecord( MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING,
                bufferSizeInBytes
        );
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.v(LOG_TAG, "in onBind");
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v(LOG_TAG, "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v(LOG_TAG, "in onUnbind");
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "in onDestroy");
    }

    public void getTimestamp() {
        Log.d("ApisManager","on function!!!");
    }
    public void callGetLayoutsForUser(){
        RequestQueue queue = Volley.newRequestQueue(this);
        Log.d("on callApi func----","on callApi!");
        String URL= "https://wili.tukuoro.com/tukwebservice/tukwebservice_app.asmx/GetLayoutsForUser?tukLogin=orayrs@gmail.com&serviceId=58469251&clientId=68174861";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("request sucsses!!",response.toString());
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Error","");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    public void startRecordAudio(){
        // Start Recording.
        audioRecorder.startRecording();

        int numberOfReadBytes   = 0;
        byte audioBuffer[]      = new  byte[bufferSizeInBytes];
        boolean recording       = false;
        float tempFloatBuffer[] = new float[3];
        int tempIndex           = 0;
        int totalReadBytes      = 0;
        byte totalByteBuffer[]  = new byte[60 * 44100 * 2];


        // While data come from microphone.
        while( true )
        {
            float totalAbsValue = 0.0f;
            short sample        = 0;

            numberOfReadBytes = audioRecorder.read( audioBuffer, 0, bufferSizeInBytes );

            // Analyze Sound.
            for( int i=0; i<bufferSizeInBytes; i+=2 )
            {
                sample = (short)( (audioBuffer[i]) | audioBuffer[i + 1] << 8 );
                totalAbsValue += Math.abs( sample ) / (numberOfReadBytes/2);
            }

            // Analyze temp buffer.
            tempFloatBuffer[tempIndex%3] = totalAbsValue;
            float temp                   = 0.0f;
            for( int i=0; i<3; ++i )
                temp += tempFloatBuffer[i];
            Log.e("TEMP----", String.valueOf(temp));
            if( (temp >=0 && temp <= 350) && recording == false )
            {
                Log.i("TAG", "1");
                tempIndex++;
                continue;
            }

            if( temp > 350 && recording == false )
            {
                Log.i("TAG", "2");
                recording = true;
            }

            //if( (temp >= 0 && temp <= 350) && recording == true )
            if(temp>140)
            {
                Log.i("TAG", "Save audio to file.");
                String path = Environment.getExternalStorageDirectory().getAbsolutePath();

                File folder = new File(path + "/audioRecord");
                if (!folder.exists()) {
                    folder.mkdir();
                }
                File file = new File(folder.getPath() + "/aa.wav");


                String fn = file.getAbsolutePath();
                //String fn = file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".flac";
                long totalAudioLen  = 0;
                long totalDataLen   = totalAudioLen + 36;
                long longSampleRate = RECORDER_SAMPLERATE;
                int channels        = 1;
                long byteRate       = 4 * RECORDER_SAMPLERATE * channels/8;
                totalAudioLen       = totalReadBytes;
                totalDataLen        = totalAudioLen + 36;
                byte finalBuffer[]  = new byte[totalReadBytes + 44];

                finalBuffer[0] = 'R';  // RIFF/WAVE header
                finalBuffer[1] = 'I';
                finalBuffer[2] = 'F';
                finalBuffer[3] = 'F';
                finalBuffer[4] = (byte) (totalDataLen & 0xff);
                finalBuffer[5] = (byte) ((totalDataLen >> 8) & 0xff);
                finalBuffer[6] = (byte) ((totalDataLen >> 16) & 0xff);
                finalBuffer[7] = (byte) ((totalDataLen >> 24) & 0xff);
                finalBuffer[8] = 'W';
                finalBuffer[9] = 'A';
                finalBuffer[10] = 'V';
                finalBuffer[11] = 'E';
                finalBuffer[12] = 'f';  // 'fmt ' chunk
                finalBuffer[13] = 'm';
                finalBuffer[14] = 't';
                finalBuffer[15] = ' ';
                finalBuffer[16] = 16;  // 4 bytes: size of 'fmt ' chunk
                finalBuffer[17] = 0;
                finalBuffer[18] = 0;
                finalBuffer[19] = 0;
                finalBuffer[20] = 1;  // format = 1
                finalBuffer[21] = 0;
                finalBuffer[22] = (byte) channels;
                finalBuffer[23] = 0;
                finalBuffer[24] = (byte) (longSampleRate & 0xff);
                finalBuffer[25] = (byte) ((longSampleRate >> 8) & 0xff);
                finalBuffer[26] = (byte) ((longSampleRate >> 16) & 0xff);
                finalBuffer[27] = (byte) ((longSampleRate >> 24) & 0xff);
                finalBuffer[28] = (byte) (byteRate & 0xff);
                finalBuffer[29] = (byte) ((byteRate >> 8) & 0xff);
                finalBuffer[30] = (byte) ((byteRate >> 16) & 0xff);
                finalBuffer[31] = (byte) ((byteRate >> 24) & 0xff);
                finalBuffer[32] = (byte) (2 * 16 / 8);  // block align
                finalBuffer[33] = 0;
                finalBuffer[34] = 4;  // bits per sample
                finalBuffer[35] = 0;
                finalBuffer[36] = 'd';
                finalBuffer[37] = 'a';
                finalBuffer[38] = 't';
                finalBuffer[39] = 'a';
                finalBuffer[40] = (byte) (totalAudioLen & 0xff);
                finalBuffer[41] = (byte) ((totalAudioLen >> 8) & 0xff);
                finalBuffer[42] = (byte) ((totalAudioLen >> 16) & 0xff);
                finalBuffer[43] = (byte) ((totalAudioLen >> 24) & 0xff);

                for( int i=0; i<totalReadBytes; ++i )
                    finalBuffer[44+i] = totalByteBuffer[i];

                FileOutputStream out;
                try {

                    out = new FileOutputStream(fn);
                    try {
                        out.write(finalBuffer);
                        out.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } catch (FileNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                tempIndex++;
                //send record to api
                //callApi();
                break;

            }

            // -> Recording sound here.
            Log.i( "TAG", "Recording Sound." );
            for( int i=0; i<numberOfReadBytes; i++ )
                totalByteBuffer[totalReadBytes + i] = audioBuffer[i];
            totalReadBytes += numberOfReadBytes;
            //*/

            tempIndex++;

        }
        //Log.d("----","before call encode audio");
        //String encodeFile = encodeAudio(getApplicationContext().getFilesDir()+"AudioRecorder/new.flac");
        // Log.i("ENCODE FILE",encodeFile.toString()+"");
        //sendRecordToApi(encodeFile);
    }
    public void sendXml(){
        HttpPost httppost = new HttpPost("https://wili.tukuoro.com/tukwebservice/tukwebservice_app.asmx");
        String SOAPRequestXML="";
        StringEntity se = null;
        try {
            se = new StringEntity(SOAPRequestXML,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        se.setContentType("text/xml");
        httppost.setHeader("Content-Type","application/soap+xml;charset=UTF-8");
        httppost.setEntity(se);

        HttpClient httpclient = new DefaultHttpClient();
        BasicHttpResponse httpResponse =
                null;
        try {
            httpResponse = (BasicHttpResponse) httpclient.execute(httppost);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e("HTTPStatus",httpResponse.getStatusLine().toString());
    }
    public void sendRecordToApi(String encodeFile) {

        Toast.makeText(this.getApplicationContext(), "on getArOb func =)",
                Toast.LENGTH_LONG).show();
        RequestQueue queue = Volley.newRequestQueue(this);

        String URL= "https://wili.tukuoro.com/tukwebservice/tukwebservice_app.asmx/ParseImmidiateSingleFromAudio";

        JSONObject params = new JSONObject();

        try {

            params.put("audio",String.valueOf(encodeFile));
            params.put("fieldName","date");
            params.put("fieldType","Any");
            params.put("language","en");
            params.put("clientId","68174861");
            params.put("serviceId","58469251");
            Log.d("on try===", String.valueOf(params.get("audio")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                URL, params,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        //pDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                // pDialog.hide();
            }
        }) ;
        queue.add(jsonObjReq);
    }
    public class MyBinder extends Binder {
        ApisManagerService getService() {
            return ApisManagerService.this;
        }
    }
}

