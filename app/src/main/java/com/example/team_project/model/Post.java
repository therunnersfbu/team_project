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
    public static final String KEY_IS_LOCAL = "isLocal";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_EVENT_PLACE = "eventPointer";
    public static final String KEY_IMAGE = "photoFile";
    public static final String KEY_COORDINATES = "coordinates";

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public User getUserAsObject() {
        return (User) getParseObject(KEY_USER);
    }

    public void setUser(ParseUser user) { put(KEY_USER, user); }

    public String getReview() {
        return getString(KEY_REVIEW);
    }

    public void setReview(String review) {
        put(KEY_REVIEW, review);
    }

    public boolean getIsLocal() { return getBoolean(KEY_IS_LOCAL); }

    public void setIsLocal(boolean isLocal) {put(KEY_IS_LOCAL, isLocal); }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }

    public void setImage(ParseFile parseFile) {
        put(KEY_IMAGE, parseFile);
    }

    public ArrayList<Boolean> getTags() { return (ArrayList<Boolean>) get(KEY_TAGS); }

    public void setTags(ArrayList<Boolean> categories) { put(KEY_TAGS, categories); }

    public PlaceEvent getEventPlace() { return (PlaceEvent) getParseObject(KEY_EVENT_PLACE); }

    public void setEventPlace (ParseObject eventPlace){
        put(KEY_EVENT_PLACE, eventPlace);
    }

    public String getCoordinates() {
        return getString(KEY_COORDINATES);
    }

    public void setCoordinates(String coordinates) {
        put(KEY_COORDINATES, coordinates);
    }
}