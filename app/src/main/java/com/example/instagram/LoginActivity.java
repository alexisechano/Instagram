package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {
    // view elements
    EditText etUsername;
    EditText etPassword;
    Button btnLogin;

    // set TAG
    public static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // go to main activity if already signed in
        if(ParseUser.getCurrentUser() != null){
            openMainActivity();
        }

        // set views and match to ID + on click listener
        setViewElems();
    }

    private void setViewElems() {
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        // set new on click listener for button
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"Clicked button!");

                // get the text fields from the login layout
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });
    }

    private void loginUser(String username, String password) {
        // log in user given credentials
        Log.i(TAG, "Attempting to login user: " + username);

        // user Parse to log in
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                 if(e != null){
                     Log.e(TAG, "Unable to login", e);
                     Toast.makeText(LoginActivity.this, "Error with username/password", Toast.LENGTH_SHORT).show();
                     return;
                 }
                 // navigate to main activity if success
                 Log.i(TAG, "Success! Going to main...");
                 Toast.makeText(LoginActivity.this, "Logging in...", Toast.LENGTH_SHORT).show();
                 openMainActivity();
            }
        });
    }

    private void openMainActivity() {
        // launch new intent
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}