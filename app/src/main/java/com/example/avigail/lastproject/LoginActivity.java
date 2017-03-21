package com.example.avigail.lastproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


public class LoginActivity extends AppCompatActivity {
        private EditText username;
        private EditText password;
        private Button login;
        private TextView loginLockedTV;
        private TextView attemptsLeftTV;
        private TextView numberOfRemainingLoginAttemptsTV;
        int numberOfRemainingLoginAttempts = 3;
        public static final String MY_PREFS_NAME = "UserProfile";
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            setupVariables();
        }

        public void authenticateLogin(View view) {

            RequestQueue queue = Volley.newRequestQueue(this);
            String URL= "https://wili.tukuoro.com/tukwebservice/tukwebservice_app.asmx/Login?UserName="+username.getText()+"&Password="+password.getText()+"&AppVersionNumber=1&OS=Android&DeviceType=phone&serviceId=58469251&clientId=68174861";
            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("request sucsses!!",response.toString());
                            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                            DocumentBuilder db = null;
                            try {
                                db = dbf.newDocumentBuilder();
                            } catch (ParserConfigurationException e) {
                                e.printStackTrace();
                            }
                            InputSource is = new InputSource();

                            is.setCharacterStream(new StringReader(response));

                            try {

                                Document doc = db.parse(is);
                                NodeList loginRes = doc.getElementsByTagName("TukLoginResponse");
                                String result = loginRes.item(0).getTextContent();

                                if (result.trim().equals("Success")) {
                                    Toast.makeText(getApplicationContext(), "Hello admin!",
                                            Toast.LENGTH_SHORT).show();
                                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                                    editor.putString("UserName", username.getText().toString());
                                    editor.commit();
                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(getApplicationContext(), "Seems like you 're not admin!",
                                            Toast.LENGTH_SHORT).show();
                                    numberOfRemainingLoginAttempts--;
                                    attemptsLeftTV.setVisibility(View.VISIBLE);
                                    numberOfRemainingLoginAttemptsTV.setVisibility(View.VISIBLE);
                                    numberOfRemainingLoginAttemptsTV.setText(Integer.toString(numberOfRemainingLoginAttempts));

                                    if (numberOfRemainingLoginAttempts == 0) {
                                        login.setEnabled(false);
                                        loginLockedTV.setVisibility(View.VISIBLE);
                                        loginLockedTV.setBackgroundColor(Color.RED);
                                        loginLockedTV.setText("LOGIN LOCKED!!!");
                                    }
                                }

                            } catch (SAXException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("Error","");
                }
            });
            // Add the request to the RequestQueue.
            queue.add(stringRequest);

        }

        private void setupVariables() {
            username = (EditText) findViewById(R.id.usernameET);
            password = (EditText) findViewById(R.id.passwordET);
            login = (Button) findViewById(R.id.loginBtn);
            loginLockedTV = (TextView) findViewById(R.id.loginLockedTV);
            attemptsLeftTV = (TextView) findViewById(R.id.attemptsLeftTV);
            numberOfRemainingLoginAttemptsTV = (TextView) findViewById(R.id.numberOfRemainingLoginAttemptsTV);
            numberOfRemainingLoginAttemptsTV.setText(Integer.toString(numberOfRemainingLoginAttempts));

            //login!!!!!!
            username.setText("avigailavraham7@gmail.com");
            password.setText("ogd9abzu");
            //login!!!!!!
        }
        public String getUserName(){
            return "admin";
        }

}