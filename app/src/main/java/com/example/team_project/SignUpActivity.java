package com.example.team_project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
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
    @BindView(R.id.etPassword2) EditText etPassword2;

    @OnClick(R.id.btnSignUp)
    public void signupBK(Button button) {
        final String username = etUsername.getText().toString();
        final String password = etPassword.getText().toString();
        final String password2 = etPassword2.getText().toString();
        final String email = etEmail.getText().toString();
        if (password.equals(password2)) {
            signup(username, password, email);
        } else {
            Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
        }
    }

    public static Activity signupAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        signupAct = this;
    }

        // User Signup
        private void signup (String username, String password, String email){
            final Intent intent = new Intent(this, SurveyActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("email", email);
            intent.putExtra("password", password);
            startActivity(intent);
        }
    }
