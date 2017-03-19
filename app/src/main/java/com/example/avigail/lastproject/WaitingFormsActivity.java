package com.example.avigail.lastproject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class WaitingFormsActivity extends ListActivity {

    TextView content;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_forms);
        listView = (ListView) findViewById(android.R.id.list);
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
        });
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