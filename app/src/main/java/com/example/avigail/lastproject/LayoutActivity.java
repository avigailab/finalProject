package com.example.avigail.lastproject;

import android.app.*;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import java.util.HashMap;

import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;

public class LayoutActivity extends Activity implements TextToSpeech.OnInitListener,
        OnUtteranceCompletedListener{
    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final String TAG = "layuot activity";
    private final int SPEECH_RECOGNITION_CODE = 1;
    private AppAdapter appAdapter;
    private TextView layoutTitle;
    private RelativeLayout layout;
    private TextView leftMessage;
    private  TextView rightMessage;
    private String currentFiledName;
    private TextToSpeech textToSpeech;

    private int uttCount = 0;
    private int lastUtterance = -1;
    private String words;
    private HashMap<String, String> params = new HashMap<String, String>();
    private static final int REQ_TTS_STATUS_CHECK = 0;
    private TextToSpeech mTts;
    AudioRecord audioRecorder;
    int bufferSizeInBytes;
    Layout currentLayout;
    TextView answerMessage;
    int leftFieldPos=0,rightFieldPos=1,fieldIndex=0,currentAnswerId=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
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
        appAdapter=new AppAdapter();
        layoutTitle = (TextView) findViewById(R.id.layoutTitle);
        layout = (RelativeLayout) findViewById(R.id.messages);
        leftMessage = (TextView) findViewById(R.id.leftMessage);
        rightMessage = (TextView) findViewById(R.id.rightMessage);
        // Check to be sure that TTS exists and is okay to use
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, REQ_TTS_STATUS_CHECK);
           /* Intent intent = new Intent(Intent.ACTION_SYNC, null, this.getApplicationContext(), SpeechService.class);
            intent.putExtra("spoken_txt", "hello world");
            this.getApplicationContext().startService(intent);

        }*/
        Button speakbtn = (Button) findViewById(R.id.speak);
        speakbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //read field name
                Log.d(TAG,"on click event");
                view.setVisibility(View.GONE);
                currentLayout = (Layout) getIntent().getSerializableExtra("LAYOUT");
                Log.e(TAG, currentLayout.layoutName);
                doSpeak();
            }
        });

    }
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        if (requestCode == REQ_TTS_STATUS_CHECK) {
            switch (resultCode) {
                case TextToSpeech.Engine.CHECK_VOICE_DATA_PASS:
                    // TTS is up and running
                    mTts = new TextToSpeech(this, this);
                    Log.v(TAG, "Pico is installed okay");
                    break;
                case TextToSpeech.Engine.CHECK_VOICE_DATA_BAD_DATA:
                case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_DATA:
                case TextToSpeech.Engine.CHECK_VOICE_DATA_MISSING_VOLUME:
                    // missing data, install it
                    Log.v(TAG, "Need language stuff: " + resultCode);
                    Intent installIntent = new Intent();
                    installIntent.setAction(
                            TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                    startActivity(installIntent);
                    break;
                case TextToSpeech.Engine.CHECK_VOICE_DATA_FAIL:
                default:
                    Log.e(TAG, "Got a failure. TTS not available");
            }
        }
        else {
            // Got something else
        }
    }
    @Override
    public void onInit(int status) {
        Log.d("oninit--------","on init func");
        if( status == TextToSpeech.SUCCESS) {
            mTts.setOnUtteranceCompletedListener(this);
            int result = mTts.setLanguage(Locale.US);

           /* if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                doSpeak();
            }*/
        }
    }
    @Override
    public void onPause()
    {
        super.onPause();
        // if we're losing focus, stop talking
        if( mTts != null)
            mTts.stop();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        mTts.shutdown();
    }
    @Override
    public void onUtteranceCompleted(String uttId) {
        Log.v(TAG, "Got completed message for uttId: " + uttId);
        lastUtterance = Integer.parseInt(uttId);
        try {
           new AsyncCall().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

    }

    public void doSpeak() {
        Log.d("doSpeak----","before while");
        //read all Layout fields
        if(fieldIndex < currentLayout.fields.size()) {
            params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,
                    String.valueOf(uttCount++));
            Log.d("doSpeak","before tts.speak");
            currentFiledName=currentLayout.fields.get(fieldIndex).filedName;
            generateLeftMessage(currentFiledName,fieldIndex,leftFieldPos);
            mTts.speak(currentFiledName,TextToSpeech.QUEUE_ADD, params);
            generateRightMessage("...",fieldIndex+100,rightFieldPos);
            fieldIndex++;
            leftFieldPos+=2;
            rightFieldPos+=2;



            //callApi();
        }
    }

    private void callApi(){
        RequestQueue queue = Volley.newRequestQueue(this);

        // final ListView listView = (ListView) findViewById(R.id.layoutsList);
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
                // mTextView.setText("That didn't work!");
                Log.e("Error","");
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void generateLeftMessage(String body, int id, int pos){
        TextView rowTextView = new TextView(getApplicationContext());
        // set some properties of rowTextView or something
        rowTextView.setText(body);
        rowTextView.setId(id);
        rowTextView.setPadding(50, 35, 50, 10);
        rowTextView.setY(150 * pos);
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(100);
        //gd.setStroke(3, 0xFF000000);
        gd.setColor(Color.parseColor("#39B57B")); // Changes this drawbale to use a single color instead of a gradient
        rowTextView.setBackgroundDrawable(gd);
        rowTextView.setTextSize(22);
        rowTextView.setMinimumWidth(300);
        //add wrap content property and align to text view
        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeLayoutParams.setMargins(30,0,0,0);
        layout.addView(rowTextView,relativeLayoutParams);

    }
    public void generateRightMessage(CharSequence body, int id, int pos){
        TextView rowTextView = new TextView(getApplicationContext());
        // set some properties of rowTextView
        rowTextView.setText(body);
        rowTextView.setId(id);
        rowTextView.setPadding(50, 35, 50, 10);
        rowTextView.setY(150 * pos);
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(100);
        gd.setColor(Color.parseColor("#E7E7E4")); // Changes this drawbale to use a single color instead of a gradient
        rowTextView.setBackgroundDrawable(gd);
        rowTextView.setTextSize(22);
        //rowTextView.setGravity(Gravity.LEFT);
        //add wrap content property and align to text view
        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        relativeLayoutParams.setMargins(0,0,30,0);
        layout.addView(rowTextView,relativeLayoutParams);

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

            if( (temp >= 0 && temp <= 350) && recording == true )
            //if(temp>140)
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
        //Log.d("----","before call encode audio");
        //String encodeFile = encodeAudio(getApplicationContext().getFilesDir()+"AudioRecorder/new.flac");
        // Log.i("ENCODE FILE",encodeFile.toString()+"");
        //sendRecordToApi(encodeFile);
    }

    public void callTTSService(){
        this.startService(new Intent(this, SpeechService.class));
    }

    class AsyncCall extends AsyncTask<String, Void, Void> {
        private static final String TAG = "AsyncCall";

        @Override
        protected Void doInBackground(String... params) {

            Log.i(TAG, "doInBackground");
            startRecordAudio();
            Log.v(TAG, "Start RECORD!!!!");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //set answer bubble text
            answerMessage = (TextView) findViewById(currentAnswerId+100);
            if(answerMessage !=null)
                answerMessage.setText("record finish "+currentAnswerId+100);
            currentAnswerId ++;

            Log.i(TAG, "onPostExecute");
            doSpeak();
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



}

