package com.example.avigail.lastproject;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

//import android.sax.Element;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class LayoutActivity extends Activity {

    private static final String TAG = "layuot activity";
    private final int SPEECH_RECOGNITION_CODE = 1;
    AppAdapter appAdapter;
    TextView layoutTitle;
    RelativeLayout layout;
    TextView leftMessage;
    TextView rightMessage;
    String currentFiledName;
    TextToSpeech t1;
    int k=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        appAdapter=new AppAdapter();
        //linearLayout=(LinearLayout)findViewById(R.id.fieldsList);
        layoutTitle = (TextView) findViewById(R.id.layoutTitle);
        layout = (RelativeLayout) findViewById(R.id.messages);
        leftMessage = (TextView) findViewById(R.id.leftMessage);
        rightMessage = (TextView) findViewById(R.id.rightMessage);
        /*for (int j = 0; j < 30; j++) {
            if(j%2==0)
                makeLeftMessage(j + "!!!!!!!!!!!!!",j);
            else
                makeRightMessage(j + "!!!!!!!!!!!!!",j);
        }*/
        getLayoutForUser();
    }
    private void getLayoutForUser() {
        Toast.makeText(this.getApplicationContext(), "on getArOb func =)",
                Toast.LENGTH_LONG).show();
        RequestQueue queue = Volley.newRequestQueue(this);

        // final ListView listView = (ListView) findViewById(R.id.layoutsList);

        String URL= "https://wili.tukuoro.com/tukwebservice/tukwebservice_app.asmx/GetLayoutsForUser?tukLogin=orayrs@gmail.com&serviceId=58469251&clientId=68174861";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        ArrayList<Layout> arrayOfLayouts = appAdapter.getObjectsLayoutsForUser(response);
                        final ArrayList<String> stringArray = new ArrayList<String>();
                        for (int i = 0; i < 1; i++) {
                            layoutTitle.setText(arrayOfLayouts.get(i).layoutName);
                            ArrayList<Field> currentFileds = arrayOfLayouts.get(i).fileds;
                            //display
                            for (int j = 0,k=0; j < currentFileds.size() && k< currentFileds.size()*2; j++,k+=2) {
                                currentFiledName = currentFileds.get(j).filedName;
                                Toast.makeText(getApplicationContext(), currentFiledName,Toast.LENGTH_SHORT).show();
                                makeLeftMessage(currentFiledName,k);

                                try {
                                    //Create instance for AsyncCallWS ,execute and wait until it done
                                    //Log.e("###","before call");
                                    new AsyncCall().execute().get();
                                    //Log.e("###","after call");




                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                }



                                //Intent myIntent = new Intent(LayoutActivity.this, SpeechService.class);
                                //myIntent.putExtra("WORD", currentFiledName);
                               // startService(myIntent);
                               /* Intent intent = new Intent(LayoutActivity.this, AudioRecordService.class);
                                startService(intent);*/
                               /* Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        //startSpeechToText(view.getId());

                                                Intent intent = new Intent(LayoutActivity.this, AudioRecordService.class);
                                                startService(intent);
                                            }
                                        }, 5000);*/


                            /*
                                rowTextView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(final View view) {
                                        Log.e("View", String.valueOf(view.getTag()));
                                        //Toast.makeText(getApplicationContext(), "Name",Toast.LENGTH_SHORT).show();
                                        Intent myIntent = new Intent(LayoutActivity.this, SpeechService.class);
                                        myIntent.putExtra("WORD", currentFiledName);
                                        startService(myIntent);
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                //startSpeechToText(view.getId());
                                                Intent layoutIntent= new Intent(view.getContext(),AudioRecordActivity.class);
                                                startActivity(layoutIntent);
                                                Intent intent = new Intent(view.getContext(), AudioRecordService.class);
                                                startService(intent);
                                            }
                                        }, 5000);

                                    }
                                });
                                // add the textview to the linearlayout
                                linearLayout.addView(rowTextView);
                              //  linearLayout.addView(answerTextView);
                            */
                            }
                        }
                    }

                       /* ArrayList<String> stringArray = appAdapter.displayFormLayout(arrayOfLayouts);*
                        // Define a new Adapter
                        // First parameter - Context
                        // Second parameter - Layout for the row
                        // Third parameter - ID of the TextView to which the data is written
                        // Forth - the Array of data
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.list_row_layout_even, R.id.text, stringArray);
                        // Assign adapter to ListView
                        listView.setAdapter(adapter);
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position,
                                                    long id) {
                               Log.e("View", String.valueOf(view.getTag()));
                            }
                        });
                        }*/
                       /* mTextView.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                //read field name
                                Log.d(TAG,"on click event");

                                //record from user
                                startSpeechToText();

                            }
                        });


                    }*/
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
    private void startSpeechToText(int id) {

        //Timer
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
       // intent.putExtra("FIELD_ID", id+100);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                // SharedPreferences
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String text = result.get(0);
                    Log.v("------->>>>",result.toString());

                    Toast.makeText(getApplicationContext(),
                            " After STT "+text,
                            Toast.LENGTH_SHORT).show();
                    RequestQueue queue = Volley.newRequestQueue(this);
                    //final String URL = "";
                    String afterDecode="";

                    try {
                        afterDecode = URLEncoder.encode(text, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //int fieldId = data.getIntExtra("FIELD_ID",1);
                    /*Toast.makeText(getApplicationContext(),
                            " After Algorithm "+fieldId,
                            Toast.LENGTH_SHORT).show();*/

                    final TextView answer = (TextView) findViewById(getResources().getIdentifier("answer", "id", getPackageName()));
                    answer.setText(text);

                    String URL= "https://wili.tukuoro.com/tukwebservice/tukwebservice_app.asmx/ParseImmidiateSingle?fieldName=date&fieldType=FreeTextNumeric&possibleValues=string&possibleValues=string&userValues="+afterDecode+"&clientId=68174861&serviceId=58469251";
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    Log.e("Response is",response);

                                    // mTextView.setText(response);

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e("That didn't work!",error.toString());
                            // mTextView.setText("Error");
                        }
                    });
                    queue.add(stringRequest);
