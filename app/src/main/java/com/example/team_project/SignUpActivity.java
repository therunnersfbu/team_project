package com.example.team_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignUpActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btnSignUp;
    private EditText etEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        etUsername = findViewById(R.id.etDescription);
        etPassword = findViewById(R.id.etPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        etEmail = findViewById(R.id.etEmail);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the username and password and password
                final String username = etUsername.getText().toString();

                final String password = etPassword.getText().toString();

                final String email = etEmail.getText().toString();
                signup(username, password, email);
            }
        });
    }

        // User Signup
        private void signup (String username, String password, String email){
            // Create the ParseUser
            ParseUser user = new ParseUser();
            // Set core properties
            user.setUsername(username);
            user.setPassword(password);
            user.setEmail(email);
            // Set custom properties
            user.put("phone", "650-253-0000");
            // Invoke signUpInBackground
            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        // Hooray! Let them use the app now.
                    } else {
                        // Sign up didn't succeed. Look at the ParseException
                        // to figure out what went wrong
                    }
                }
            });
        }
    }
