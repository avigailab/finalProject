package com.example.avigail.lastproject;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class LoginActivity extends AppCompatActivity {
        private EditText username;
        private EditText password;
        private Button login;
        private TextView loginLockedTV;
        private TextView attemptsLeftTV;
        private TextView numberOfRemainingLoginAttemptsTV;
        int numberOfRemainingLoginAttempts = 3;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);
            setupVariables();
        }

        public void authenticateLogin(View view) {
            if (username.getText().toString().equals("admin") &&
                    password.getText().toString().equals("admin")) {
                Toast.makeText(getApplicationContext(), "Hello admin!",
                        Toast.LENGTH_SHORT).show();
               /* Intent intent = new Intent(this, AudioRecordActivity.class);
                startActivity(intent);*/
                 Intent intent = new Intent(this, LayoutActivity.class);
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
        }

        private void setupVariables() {
            username = (EditText) findViewById(R.id.usernameET);
            password = (EditText) findViewById(R.id.passwordET);
            login = (Button) findViewById(R.id.loginBtn);
            loginLockedTV = (TextView) findViewById(R.id.loginLockedTV);
            attemptsLeftTV = (TextView) findViewById(R.id.attemptsLeftTV);
            numberOfRemainingLoginAttemptsTV = (TextView) findViewById(R.id.numberOfRemainingLoginAttemptsTV);
            numberOfRemainingLoginAttemptsTV.setText(Integer.toString(numberOfRemainingLoginAttempts));
        }
        public String getUserName(){
            return "admin";
        }

}