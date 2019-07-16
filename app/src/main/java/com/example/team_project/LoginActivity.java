package com.example.team_project;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnLogIn;
    private Button btnSignUp;
    Fragment PostsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogIn = findViewById(R.id.btnLogIn);
        btnSignUp = findViewById(R.id.btnSignUp);


        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            // so that when user go backs they are not logged out
            finish();
        } else {
            // show the signup or login screen
            btnLogIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // get the username and password
                    final String username = etUsername.getText().toString();

                    final String password = etPassword.getText().toString();
                    login(username, password);
                }
            });
        }


        // send to sign up page when sign up button is clicked
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the username and password
                final String username = etUsername.getText().toString();

                final String password = etPassword.getText().toString();
                signup(username, password);
            }
        });
    }

    private void login(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e == null) { // there are no errors
                    Log.d("LoginActivity", "Login successful");

                    final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);

                    // so that when user go backs they are not logged out
                    finish();
                } else {
                    Log.e("LoginActivity", "Login failure");
                    e.printStackTrace();
                }
            }
        });
    }

    // User Signup
    private void signup(String username, String password) {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        // user.setEmail("shannonmj@fb.com");
        // Set custom properties
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("SignUpActivity", "Login successful");
                    final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    // or you can put login(username, password)
                } else {
                    Log.e("SignUpActivity", "Sign Up failure");
                    e.printStackTrace();
                }
            }
        });
    }
}
