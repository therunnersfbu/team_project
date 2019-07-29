package com.example.team_project.details;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.example.team_project.R;
import com.example.team_project.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
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
    }

    @Override
    protected void onResume() {
        super.onResume();

        ParseQuery parseQuery = new ParseQuery("Post");
        parseQuery.include(Post.KEY_USER);
        parseQuery.setLimit(1000);

        parseQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    ArrayList<Post> postsForThisEvent = new ArrayList<>();
                    postsForThisEvent.add(new Post());
                    for (Post i : objects) {
                        if (id.equals(i.getEventPlace().getAppId())) {
                            postsForThisEvent.add(i);
                        }
                    }
                    EventsDetailsAdapter adapter = new EventsDetailsAdapter(
                            postsForThisEvent, id, type, distance, DetailsActivity.this);
                    rvEventsDetail.setAdapter(adapter);

                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
