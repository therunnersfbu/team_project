package com.example.team_project.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.io.File;
import java.util.ArrayList;

@ParseClassName("Place")
public class Place extends ParseObject {

    // initialize types to be stored in Place parse object
    public static final String KEY_NAME = "name";
    public static final String KEY_LIKED = "liked";
    // TODO: figure out how wa want to save the distance and link to directions on maps app
    // TODO: initialize all tags as booleans
    // public static final String KEY_DISTANCE = "distance";
    // public static final String KEY_DIRECTIONS = "name";
    public static final String KEY_IMAGES = "photoArray";
    public static final String KEY_HOURS = "hours";
    public static final String KEY_APIID = "apiId";

    public String getName() {
        return getString(KEY_NAME);
    }
    public void setName(String name) {
        put(KEY_NAME, name);
    }
    public boolean getLiked() {
        return getBoolean(KEY_LIKED);
    }
    public void setLiked(boolean liked) {
        put(KEY_LIKED, liked);
    }
    public ArrayList<File> getPhotos() {
        return (ArrayList<File>) get(KEY_IMAGES);
    }
    public void setPhotos(ArrayList<File> photos) {
        put(KEY_IMAGES, photos);
    }
    public String getHours() {
        return getString(KEY_HOURS);
    }
    public void setHours(String hours) {
        put(KEY_HOURS, hours);
    }
    public String getAppId() {
        return getString(KEY_APIID);
    }
    public void setAppId(String appId) {
        put(KEY_APIID, appId);
    }

}
