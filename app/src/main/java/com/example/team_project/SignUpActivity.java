package com.example.team_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpActivity extends AppCompatActivity {
    @BindView(R.id.etUsername) EditText etUsername;
    @BindView(R.id.etPassword) EditText etPassword;
    @BindView(R.id.etEmail) EditText etEmail;

    @OnClick(R.id.btnSignUp)
    public void signupBK(Button button) {
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();
        final String email = etEmail.getText().toString();
        signup(username, password, email);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
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
                        Log.d("SignUpActivity", "Login successful");
                        final Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                            Log.e("SignUpActivity", "Sign Up failure");
                            e.printStackTrace();
                    }
                }
            });
        }
    }
