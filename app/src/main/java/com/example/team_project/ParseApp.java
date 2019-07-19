package com.example.team_project;

import android.app.Application;

import com.example.team_project.model.Event;
import com.example.team_project.model.PlaceEvent;
import com.example.team_project.model.Post;
import com.example.team_project.model.User;
import com.parse.Parse;
import com.parse.ParseObject;

import org.w3c.dom.Comment;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(PlaceEvent.class);
        ParseObject.registerSubclass(User.class);

        //ParseObject.registerSubclass(Post.class);


        // Use for monitoring Parse OkHttp traffic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
       /* OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);*/

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("trendycity")
                .clientKey("TrendyMaster")
                .server("http://trendycityapp.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);

    }

}
