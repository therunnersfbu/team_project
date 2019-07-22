package com.example.team_project.model;

import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class Place {
    // data for list
    private String id;
    private double[] location;
    private String name;
    private String address;
    private String distance;

    // data for detail view
    private String phoneNumber;
    private ArrayList<String> openHours;
    private int price;

    public static Place placeFromJson(JSONObject object) throws JSONException {
        Place place = new Place();
        place.id = object.getString("place_id");
        double[] loc = new double[2];
        loc[0] = object.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
        loc[1] = object.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
        place.location = loc;
        place.name = object.getString("name");
        place.address = object.getString("vicinity");
        return place;
    }

    public void setDetailsFinished() {
        Log.d("Place", "Got details!");
    }

    public String getPlaceId() {
        return id;
    }

    public double[] getLocation() {
        return location;
    }

    public String getPlaceName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public ArrayList<String> getOpenHours() {
        return openHours;
    }

    public int getPrice() {
        return price;
    }

    public void setPlaceId(String id) {
        this.id = id;
    }

    public void setLocation(double[] location) {
        this.location = location;
    }

    public void setPlaceName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setOpenHours(ArrayList<String> openHours) {
        this.openHours = openHours;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDistance() {
        return distance;
    }
}
