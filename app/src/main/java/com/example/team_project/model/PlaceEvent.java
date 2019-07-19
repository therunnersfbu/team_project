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
    // TODO: initialize all tags as integers

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
}
