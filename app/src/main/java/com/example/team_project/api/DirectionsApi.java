package com.example.team_project.api;

import android.util.Log;
import com.example.team_project.account.ProfileActivity;
import com.example.team_project.calendar.CalendarAdapter;
import com.example.team_project.fragments.EventsFragment;
import com.example.team_project.search.SearchActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import cz.msebera.android.httpclient.Header;

public class DirectionsApi {

    private static final String API_BASE_URL = "https://maps.googleapis.com/maps/api/distancematrix/json?";
    private static final String  API_KEY = "AIzaSyAJwFw0rvA3FQzEmbC-iw6CXfyTr9PibgA";

    private AsyncHttpClient client;
    private String origin;
    private String destinations;
    private ArrayList<String> distances;
    private Object source;

    public DirectionsApi(Object source) {
        client = new AsyncHttpClient();
        destinations = "&destinations=";
        distances = new ArrayList<>();
        this.source = source;
    }

    public void setOrigin(double lat, double lng) {
        origin = "&origins=" + lat + "," + lng;
    }

    public void addDestination(String destination) {
        destinations += destination.replace(" ", ",") + "|";
    }

    public void getDistance() {
        String url = API_BASE_URL + "key=" + API_KEY + "&units=imperial" +
                origin + destinations.substring(0, destinations.length() - 1);

        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray array = response.getJSONArray("rows").getJSONObject(0).getJSONArray("elements");
                    for (int i = 0; i < array.length(); i++) {
                        distances.add(array.getJSONObject(i).getJSONObject("distance").getString("text"));
                    }

                    if (source instanceof SearchActivity) {
                        ((SearchActivity) source).getDistances(distances);
                    } else if (source instanceof EventsFragment) {
                        ((EventsFragment) source).gotDistances(distances);
                    } else if (source instanceof ProfileActivity) {
                        ((ProfileActivity) source).gotDistances(distances);
                    } else if (source instanceof CalendarAdapter) {
                        ((CalendarAdapter) source).gotDistance(distances.get(0));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("Directions", responseString);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();
            }
        });
    }
}
