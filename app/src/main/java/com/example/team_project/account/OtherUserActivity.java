package com.example.team_project.account;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.team_project.BottomNavActivity;
import com.example.team_project.LoginActivity;
import com.example.team_project.R;
import com.example.team_project.SurveyActivity;
import com.example.team_project.model.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.ArrayList;

public class OtherUserActivity extends AppCompatActivity {
    private ImageView ivProfilePic;
    private TextView tvName;
    private ParseUser user;
    private RecyclerView rvLiked;
    private LikedAdapter likedAdapter;
    private ArrayList<String> liked;
    private ArrayList<String> distances;
    private ArrayList<String> ids;
    RecyclerView.LayoutManager likedManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user);

        rvLiked = findViewById(R.id.rvLiked);
        liked = new ArrayList<>();
        distances = new ArrayList<>();
        ids = new ArrayList<>();
        likedManager = new LinearLayoutManager(this);
        likedAdapter = new LikedAdapter(liked, distances, ids);
        rvLiked.setLayoutManager(likedManager);
        rvLiked.setAdapter(likedAdapter);

        user = BottomNavActivity.targetUser;
        tvName = findViewById(R.id.tvName);
        tvName.setText(user.getString(User.KEY_NAME));

        ivProfilePic = findViewById(R.id.ivProfilePic);
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
        ArrayList<String> likedParse = (ArrayList<String>) user.get(User.KEY_LIKED_EVENTS);
        for (String i : likedParse) {
            String[] spot = i.split("\\(\\)");
            ids.add(spot[0]);
            distances.add(spot[1]);
            liked.add(spot[2]);
        }
        likedAdapter.notifyDataSetChanged();
    }
}
