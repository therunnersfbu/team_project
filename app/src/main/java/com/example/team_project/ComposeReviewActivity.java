package com.example.team_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.nex3z.flowlayout.FlowLayout;

public class ComposeReviewActivity extends AppCompatActivity {

    private TextView tvHeader;
    private EditText etBody;
    private Button btnPhoto;
    private Button btnCancel;
    private Button btnPost;
    private FlowLayout flTags;
    private Switch sLocal;
    private String id;
    private String name;
    private static final String EVENT_ID = "eventID";
    private static final String NAME = "eventName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_review);
        tvHeader = findViewById(R.id.tvHeader);
        etBody = findViewById(R.id.etBody);
        btnPhoto = findViewById(R.id.btnPhoto);
        btnCancel = findViewById(R.id.btnCancel);
        btnPost = findViewById(R.id.btnPost);
        flTags = findViewById(R.id.flTags);
        sLocal = findViewById(R.id.sLocal);
        id = getIntent().getStringExtra(EVENT_ID);
        name = getIntent().getStringExtra(NAME);
        tvHeader.setText(name);

    }
}
