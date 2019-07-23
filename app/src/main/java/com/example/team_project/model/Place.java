package com.example.team_project.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class Place {
    // data for an event
    private String id;
    private double[] location;
    private String name;
    private String address;
    private String distance;

    // with details
    private String phoneNumber;
    private ArrayList<String> openHours;
    private int price;

    public static Place placeFromJson(JSONObject object, boolean singlePLace) {
        Place place = new Place();
        try {
            place.id = object.getString("place_id");
            double[] loc = new double[2];
            loc[0] = object.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
            loc[1] = object.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            place.location = loc;
            place.name = object.getString("name");
            place.address = object.getString("vicinity");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (singlePLace) {
            try {
                place.phoneNumber = object.getString("formatted_phone_number");
            } catch (JSONException e) {
                place.phoneNumber = "";
            }
            try {
                place.price = object.getInt("price_level");
            } catch (JSONException e) {
                place.price = -1;
            }

            ArrayList<String> openingHours = new ArrayList<>();
            try {
                JSONArray array = object.getJSONObject("opening_hours").getJSONArray("weekday_text");
                for (int i = 0; i < array.length(); i++) {
                    openingHours.add(array.getString(i));
                }
            } catch (JSONException e) {
                openingHours = new ArrayList<>();
            }
            place.openHours = openingHours;
        }

        return place;
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
        if(openHours.isEmpty())
        {
            openHours.add(" ");
        }
        return openHours;
    }

    public String getPrice() {
        String pricetag = "";
        if (price>=0)
        {
            for(int i = 0; i<=price; i++)
            {
                pricetag = pricetag+"$";
            }
            return pricetag;
        }
        else
        return "no price information";
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
