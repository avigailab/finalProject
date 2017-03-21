package com.example.avigail.lastproject;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class WaitingFormsActivity extends Activity {
    ListView listView;
    public static final String MY_PREFS_NAME = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_forms);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        Map<String,?> keys = prefs.getAll();
        String[] values=new String[keys.size()];
        int i=0;
        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());
            values[i]= entry.getKey();
            i++;
        }
        listView = (ListView) findViewById(android.R.id.list);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                values);
        // Specify the layout to use when the list of choices appears
        // Apply the adapter to the spinner
        listView.setAdapter(adapter);
        //********************************
        // Inflate the layout for this fragment
        //add click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("---", "click " + i);
                Intent singelWaitingFormIntent = new Intent(getApplicationContext(), SingelWaitingForm.class);
                //pass current waiting form
                singelWaitingFormIntent.putExtra("WAITING_FORM_INDEX",i);
                startActivity(singelWaitingFormIntent);            }
        });
    }

}