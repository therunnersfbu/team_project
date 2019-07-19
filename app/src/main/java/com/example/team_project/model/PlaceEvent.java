package com.example.team_project.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import java.io.File;
import java.util.ArrayList;

@ParseClassName("PlaceEvent")
public class PlaceEvent extends ParseObject {
    public static final String KEY_API = "apiId";
    public static final String KEY_IMAGES = "photoArray";
    public static final String KEY_CATEGORIES = "categories";
    public static final String KEY_TAGS = "tags";

    public String getAppId() {
        return getString(KEY_API);
    }
    public void setAppId(String appId) {
        put(KEY_API, appId);
    }
    public ArrayList<ParseFile> getPhotos() {
        return (ArrayList<ParseFile>) get(KEY_IMAGES);
    }
    public void setPhotos(ArrayList<File> photos) {
        put(KEY_IMAGES, photos);
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
}
