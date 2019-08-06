package com.example.team_project.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import java.util.ArrayList;

@ParseClassName("PlaceEvent")
public class PlaceEvent extends ParseObject {
    public static final String KEY_API = "apiId";
    public static final String KEY_CATEGORIES = "categories";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_NAME = "name";
    public static final String KEY_COORDS = "coordinates";
    public static final String KEY_LIKED = "liked";
    public static final String KEY_REVIEWED = "reviewed";

    public String getAppId() {
        String id = "";
        try {
            id = fetchIfNeeded().getString(KEY_API);

        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }
        return id;
    }
    public void setAppId(String appId) {
        put(KEY_API, appId);
    }
    public ArrayList<Boolean> getCategories() {
        return (ArrayList<Boolean>) get(KEY_CATEGORIES);
    }
    public void setCategories(ArrayList<Boolean> categories) {
        put(KEY_CATEGORIES, categories);
    }
    public ArrayList<Integer> getTags() {
        return (ArrayList<Integer>) get(KEY_TAGS);
    }
    public void setTags(ArrayList<Integer> categories) {
        put(KEY_TAGS, categories);
    }
    public String getName() {
        return getString(KEY_NAME);
    }
    public void setName(String name) {
        put(KEY_NAME, name);
    }
    public String getCoordinates() {
        return getString(KEY_COORDS);
    }
    public void setCoordinates(String coordinates) {
        put(KEY_COORDS, coordinates);
    }
    public int getLiked() {
        return getInt(KEY_LIKED);
    }
    public void setLiked(int liked) {
        put(KEY_LIKED, liked);
    }
    public int getReviewed() {
        return getInt(KEY_REVIEWED);
    }
    public void setReviewed(int reviewed) {
        put(KEY_REVIEWED, reviewed);
    }
}
