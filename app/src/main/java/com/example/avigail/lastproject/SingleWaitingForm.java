package com.example.avigail.lastproject;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.Normalizer;
import java.util.Iterator;
import java.util.Map;

public class SingleWaitingForm extends AppCompatActivity {
    public static final String MY_PREFS_NAME = "MyPrefs";
    private static final String TAG ="SingleWaitingForm";
    Button saveForm, sendForm;
    int currentFormIndex=-1;
    Layout currentForm;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singel_waiting_form);
        saveForm =(Button)findViewById(R.id.save);
        sendForm =(Button)findViewById(R.id.send);
        gson = new Gson();
        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_singel_waiting_form); // linearMain //is your linearlayout in XML file
        // create the layout params that will be used to define how your
        // button will be displayed
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);


        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        currentFormIndex= getIntent().getIntExtra("WAITING_FORM_INDEX",-1);
        Map<String,?> keys = prefs.getAll();
        int i=0;
        for(Map.Entry<String,?> entry : keys.entrySet()){
            if(i == currentFormIndex) {
                String json = prefs.getString(entry.getKey(), "");
                currentForm = gson.fromJson(json, Layout.class);
            }
            i++;
        }
        for(int j=0;j<currentForm.fields.size();j++)
        {
            // Create LinearLayout
            LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout2);
            // Create TextView
            TextView lb= new TextView(this);
            lb.setText(currentForm.fields.get(j).filedName);
            lb.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            ll.addView(lb);

            // Create TextView
            EditText et= new EditText(this);
            et.setText(currentForm.fields.get(j).filedAnswer);
            et.setId(j);
            ll.addView(et);

        }
        saveForm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                for(int j=0;j<currentForm.fields.size();j++){
                    EditText etf = (EditText) findViewById(j);
                    String st = etf.getText().toString();
                    currentForm.fields.get(j).filedAnswer = st;
                    Log.i(TAG,st);
                }

                String jsonLayout = gson.toJson(currentForm);
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString(currentForm.layoutName, jsonLayout);
                editor.commit();
                finish();

            }
        });
        sendForm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                AppAdapter appAdapter = new AppAdapter();
                appAdapter.submitLayoutForUser(currentForm);
            }
        });

    }
}
