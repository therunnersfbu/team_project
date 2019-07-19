package com.example.team_project.model;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

@ParseClassName("User")
public class User extends ParseObject {
    // list the attributes
    public static final String KEY_NAME = "name";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_LIKED_EVENTS = "likedEvents";
    public static final String KEY_ADDED_EVENTS = "addedEvents";
    public static final String KEY_PROFILE_PIC = "profilePic";
    public static final String KEY_VERIFIED = "verifiedUser";
    public static final String KEY_CATEGORIES = "categoriesInterested";
    public static final String KEY_TAGS = "tagsInterested";

    public String getName() {
        return getString(KEY_NAME);
    }
    public void setName(String name) {
        put(KEY_NAME, name);
    }
    public String getEmail() {
        return getString(KEY_EMAIL);
    }
    public void setEmail(String email) {
        put(KEY_NAME, email);
    }
    public String getPassword() {
        return getString(KEY_PASSWORD);
    }
    public void setPassword(String password) {
        put(KEY_NAME, password);
    }
    public ArrayList<ParseObject> getLikedEvents() {
        return (ArrayList<ParseObject>) get(KEY_LIKED_EVENTS);
    }
    public void setLikedEvents(ArrayList<ParseObject> likedEvents) {
        put(KEY_LIKED_EVENTS, likedEvents);
    }
    public ArrayList<ParseObject> getAddedEvents() {
        return (ArrayList<ParseObject>) get(KEY_ADDED_EVENTS);
    }
    public void setAddedEvents(ArrayList<ParseObject> addedEvents) {
        put(KEY_ADDED_EVENTS, addedEvents);
    }
    public boolean getVerified() {
        return getBoolean(KEY_VERIFIED);
    }
    public void setVerified(boolean verified) {
        put(KEY_VERIFIED, verified);
    }
    public ParseFile getImage() {
        return getParseFile(KEY_PROFILE_PIC);
    }
    public void setImage(ParseFile profilePic) {
        put(KEY_PROFILE_PIC, profilePic);
    }
    public ArrayList<Boolean> getCategories() {
        return (ArrayList<Boolean>) get(KEY_CATEGORIES);
    }
    public void setCategories(ArrayList<Boolean> categories) {
        put(KEY_CATEGORIES, categories);
    }
    public ArrayList<Boolean> getTags() {
        return (ArrayList<Boolean>) get(KEY_TAGS);
    }
    public void setTags(ArrayList<Boolean> categories) {
        put(KEY_TAGS, categories);
    }
}
