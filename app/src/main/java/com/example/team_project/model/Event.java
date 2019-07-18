package com.example.team_project.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Event {
    private String id;
    private double[] location;
    private String name;
    private String venueName;
    private String address;
    private String startTime;

    public static Event eventFromJson(JSONObject object) throws JSONException {
        Event event = new Event();
        event.id = object.getString("id");
        double[] loc = new double[2];
        loc[0] = Double.parseDouble(object.getString("latitude"));
        loc[1] = Double.parseDouble(object.getString("longitude"));
        event.location = loc;
        event.name = object.getString("title");
        event.venueName = object.getString("venue_name");
        String postalCode =  object.getString("postal_code").equals("null") ? "" : " " + object.getString("postal_code");
        event.address = object.getString("venue_address") + ", " + object.getString("city_name")
                + ", " + object.getString("region_abbr") + postalCode
                + ", " + object.getString("country_abbr");
        event.startTime = object.getString("start_time");
        return event;
    }

    public void setEventId(String id) {
        this.id = id;
    }

    public void setLocation(double[] location) {
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

    public double[] getLocation() {
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
}
