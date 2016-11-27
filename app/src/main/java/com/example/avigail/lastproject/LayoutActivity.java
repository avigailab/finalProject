package com.example.avigail.lastproject;

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

public class LayoutActivity extends Activity {

    private static final String TAG = "layuot activity";
    private final int SPEECH_RECOGNITION_CODE = 1;
    AppAdapter appAdapter;
    LinearLayout linearLayout;
    TextView layoutTitle;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        appAdapter=new AppAdapter();
        linearLayout=(LinearLayout)findViewById(R.id.fieldsList);
        layoutTitle = (TextView) findViewById(R.id.layoutTitle);
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
                            for (int j = 0; j < currentFileds.size(); j++) {

                                final String currentFiledName = currentFileds.get(j).filedName;
                                final TextView rowTextView = new TextView(getApplicationContext());
                                //final TextView rowTextView = (TextView) findViewById(R.id.text3);

                                // set some properties of rowTextView or something
                                rowTextView.setText(currentFiledName);
                                rowTextView.setId(j);
                                rowTextView.setTextAppearance(getApplicationContext(),R.style.leftField);
                                rowTextView.setPadding(50, 10, 50, 10);

                                // Gets linearlayout
                                LinearLayout layout = (LinearLayout)findViewById(R.id.myLayout);
                                // Gets the layout params that will allow you to resize the layout
                                ViewGroup.LayoutParams params = layout.getLayoutParams();

                                // Changes the height and width to the specified *pixels*
                                /*params.height = 100;
                                params.width = 100;*/
                                //params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                                layout.setLayoutParams(params);
                                rowTextView.setY(30*j);
                                GradientDrawable gd = new GradientDrawable();
                                gd.setColor(Color.parseColor("#39B57B")); // Changes this drawbale to use a single color instead of a gradient
                                gd.setCornerRadius(5);
                                gd.setStroke(1, 0xFF000000);
                                rowTextView.setBackgroundDrawable(gd);


                                //for answer
                                /*final TextView answerTextView = new TextView(getApplicationContext());
                                //final TextView rowTextView = (TextView) findViewById(R.id.text3);

                                // set some properties of rowTextView or something
                                answerTextView.setId(j+100);
                                answerTextView.setTextAppearance(getApplicationContext(),R.style.leftField);
                                answerTextView.setPadding(50, 10, 50, 10);
                                answerTextView.setY(30*j);
                                GradientDrawable ansGd = new GradientDrawable();
                                ansGd.setColor(Color.parseColor("#64BD68")); // Changes this drawbale to use a single color instead of a gradient
                                ansGd.setCornerRadius(5);
                                ansGd.setStroke(1, 0xFF000000);
                                answerTextView.setBackgroundDrawable(ansGd);
*/
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
                                                startSpeechToText(view.getId());
                                            }
                                        }, 5000);

                                    }
                                });
                                // add the textview to the linearlayout
                                linearLayout.addView(rowTextView);
                              //  linearLayout.addView(answerTextView);

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
                                Toast.makeText(getApplicationContext(), currentFiled,Toast.LENGTH_SHORT).show();
                                textToSpeech.speak(currentFiled, TextToSpeech.QUEUE_FLUSH, null);
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

        /*final TextView mTextView = (TextView) findViewById(R.id.text);
        mTextView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //read field name
                Log.d(TAG,"on click event");




               /* Intent myIntent = new Intent(MainActivity.this, TTSActivity.class);
                myIntent.putExtra("key", "Please Enter Name"); //Optional parameters
                MainActivity.this.startActivity(myIntent);*/

        //  TTSActivity tts = new TTSActivity();
        // tts.speak("Please Enter Name");
        //textToSpeech.speak("Please Enter Name", TextToSpeech.QUEUE_ADD, null);

        //textToSpeech.speak("Name",1,null,null);
        //record from user

        //       }
        // });

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




}

