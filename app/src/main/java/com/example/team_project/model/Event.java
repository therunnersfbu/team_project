package com.example.team_project.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Event {
    private String id;
    private String location;
    private String name;
    private String venueName;
    private String address;
    private String startTime;
    private String distance;

    public static Event eventFromJson(JSONObject object, boolean singleEvent) throws JSONException {
        Event event = new Event();
        event.id = object.getString("id");
        event.location = object.getString("latitude") + " " + object.getString("longitude");
        event.name = object.getString("title");
        event.venueName = object.getString("venue_name");
//        String postalCode =  object.getString("postal_code").equals("null") ? "" : " " + object.getString("postal_code");
        event.address = singleEvent ?
                object.getString("address") + ", " + object.getString("city") :
                object.getString("venue_address") + ", " + object.getString("city_name");
        event.startTime = object.getString("start_time");
        return event;
    }

    public void setEventId(String id) {
        this.id = id;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setEventName(String name) {
        this.name = name;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEventId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public String getEventName() {
        return name;
    }

    public String getVenueName() {
        return venueName;
    }

    public String getAddress() {
        return address;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDistance() {
        return distance;
    }
}
