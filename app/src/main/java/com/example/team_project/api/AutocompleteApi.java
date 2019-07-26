package com.example.team_project.api;

import android.util.Log;
import com.example.team_project.EventsDetailsAdapter;
import com.example.team_project.LocationActivity;
import com.example.team_project.SearchActivity;
import com.example.team_project.model.Place;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class AutocompleteApi {
    private final String API_BASE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?";
    private final String  API_KEY = "AIzaSyAJwFw0rvA3FQzEmbC-iw6CXfyTr9PibgA";

    private AsyncHttpClient client;
    private String location;
    private String radius;
    private String key;
    private String input;
    private JSONArray array;
    private ArrayList<String> ids;
    private Object source;

    public AutocompleteApi(Object source) {
        this.source = source;
        this.key = "&key="+API_KEY;
        client = new AsyncHttpClient();
        location = "";
        radius = "";
        array = new JSONArray();
    }

    public void getTopPlaces() {
        array = new JSONArray();
        getPlaces();
    }

    public void setInput(String input) {
        this.input = "input=" + input;
    }

    public void getMorePlaces() {
        array = new JSONArray();
        getPlaces();
    }

    private void getPlaces() {
        String url = API_BASE_URL + input + key;
        ids = new ArrayList<>();
        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("predictions");
                    for (int i = 0; i < results.length()||i<5; i++) {
                        ids.add(results.getJSONObject(i).getString("place_id"));
                    }
                    ((LocationActivity) source).apiFinishedLocation(ids);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("Places", responseString);
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                throwable.printStackTrace();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                throwable.printStackTrace();;
            }
        });
    }
}
