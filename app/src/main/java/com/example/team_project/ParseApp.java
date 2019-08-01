package com.example.team_project;

import android.app.Application;
import com.example.team_project.model.PlaceEvent;
import com.example.team_project.model.Post;
import com.example.team_project.model.User;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(PlaceEvent.class);
        ParseObject.registerSubclass(User.class);

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("trendycity")
                .clientKey("TrendyMaster")
                .server("http://trendycityapp.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);

    }

}