// add the request object to the queue to be executed
                    //  ApplicationController.getInstance().addToRequestQueue(req);

                    AppAdapter appAdapter= new AppAdapter();
                    String newWord=appAdapter.bestFive(text);

                    Toast.makeText(getApplicationContext(),
                            " After Algorithm "+text,
                            Toast.LENGTH_SHORT).show();


                }
                break;
            }

        }
    }
    public void makeLeftMessage(String body,int id){
        TextView rowTextView = new TextView(getApplicationContext());
        // set some properties of rowTextView or something
        rowTextView.setText(body);
        rowTextView.setId(id);
        rowTextView.setPadding(50, 35, 50, 10);
        rowTextView.setY(150 * id);
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
    public void makeRightMessage(CharSequence body, int id){
        TextView rowTextView = new TextView(getApplicationContext());
        // set some properties of rowTextView
        rowTextView.setText(body);
        rowTextView.setId(id);
        rowTextView.setPadding(50, 35, 50, 10);
        rowTextView.setY(150 * id);
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
    public void callTTS(){
      /*  t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = t1.setLanguage(Locale.US);
                }
            }
        });
        t1.speak(currentFiledName, TextToSpeech.QUEUE_FLUSH, null);*/
        RequestQueue queue = Volley.newRequestQueue(this);
        //final String URL = "";
        String afterDecode="";

        try {
            afterDecode = URLEncoder.encode("hi", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //int fieldId = data.getIntExtra("FIELD_ID",1);
                    /*Toast.makeText(getApplicationContext(),
                            " After Algorithm "+fieldId,
                            Toast.LENGTH_SHORT).show();*/

        //final TextView answer = (TextView) findViewById(getResources().getIdentifier("answer", "id", getPackageName()));
       // Log.e("===>","!!")
        String URL= "https://wili.tukuoro.com/tukwebservice/tukwebservice_app.asmx/ParseImmidiateSingle?fieldName=date&fieldType=FreeTextNumeric&possibleValues=string&possibleValues=string&userValues="+afterDecode+"&clientId=68174861&serviceId=58469251";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.e("Response is",response);

                        // mTextView.setText(response);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("That didn't work!",error.toString());
                // mTextView.setText("Error");
            }
        });
        queue.add(stringRequest);
// add the request object to the queue to be executed
    }
    class AsyncCall extends AsyncTask<String, Void, Void> {
        private static final String TAG = "AsyncCall";

        @Override
        protected Void doInBackground(String... params) {

            Log.i(TAG, "doInBackground");
            Log.e("call tts","==");
            callTTS();
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.i(TAG, "onPostExecute");
            makeRightMessage("!!!!",k+1);
            k+=2;
            Log.e("###","after makeRightMessage");
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

