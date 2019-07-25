package com.example.team_project.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.File;
import java.util.ArrayList;

@ParseClassName("PlaceEvent")
public class PlaceEvent extends ParseObject {
    public static final String KEY_API = "apiId";
    public static final String KEY_CATEGORIES = "categories";
    public static final String KEY_TAGS = "tags";
    public static final String KEY_NAME = "name";

    public String getAppId() {
        return getString(KEY_API);
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
}
