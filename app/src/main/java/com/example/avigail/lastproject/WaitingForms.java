package com.example.avigail.lastproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class WaitingForms extends Fragment {
    private static final String TAG = "WaitingForms";
    ListView listView;

    public static final String MY_PREFS_NAME = "MyPrefs";

    public WaitingForms() {
        // Required empty public constructor
    }


    public static WaitingForms newInstance() {
        WaitingForms fragment = new WaitingForms();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG,"in waiting form");
        View view = inflater.inflate(R.layout.waiting_forms, container, false);

        SharedPreferences prefs = getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        Map<String, ?> keys = prefs.getAll();
        String[] values = new String[keys.size()];
        int i = 0;
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            values[i] = entry.getKey();
            i++;
        }
        //no waiting forms
        if(i==0) {
            view.findViewById(R.id.no_form_label).setVisibility(View.VISIBLE);
        }


        listView = (ListView) view.findViewById(android.R.id.list);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
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
                Intent singelWaitingFormIntent = new Intent(getActivity(), SingleWaitingForm.class);
                //pass current waiting form
                singelWaitingFormIntent.putExtra("WAITING_FORM_INDEX", i);
                startActivity(singelWaitingFormIntent);
            }
        });
        return view;
    }

}