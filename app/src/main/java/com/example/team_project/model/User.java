package com.example.team_project.model;

import org.json.JSONException;
import org.json.JSONObject;

// @Parcel
public class User {
    // list the attributes
    public String username;
    public String password;


    //deserialize the JSON
    public static User fromJSON(JSONObject json) throws JSONException {
        User user = new User();

        //extract and fill values
        user.username = json.getString("username");
        user.password = json.getString("password");

        return user;
    }

    public String setUsername() {
        return username;
    }

    public String setPassword() {
        return password;
    }

}
