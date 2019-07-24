package com.example.team_project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.team_project.model.Post;

import java.util.ArrayList;
import java.util.List;

public class DetailsActivity extends AppCompatActivity {
    private RecyclerView rvEventsDetail;
    private String id;
    private boolean type;
    private String distance;
    public static final String EVENT_ID = "eventID";
    public static final String TYPE = "type";
    public static final String DISTANCE = "distance";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        id = getIntent().getStringExtra(EVENT_ID);
        type = getIntent().getBooleanExtra(TYPE, true);
        distance = getIntent().getStringExtra(DISTANCE);
        rvEventsDetail = (RecyclerView)findViewById(R.id.rvEventsDetail);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DetailsActivity.this);
        rvEventsDetail.setLayoutManager(linearLayoutManager);
        EventsDetailsAdapter adapter = new EventsDetailsAdapter(getData(), id, type, distance, this);
        rvEventsDetail.setAdapter(adapter);
    }

    private List<Post> getData() {
        List<Post> testPosts = new ArrayList<Post>();
        Post post1 = new Post();
        Post post2 = new Post();
        Post post3 = new Post();
        Post post4 = new Post();
        testPosts.add(post1);
        testPosts.add(post2);
        testPosts.add(post3);
        testPosts.add(post4);
        return testPosts;
    }
}
