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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.HashMap;

public class LayoutActivity extends Activity implements TextToSpeech.OnInitListener,
        OnUtteranceCompletedListener{
    //bound service variables
    /*AudioRecordService mBoundService;
    boolean mServiceBound = false;*/
    ApisManagerService apiBoundService;
    boolean apiServiceBound = false;

    private static final String TAG = "layuot activity";
    private final int SPEECH_RECOGNITION_CODE = 1;
    private AppAdapter appAdapter;
    private TextView layoutTitle;
    private String currentFiledName;
    private TextToSpeech textToSpeech;

    private int uttCount = 0;
    private int lastUtterance = -1;
    private HashMap<String, String> params = new HashMap<String, String>();
    private static final int REQ_TTS_STATUS_CHECK = 0;
    private TextToSpeech mTts;
    Layout currentLayout;
    private ListView messagesContainer;
    private LayoutAdapter adapter;
    int fieldIndex=0,currentAnswerId=100;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        adapter = new LayoutAdapter(LayoutActivity.this, new ArrayList<LayoutMessage>());
        messagesContainer.setAdapter(adapter);
       /* currentLayout =new Layout(1,"asasas");
        ArrayList fields = new ArrayList<Field>();
        for (int j = 0; j < 3; j++) {
            Field field = new Field(j, "hi   "+j, false, 1, "int");
            fields.add(j, field);
        }
        currentLayout.fields=fields;*/
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

        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        /*Intent intent = new Intent(this, AudioRecordService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);*/
        Intent intent = new Intent(this, ApisManagerService.class);
        startService(intent);
        bindService(intent, apiServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (apiServiceBound) {
            unbindService(apiServiceConnection);
            apiServiceBound = false;
        }
    }
    private ServiceConnection apiServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            apiServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            /*RecorderBinder myBinder = (AudioRecordService.RecorderBinder) service;
            mBoundService = myBinder.getService();
            mServiceBound = true;*/
            ApisManagerService.MyBinder myBinder = (ApisManagerService.MyBinder) service;
            apiBoundService = myBinder.getService();
            apiServiceBound = true;
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
            currentFiledName=currentLayout.fields.get(fieldIndex).filedName;
            generateLeftMessage(currentFiledName,fieldIndex);
            mTts.speak(currentFiledName,TextToSpeech.QUEUE_ADD, params);
            fieldIndex++;

        }
    }

    private void callApi(){
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

    public void generateLeftMessage(String body, int id){
        LayoutMessage layoutMessage = new LayoutMessage();
        layoutMessage.setId(id);//dummy
        layoutMessage.setMessage(body);
        layoutMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        layoutMessage.setMe(true);
        displayMessage(layoutMessage);

    }
    public void generateRightMessage(String body, int id){
        LayoutMessage layoutMessage = new LayoutMessage();
        layoutMessage.setId(id);
        layoutMessage.setMe(false);
        layoutMessage.setMessage(body);
        layoutMessage.setDate(DateFormat.getDateTimeInstance().format(new Date()));
        displayMessage(layoutMessage);

    }
    public void displayMessage(LayoutMessage message) {
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
            if (apiServiceBound) {
                Log.v(TAG, "Start RECORD!!!!");
                apiBoundService.startRecordAudio();
                //apiBoundService.callGetLayoutsForUser();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            SendSoap myRequest = new SendSoap();
            myRequest.execute();
            Log.d("after","post execute");
            //set answer bubble text
            generateRightMessage("result",currentAnswerId);
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

