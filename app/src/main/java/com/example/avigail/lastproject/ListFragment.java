package com.example.avigail.lastproject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.ArrayList;
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListFragment} interface
 * to handle interaction events.
 * Use the {@link ListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListFragment extends Fragment{
    ListView listView;
    private AppAdapter appAdapter;
    final  String TAG="ListFragment";
    static ArrayList<Layout> arrayOfLayouts;
    String [] layoutsNames;
    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance() {
        ListFragment fragment = new ListFragment();
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
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        appAdapter=new AppAdapter();
        listView = (ListView) view.findViewById(R.id.form_list);
        getLayoutForUser();
        return view;
    }
    private void getLayoutForUser() {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        // final ListView listView = (ListView) findViewById(R.id.layoutsList);

        String URL= "https://wili.tukuoro.com/tukwebservice/tukwebservice_app.asmx/GetLayoutsForUser?tukLogin=orayrs@gmail.com&serviceId=58469251&clientId=68174861";
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
                                    android.R.layout.simple_list_item_1,
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
                                    Intent layoutIntent = new Intent(getActivity(), LayoutActivity.class);
                                    //pass current layout
                                    layoutIntent.putExtra("LAYOUT",arrayOfLayouts.get(i));
                                    startActivity(layoutIntent);

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
