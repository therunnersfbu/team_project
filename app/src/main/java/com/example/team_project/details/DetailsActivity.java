package com.example.team_project.details;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.example.team_project.PublicVariables;
import com.example.team_project.R;
import com.example.team_project.location.LocationAdapter;
import com.example.team_project.model.PlaceEvent;
import com.example.team_project.model.Post;
import com.example.team_project.utils.ContextProvider;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements EventsDetailsAdapter.AdapterCallback{

    public static final String EVENT_ID = "eventID";
    public static final String TYPE = "type";
    public static final String DISTANCE = "distance";
    public static final int REQUEST_PHONE_CALL = 170;

    private String id;
    private boolean type;
    private String distance;

    @BindView(R.id.rvEventsDetail) RecyclerView rvEventsDetail;
    @BindView(R.id.pbSpinner) ProgressBar mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        id = getIntent().getStringExtra(EVENT_ID);
        type = getIntent().getBooleanExtra(TYPE, true);
        distance = getIntent().getStringExtra(DISTANCE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(DetailsActivity.this);
        rvEventsDetail.setLayoutManager(linearLayoutManager);
    }

    @Override
    protected void onResume() {
        super.onResume();

        ParseQuery placeEventQuery = new ParseQuery("PlaceEvent");
        placeEventQuery.setLimit(1000);
        placeEventQuery.whereMatches(PlaceEvent.KEY_API, id);
        try {
            PlaceEvent placeEvent = (PlaceEvent) placeEventQuery.getFirst();

            ParseQuery parseQuery = new ParseQuery("Post");
            parseQuery.include(Post.KEY_USER);
            parseQuery.setLimit(1000);
            parseQuery.whereEqualTo(Post.KEY_EVENT_PLACE, placeEvent);

            parseQuery.findInBackground(new FindCallback<Post>() {
                @Override
                public void done(List<Post> objects, ParseException e) {
                    if (e == null) {
                        ArrayList<Post> postsForThisEvent = new ArrayList<>();
                        postsForThisEvent.add(new Post());
                        postsForThisEvent.addAll(objects);

                        EventsDetailsAdapter adapter = new EventsDetailsAdapter(
                                postsForThisEvent, id, type, distance, new ContextProvider() {
                            @Override
                            public Context getContext() {
                                return DetailsActivity.this;
                            }
                        });
                        rvEventsDetail.setAdapter(adapter);
                        adapter.setOnItemClickedListener(DetailsActivity.this);
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        } catch (ParseException e) {
            ArrayList<Post> postsForThisEvent = new ArrayList<>();
            postsForThisEvent.add(new Post());

            EventsDetailsAdapter adapter = new EventsDetailsAdapter(
                    postsForThisEvent, id, type, distance, new ContextProvider() {
                @Override
                public Context getContext() {
                    return DetailsActivity.this;
                }
            });
            rvEventsDetail.setAdapter(adapter);
            adapter.setOnItemClickedListener(DetailsActivity.this);
        }
    }

    @Override
    public void onItemClicked() {
        mSpinner.setVisibility(View.GONE);
    }
}
