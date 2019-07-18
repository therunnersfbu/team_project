package com.example.team_project.api;

import android.util.Log;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;

public class PlacesApi {
    private final  String API_BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private final  String  API_KEY = "AIzaSyAJwFw0rvA3FQzEmbC-iw6CXfyTr9PibgA";

    private AsyncHttpClient client;
    private String location;
    private String radius;
    private String pageToken;
    private JSONArray array;

    public PlacesApi() {
        client = new AsyncHttpClient();
        location = "";
        radius = "";
    }

    public void setLocation(double lat, double lng) {
        this.location = "&location=" + lat + "," + lng;
    }

    public void setRadius(int radius) {
        this.radius = "&radius=" + radius;
    }

    public void getTopPlaces() {
        pageToken = "";
        getPlaces();
    }

    public void getMorePlaces() {
        getPlaces();
    }

    private void getPlaces() {
        String url = API_BASE_URL + "key=" + API_KEY + location + radius + pageToken;

        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    pageToken = "&pagetoken=" + response.getString("next_page_token");
                    array = response.getJSONArray("results");
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
