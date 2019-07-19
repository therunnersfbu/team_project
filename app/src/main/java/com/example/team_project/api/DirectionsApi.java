package com.example.team_project.api;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class DirectionsApi {
    private static final String API_BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String  API_KEY = "AIzaSyAJwFw0rvA3FQzEmbC-iw6CXfyTr9PibgA";

    private static AsyncHttpClient client;

    public static void getDistance(double[] origin, double[] destination) {
        client = new AsyncHttpClient();

        String locations = "&origin=" + origin[0] + "," + origin[1] + "&destination="
                + destination[0] + "," + destination[1];
        String url = API_BASE_URL + "key=" + API_KEY + locations;

        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    String distance = response.getJSONArray("routes").getJSONObject(0)
                            .getJSONArray("legs").getJSONObject(0).getJSONObject("distance")
                            .getString("text");
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
                throwable.printStackTrace();;
            }
        });
    }
}
