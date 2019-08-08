package com.example.team_project.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class Place {
    // data for an event
    private String id;
    private String location;
    private String name;
    private String address;
    private String distance;

    // with details
    private String phoneNumber;
    private String phoneToCall;
    private ArrayList<String> openHours;
    private int price;

    public static Place placeFromJson(JSONObject object, boolean singlePLace) {
        Place place = new Place();
        try {
            place.id = object.getString("place_id");
            place.location = object.getJSONObject("geometry").getJSONObject("location").getDouble("lat")
                    + " " + object.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
            place.name = object.getString("name");
            place.address = object.getString("vicinity");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (singlePLace) {
            try {
                place.phoneNumber = object.getString("formatted_phone_number");
                place.phoneToCall = object.getString("international_phone_number").split(" ")[1].replaceAll("-", "");
            } catch (JSONException e) {
                place.phoneNumber = "";
                place.phoneToCall = "";
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

    public String getLocation() {
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
        if(openHours.isEmpty()) {
            openHours.add(" ");
        }
        return openHours;
    }

    public String getPrice() {
        String pricetag = "";
        if (price>=0) {
            for(int i = 0; i<=price; i++) {
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

    public void setLocation(String location) {
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

    public String getPhoneToCall() {
        return phoneToCall;
    }
}
