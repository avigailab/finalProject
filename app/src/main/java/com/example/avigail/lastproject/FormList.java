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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FormList} interface
 * to handle interaction events.
 * Use the {@link FormList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FormList extends Fragment{
    ListView listView;
    private AppAdapter appAdapter;
    final  String TAG="FormList";
    static ArrayList<Layout> arrayOfLayouts;
    String [] layoutsNames;
    public static final String MY_PREFS_NAME = "UserProfile";
    public FormList() {
        // Required empty public constructor
    }

    public static FormList newInstance() {
        FormList fragment = new FormList();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_form, container, false);
        appAdapter=new AppAdapter();
        listView = (ListView) view.findViewById(R.id.form_list);
        getLayoutForUser();
        return view;
    }
    private void getLayoutForUser() {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        SharedPreferences userProfilePref = this.getActivity().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String userName = userProfilePref.getString("UserName","");
        Log.e(TAG,userName);
        String URL= "https://wili.tukuoro.com/tukwebservice/tukwebservice_app.asmx/GetLayoutsForUser?tukLogin="+userName+"&serviceId=58469251&clientId=68174861";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response-------------",response.toString());
                        if(response!=null) {
                            arrayOfLayouts = appAdapter.getObjectsLayoutsForUser(response);
                            layoutsNames= new String[arrayOfLayouts.size()];
                            for (int i = 0; i < arrayOfLayouts.size(); i++) {
                                Log.e(TAG, "layout number " + i);
                                layoutsNames[i] = arrayOfLayouts.get(i).layoutName;
                            }

                            // Create an ArrayAdapter using the string array and a default spinner layout
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                    R.layout.list_form_item,
                                    layoutsNames);
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
                                    Intent formIntent = new Intent(getActivity(), FormActivity.class);
                                    //pass current layout
                                    formIntent.putExtra("LAYOUT",arrayOfLayouts.get(i));
                                    startActivity(formIntent);

                                }
                            });
                        }
                    }

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

}
