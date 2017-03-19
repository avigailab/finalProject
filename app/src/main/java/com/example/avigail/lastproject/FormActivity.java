package com.example.avigail.lastproject;

import android.app.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;

import org.ksoap2.serialization.SoapObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.HashMap;

public class FormActivity extends Activity implements TextToSpeech.OnInitListener,
        OnUtteranceCompletedListener{
    //bound service variables
    /*AudioRecordService mBoundService;
    boolean mServiceBound = false;*/
    AudioRecordService apiBoundService;
    boolean recordServiceBound = false;

    private static final String TAG = "layuot activity";
    private final int SPEECH_RECOGNITION_CODE = 1;
    private AppAdapter appAdapter;
    private TextView layoutTitle;
    private String currentFieldName;
    private TextToSpeech textToSpeech;

    Button saveForm, sendForm;

    private int uttCount = 0;
    private int lastUtterance = -1;
    private HashMap<String, String> params = new HashMap<String, String>();
    private static final int REQ_TTS_STATUS_CHECK = 0;
    private TextToSpeech mTts;
    Layout currentLayout;
    private ListView messagesContainer;
    private FormAdapter adapter;
    int fieldIndex=0,currentAnswerId=100;
    String currentFieldType ="";
    String finalRespone="Defult Respone";
    Activity activity=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        adapter = new FormAdapter(FormActivity.this, new ArrayList<FormMessage>());
        messagesContainer.setAdapter(adapter);
        saveForm =(Button)findViewById(R.id.save);
        sendForm =(Button)findViewById(R.id.send);

        appAdapter=new AppAdapter();
        layoutTitle = (TextView) findViewById(R.id.layoutTitle);
        // Check to be sure that TTS exists and is okay to use
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, REQ_TTS_STATUS_CHECK);

        Button speakbtn = (Button) findViewById(R.id.speak);
        speakbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //read field name
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
                    Log.v(TAG, "tts is installed okay");
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

    }
    @Override
    public void onInit(int status) {

        if( status == TextToSpeech.SUCCESS) {
            mTts.setOnUtteranceCompletedListener(this);
            int result = mTts.setLanguage(Locale.US);

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        /*Intent intent = new Intent(this, AudioRecordService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);*/
        Intent intent = new Intent(this, AudioRecordService.class);
        startService(intent);
        bindService(intent, recordServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (recordServiceBound) {
            unbindService(recordServiceConnection);
            recordServiceBound = false;
        }
    }
    private ServiceConnection recordServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            recordServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /*RecorderBinder myBinder = (AudioRecordService.RecorderBinder) service;
            mBoundService = myBinder.getService();
            mServiceBound = true;*/
            AudioRecordService.MyBinder myBinder = (AudioRecordService.MyBinder) service;
            apiBoundService = myBinder.getService();
            recordServiceBound = true;
        }
    };
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
            currentFieldName =currentLayout.fields.get(fieldIndex).filedName;
            generateLeftMessage(currentFieldName,fieldIndex);
            mTts.speak(currentFieldName,TextToSpeech.QUEUE_ADD, params);
            fieldIndex++;

        }
        else{

            saveForm.setVisibility(View.VISIBLE);
            sendForm.setVisibility(View.VISIBLE);
            WaitingForms waitingForms=new WaitingForms();
            Log.i(TAG, String.valueOf(currentLayout.fields.get(0).filedAnswer));
            //waitingForms.addForm(currentLayout);
            Log.i(TAG, waitingForms.getWaitingForms().get(0).layoutName);

        }
    }


    public void generateLeftMessage(String body, int id){
        FormMessage formMessage = new FormMessage();
        formMessage.setId(id);//dummy
        formMessage.setMessage(body);
        formMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        formMessage.setMe(true);
        displayMessage(formMessage);

    }
    public void generateRightMessage(String body, int id){
        FormMessage formMessage = new FormMessage();
        formMessage.setId(id);
        formMessage.setMe(false);
        formMessage.setMessage(body);
        formMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        displayMessage(formMessage);

    }
    public void displayMessage(FormMessage message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }
    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }
    class AsyncCall extends AsyncTask<String, Void, Void> {
        private static final String TAG = "AsyncCall";

        @Override
        protected Void doInBackground(String... params) {

            Log.i(TAG, "doInBackground");
            if (recordServiceBound) {
                Log.v(TAG, "Start RECORD!!!!");
                apiBoundService.startRecordAudio();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            ProgressDialog progDailog = ProgressDialog.show(activity, "Process ", "please wait....", true, true);
            currentFieldName = currentLayout.fields.get(fieldIndex-1).filedName;
            currentFieldType = currentLayout.fields.get(fieldIndex-1).dataType;

            SendRecordSoap myRequest = new SendRecordSoap(currentFieldName, currentFieldType,"en");
            try {

                SoapObject respone = (SoapObject) myRequest.execute().get();
                progDailog.dismiss();
                if(respone!=null) {
                    SoapObject respone_1 = (SoapObject) respone.getProperty(1);
                    SoapObject respone_2 = (SoapObject) respone_1.getProperty(0);
                    finalRespone=String.valueOf(respone_2.getProperty(0));
                    //Log.i(TAG, String.valueOf(respone_2.getProperty(0)));
                }
                Log.d("after","post execute");
                //set answer bubble text
                generateRightMessage(finalRespone,currentAnswerId);
                currentLayout.fields.get(fieldIndex-1).setFiledAnswer(finalRespone);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            currentAnswerId++;
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
