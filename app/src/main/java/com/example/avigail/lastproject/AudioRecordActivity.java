package com.example.avigail.lastproject;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.LinearLayout;
import android.os.Environment;
import android.view.ViewGroup;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Context;
import android.util.Log;
import android.media.MediaRecorder;
import android.media.MediaPlayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.provider.MediaStore;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;



public class AudioRecordActivity extends AppCompatActivity
{
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_BPP = 16;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private static final String WRITE_EXTERNAL_STORAGE = "true";
    private static final String RECORD_AUDIO = "true";
    private static final String TAG = "AudioRecordActivity";
    Button buttonStart, buttonStop, buttonPlayLastRecordAudio,
            buttonStopPlayingRecording ;
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    Random random ;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";
    public static final int RequestPermissionCode = 1;
    private boolean permissionToRecordAccepted = false;
    private boolean permissionToWriteAccepted = false;
    private String [] permissions = {"android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"};
    MediaPlayer mediaPlayer ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);
        int requestCode = 200;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }

        buttonStart = (Button) findViewById(R.id.button);
        buttonStop = (Button) findViewById(R.id.button2);
        buttonPlayLastRecordAudio = (Button) findViewById(R.id.button3);
        buttonStopPlayingRecording = (Button)findViewById(R.id.button4);

        buttonStop.setEnabled(false);
        buttonPlayLastRecordAudio.setEnabled(false);
        buttonStopPlayingRecording.setEnabled(false);

        random = new Random();

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int bufferSizeInBytes = AudioRecord.getMinBufferSize( RECORDER_SAMPLERATE,
                        RECORDER_CHANNELS,
                        RECORDER_AUDIO_ENCODING
                );
                // Initialize Audio Recorder.
                AudioRecord audioRecorder = new AudioRecord( MediaRecorder.AudioSource.MIC,
                        RECORDER_SAMPLERATE,
                        RECORDER_CHANNELS,
                        RECORDER_AUDIO_ENCODING,
                        bufferSizeInBytes
                );

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

                    if( (temp >= 0 && temp <= 350) && recording == true )
                    //if(temp>140)
                    {
                        Log.i("TAG", "Save audio to file.");

                        // Save audio to file.
                       /* String filepath = Environment.getExternalStoragePublicDirectory("/audio").getPath();
                       //String filepath=getApplicationContext().getFilesDir()+"";
                                File file = new File(filepath,"AudioRecorder");
                        if( !file.exists() )
                            file.mkdirs();

                        String fn = file.getAbsolutePath() + "/aa.flac";*/
                       /* String path = Environment.getExternalStorageDirectory().getAbsolutePath();

                        File folder = new File(path + "/audioRecord");
                        if (!folder.exists()) {
                            folder.mkdir();
                        }
                        File file = new File(folder.getPath() + "/aa.wav");


                        String fn = file.getAbsolutePath() + "/aa.wav";*/
                        //String fn = file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".flac";
                        long totalAudioLen  = 0;
                        long totalDataLen   = totalAudioLen + 36;
                        long longSampleRate = RECORDER_SAMPLERATE;
                        int channels        = 2;
                        long byteRate       = RECORDER_BPP * RECORDER_SAMPLERATE * channels/8;
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

                       /* FileOutputStream out;
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
                        }*/
                        saveAudioFile(finalBuffer);

                        //*/
                        tempIndex++;
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
                /*if(true) {

                    AudioSavePathInDevice =
                            getApplicationContext().getFilesDir() + "/"+CreateRandomAudioFileName(5) +"_AudioRecording.3gp";

                    MediaRecorderReady();

                    try {
                        mediaRecorder.prepare();

                    } catch (IllegalStateException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    mediaRecorder.start();

                    buttonStart.setEnabled(false);
                    buttonStop.setEnabled(true);

                    Toast.makeText(AudioRecordActivity.this, "Recording started",
                            Toast.LENGTH_LONG).show();
                } else {
                    requestPermission();
                }

*/
                //Log.d("----","before call encode audio");
            //    String encodeFile = encodeAudio(getApplicationContext().getFilesDir()+"AudioRecorder/new.flac");
               // Log.i("ENCODE FILE",encodeFile.toString()+"");
              //  sendRecordToApi(encodeFile);
                //uploadImage();

            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(TAG, String.valueOf(mediaRecorder.getMaxAmplitude()));
                //Log.e(TAG, mediaRecorder.AudioEncoder);
                mediaRecorder.stop();
                buttonStop.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);

                Toast.makeText(AudioRecordActivity.this, "Recording Completed",
                        Toast.LENGTH_LONG).show();
            }
        });

        buttonPlayLastRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) throws IllegalArgumentException,
                    SecurityException, IllegalStateException {

                buttonStop.setEnabled(false);
                buttonStart.setEnabled(false);
                buttonStopPlayingRecording.setEnabled(true);
                AudioSavePathInDevice="/storage/emulated/0/TokuoroRecords/record.wav";
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(AudioSavePathInDevice);
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                mediaPlayer.start();
                Toast.makeText(AudioRecordActivity.this, "Recording Playing",
                        Toast.LENGTH_LONG).show();
            }
        });

        buttonStopPlayingRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonStop.setEnabled(false);
                buttonStart.setEnabled(true);
                buttonStopPlayingRecording.setEnabled(false);
                buttonPlayLastRecordAudio.setEnabled(true);

                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    MediaRecorderReady();
                }
            }
        });

    }
    private void saveAudioFile(byte[]finalBuffer){

        // this code for android M + , requesting special permission at runtime
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},23
            );

        }

        // this code is for saving to file directory
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();

        File folder = new File(path + "/TokuoroRecords");
        if (!folder.exists()) {
            folder.mkdir();
        }

        File file = new File(folder.getPath() + "/record.wav");
        FileOutputStream ostream;
        try {
            Log.d("create record",file.getAbsolutePath());
            file.createNewFile();
            ostream = new FileOutputStream(file);
            ostream.write(finalBuffer);
            ostream.flush();
            ostream.close();

            String realPath = file.getAbsolutePath();
            File f = new File(realPath);

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());

        }
    }
    public void MediaRecorderReady(){
        mediaRecorder=new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
    }

    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(AudioRecordActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
   public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length> 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(AudioRecordActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(AudioRecordActivity.this,RecordPermission+"----Permission Denied----"+StoragePermission,Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    private String encodeAudio(String selectedPath) {
        byte[] audioBytes;
        try {

            // Just to check file size.. Its is correct i-e; Not Zero
            //File audioFile = new File(selectedPath);
            //long fileSize = audioFile.length();
            String filepath=getApplicationContext().getFilesDir()+"";
            File file = new File(filepath,"AudioRecorder");
            //if( !file.exists() )
              //  file.mkdirs();

            String fn = file.getAbsolutePath() + "/new.flac";
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            FileInputStream fis = new FileInputStream(new File(fn));
            byte[] buf = new byte[1024];
            int n;
            while (-1 != (n = fis.read(buf)))
                baos.write(buf, 0, n);
            baos.close();
            audioBytes = baos.toByteArray();

            // Here goes the Base64 string
            String _audioBase64 = Base64.encodeToString(audioBytes, Base64.DEFAULT);
            Log.d("ENCODE FILE",_audioBase64);
            return _audioBase64;

        } catch (Exception e) {
            Log.e("audio encode execption","");
            //DiagnosticHelper.writeException(e);
        }
        return "";
    }
    public String getEncodeFile(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
    private void uploadImage(){
        //Showing the progress dialog
        String URL= "https://wili.tukuoro.com/tukwebservice/tukwebservice_app.asmx/ParseImmidiateSingleFromAudio";
        //final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                       // loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(getApplicationContext(), s , Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                       // loading.dismiss();
                        Log.d("volley error","error respones");
                        //Showing toast
//                        Toast.makeText(getApplicationContext(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String


                //Creating parameters
                Map<String,String> params = new Hashtable<String, String>();
                String filepath=getApplicationContext().getFilesDir()+"";
                File file = new File(filepath,"AudioRecorder");
                String fn = file.getAbsolutePath() + "/new.flac";
                String encodeFile = encodeAudio(fn);
                //Adding parameters
                params.put("audio",encodeFile);
                params.put("fieldName","date");
                params.put("fieldType","Any");
                params.put("language","en-US");
                params.put("clientId","68174861");
                params.put("serviceId","58469251");
                //returning parameters
                return params;
            }

        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void sendRecordToApi(String encodeFile) {

        Toast.makeText(this.getApplicationContext(), "on getArOb func =)",
                Toast.LENGTH_LONG).show();
        RequestQueue queue = Volley.newRequestQueue(this);

        String URL= "https://wili.tukuoro.com/tukwebservice/tukwebservice_app.asmx/ParseImmidiateSingleFromAudio";

        JSONObject params = new JSONObject();
        Log.d("ENCODE FILE",encodeFile.toString());

        try {

            params.put("audio",encodeFile);
            params.put("fieldName","date");
            params.put("fieldType","Any");
            params.put("language","en");
            params.put("clientId","68174861");
            params.put("serviceId","58469251");
            Log.d("on try===", String.valueOf(params));
        } catch (JSONException e) {
            e.printStackTrace();
        }
       /* StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("RESPONSE-----",response);

                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // mTextView.setText("That didn't work!");
                Log.e("Error","");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);*/
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
        }) ;/*{

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };*/
        queue.add(jsonObjReq);
    }

}
