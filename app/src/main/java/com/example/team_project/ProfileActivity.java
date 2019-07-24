package com.example.team_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.parse.ParseUser;



public class ProfileActivity extends AppCompatActivity {
     private Button btnLogout;
     private Button btnSurvey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                final Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                BottomNavActivity.bottomNavAct.finish();
                finish();
            }
        });

        btnSurvey = findViewById((R.id.btnSurvey));
        btnSurvey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(ProfileActivity.this, SurveyActivity.class);
                intent.putExtra("retaking", true);
                startActivity(intent);
            }
        });
    }
}
