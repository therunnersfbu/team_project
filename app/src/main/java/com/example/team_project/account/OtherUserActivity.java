package com.example.team_project.account;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.team_project.BottomNavActivity;
import com.example.team_project.PublicVariables;
import com.example.team_project.R;
import com.example.team_project.api.DirectionsApi;
import com.example.team_project.model.PlaceEvent;
import com.example.team_project.model.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;

public class OtherUserActivity extends AppCompatActivity implements DirectionsApi.GetDistances {

    private ArrayList<String> liked;
    private ArrayList<String> distances;
    private ArrayList<String> ids;
    private ArrayList<String> address;
    private RecyclerView.LayoutManager likedManager;
    private LikedAdapter likedAdapter;
    private ParseUser user;

    @BindView(R.id.rvLiked) RecyclerView rvLiked;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.ivProfilePic) ImageView ivProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user);
        ButterKnife.bind(this);

        liked = new ArrayList<>();
        distances = new ArrayList<>();
        ids = new ArrayList<>();
        address = new ArrayList<>();
        likedManager = new LinearLayoutManager(this);
        likedAdapter = new LikedAdapter(liked, distances, ids, address, null);

        rvLiked.setLayoutManager(likedManager);
        rvLiked.setAdapter(likedAdapter);
        user = BottomNavActivity.targetUser;
        tvName.setText(user.getString(User.KEY_NAME));

        ParseFile imageFile = user.getParseFile(User.KEY_PROFILE_PIC);
        if (imageFile != null) {
            Glide.with(this)
                    .load(imageFile.getUrl())
                    .placeholder(R.drawable.ic_person_black_24dp)
                    .error(R.drawable.ic_person_black_24dp)
                    .into(ivProfilePic);
        } else {
            Glide.with(this)
                    .load(R.drawable.ic_person_black_24dp)
                    .placeholder(R.drawable.ic_person_black_24dp)
                    .error(R.drawable.ic_person_black_24dp)
                    .into(ivProfilePic);
        }

        getLiked();
    }

    private void getLiked() {
        DirectionsApi api = new DirectionsApi(this);
        api.setOrigin(BottomNavActivity.currentLat, BottomNavActivity.currentLng);
        ArrayList<String> likedParse = (ArrayList<String>) user.get(User.KEY_LIKED_EVENTS);
        for (String i : likedParse) {
            try {
                String[] spot = i.split(PublicVariables.splitindicator);
                ids.add(spot[0]);
                liked.add(spot[1]);
                address.add(spot[2]);
                ParseQuery query = new ParseQuery("PlaceEvent");
                query.whereContains(PlaceEvent.KEY_API, spot[0]);
                String coords = ((PlaceEvent) query.getFirst()).getCoordinates().replace(" ", ",");
                api.addDestination(coords);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        api.getDistance();
    }

    @Override
    public void gotDistances(ArrayList<String> distancesApi) {
        distances.addAll(distancesApi);
        likedAdapter.notifyDataSetChanged();
    }
}
