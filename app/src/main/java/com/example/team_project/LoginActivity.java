package com.example.team_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.etUsername) EditText etUsername;
    @BindView(R.id.etPassword) EditText etPassword;

    @OnClick(R.id.btnLogIn)
    public void loginBK(Button button) {
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();
        login(username, password);
    }

    @OnClick(R.id.btnSignUp)
    public void signupBK(Button button) {
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();
        signup(username, password);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            final Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            // so that when user go backs they are not logged out
            finish();
        }
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
