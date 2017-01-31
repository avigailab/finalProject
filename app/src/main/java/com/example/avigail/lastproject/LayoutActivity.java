package com.example.avigail.lastproject;

import android.app.*;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.HashMap;

import com.example.avigail.lastproject.AudioRecordService.MyBinder;

public class LayoutActivity extends Activity implements TextToSpeech.OnInitListener,
        OnUtteranceCompletedListener{
    //bound service variables
    AudioRecordService mBoundService;
    boolean mServiceBound = false;

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
    private HashMap<String, String> params = new HashMap<String, String>();
    private static final int REQ_TTS_STATUS_CHECK = 0;
    private TextToSpeech mTts;

    Layout currentLayout;
    TextView answerMessage;
    int leftFieldPos=0,rightFieldPos=1,fieldIndex=0,currentAnswerId=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        appAdapter=new AppAdapter();
        layoutTitle = (TextView) findViewById(R.id.layoutTitle);
        layout = (RelativeLayout) findViewById(R.id.messages);
        leftMessage = (TextView) findViewById(R.id.leftMessage);
        rightMessage = (TextView) findViewById(R.id.rightMessage);
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
        Intent intent = new Intent(this, AudioRecordService.class);
        startService(intent);
        bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mServiceBound) {
            unbindService(mServiceConnection);
            mServiceBound = false;
        }
    }
    private ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyBinder myBinder = (MyBinder) service;
            mBoundService = myBinder.getService();
            mServiceBound = true;
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
            generateLeftMessage(currentFiledName,fieldIndex,leftFieldPos);
            mTts.speak(currentFiledName,TextToSpeech.QUEUE_ADD, params);
            generateRightMessage("...",fieldIndex+100,rightFieldPos);
            fieldIndex++;
            leftFieldPos+=2;
            rightFieldPos+=2;

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

    public void generateLeftMessage(String body, int id, int pos){
        TextView rowTextView = new TextView(getApplicationContext());
        // set some properties of rowTextView or something
        rowTextView.setText(body);
        rowTextView.setId(id);
        rowTextView.setPadding(50, 15, 50, 15);
        rowTextView.setY(150 * pos);
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(100);
        //gd.setStroke(3, 0xFF000000);
        // Changes this drawbale to use a single color instead of a gradient
        gd.setColor(Color.parseColor("#39B57B"));
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
        rowTextView.setPadding(50, 15, 50, 15);
        rowTextView.setY(150 * pos);
        rowTextView.setTextColor(Color.parseColor("#000000"));
        // Changes this drawbale to use a single color instead of a gradient
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(100);
        gd.setColor(Color.parseColor("#E7E7E4"));
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
    class AsyncCall extends AsyncTask<String, Void, Void> {
        private static final String TAG = "AsyncCall";

        @Override
        protected Void doInBackground(String... params) {

            Log.i(TAG, "doInBackground");
            if (mServiceBound) {
                Log.d("---------","hiii");
                Log.v(TAG, "Start RECORD!!!!");
                mBoundService.startRecordAudio();
            }
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

