package com.example.avigail.lastproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    ProgressBar bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singel_waiting_form);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        bar = (ProgressBar) this.findViewById(R.id.progressBar);
        saveForm =(Button)findViewById(R.id.save);
        sendForm =(Button)findViewById(R.id.send);
        gson = new Gson();

        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.activity_singel_waiting_form);
        // create the layout params that will be used to define layout
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        //get current index of form
        currentFormIndex= getIntent().getIntExtra("WAITING_FORM_INDEX",-1);

        //iterate forms to get current form details
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
            /*et.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  LinearLayout linearLayout = (LinearLayout) findViewById(R.id.single_waiting_form_btns);
                  linearLayout.setVisibility(View.GONE);
                }
            });
*/
            ll.addView(et);

        }
        saveForm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //show dialog
                bar.setVisibility(View.VISIBLE);

                //iterate all fields in current form
                for(int j=0;j<currentForm.fields.size();j++){
                    EditText etf = (EditText) findViewById(j);
                    String st = etf.getText().toString();
                    currentForm.fields.get(j).filedAnswer = st;
                    Log.i(TAG,st);
                }
                //delete from shared preferences
                String jsonLayout = gson.toJson(currentForm);
                SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                editor.putString(currentForm.layoutName, jsonLayout);
                editor.commit();
                //finish activity
                finish();

            }
        });
        sendForm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //show dialog
                bar.setVisibility(View.VISIBLE);
                AppAdapter appAdapter = new AppAdapter();
               if(appAdapter.submitLayoutForUser(currentForm)){
                   Toast.makeText(getApplicationContext(),  getResources().getString(R.string.submitFormSucsess),
                           Toast.LENGTH_SHORT).show();
                   //delete from shared preferences
                   SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                   editor.remove(currentForm.layoutName);
                   editor.apply();
                   //go to main activity to apply changes
                   Intent i = new Intent(getApplication(),MainActivity.class);
                   startActivity(i);
               }
                else {
                   Toast.makeText(getApplicationContext(),  getResources().getString(R.string.submitFormFaild),
                           Toast.LENGTH_SHORT).show();
                   //go to main activity to apply changes
                   Intent i = new Intent(getApplication(),MainActivity.class);
                   startActivity(i);
               }


            }
        });

    }



}
