package com.example.team_project.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

@ParseClassName("Post")
public class Post extends ParseObject {

    public static final String KEY_USER = "user";
    public static final String KEY_REVIEW = "review";
    public static final String KEY_EVENT = "event";
    public static final String KEY_IS_LOCAL = "isLocal";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_ID = "eventId";
    public static final String KEY_IMAGE ="photoFile";

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) { put(KEY_USER, user); }

    public String getReview() {
        return getString(KEY_REVIEW);
    }

    public void setReview(String review) {
        put(KEY_REVIEW, review);
    }

    public ParseObject getEvent() {
        return getParseObject(KEY_EVENT);
    }

    public void setEvent(ParseObject event) {
        put(KEY_EVENT, event);
    }

    public boolean getIsLocal() { return getBoolean(KEY_IS_LOCAL); }

    public void setIsLocal(boolean isLocal) {put(KEY_IS_LOCAL, isLocal); }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public ArrayList<Integer> getTags() { return (ArrayList<Integer>) get(KEY_TAGS); }

    public void setTags(ArrayList<Integer> categories) { put(KEY_TAGS, categories); }

    public String getId() {
        return getString(KEY_ID);
    }

    public void setId (String id){
        put(KEY_ID, id);
    }
}