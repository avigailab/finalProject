package com.example.avigail.lastproject;

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

public class WaitingFormsActivity extends ListActivity {

    TextView content;
    ListView listView;
    public static final String MY_PREFS_NAME = "MyPrefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_forms);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        Map<String,?> keys = prefs.getAll();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());
        }
       /* Gson gson = new Gson();
        String json = prefs.getString("MyObject", "");
        Layout obj = gson.fromJson(json, Layout.class);*/

       /* SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString("text", null);
        if (restoredText != null) {
            String name = prefs.getString("name", "No name defined");//"No name defined" is the default value.
            int idName = prefs.getInt("idName", 0); //0 is the default value.
        }*/

        /*listView = (ListView) findViewById(android.R.id.list);
        content = (TextView)findViewById(R.id.output);
        WaitingForms waitingForms=new WaitingForms();
        final ArrayList<Layout> arrayOfLayouts=waitingForms.getWaitingForms();
        Log.i("waaaaa", String.valueOf(waitingForms.getWaitingForms().get(0).layoutName));
        String[] values = new String[] {""};
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                values);
        // Specify the layout to use when the list of choices appears
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        listView.setAdapter(adapter);

        //********************************
        // Inflate the layout for this fragment
        //add click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("---", "click " + i);
                Intent layoutIntent = new Intent(getApplication(), FormActivity.class);
                //pass current layout
                layoutIntent.putExtra("LAYOUT",arrayOfLayouts.get(i));
                startActivity(layoutIntent);

            }
        });*/
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        super.onListItemClick(l, v, position, id);

        // ListView Clicked item index
        int itemPosition     = position;

        // ListView Clicked item value
        String  itemValue    = (String) l.getItemAtPosition(position);

        content.setText("Click : \n  Position :"+itemPosition+"  \n  ListItem : " +itemValue);

    }
}