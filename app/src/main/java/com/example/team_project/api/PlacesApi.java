package com.example.team_project.api;

import android.util.Log;

import com.example.team_project.MainActivity;
import com.example.team_project.model.Place;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class PlacesApi {
    private final String API_BASE_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    private final String  API_KEY = "AIzaSyAJwFw0rvA3FQzEmbC-iw6CXfyTr9PibgA";
    private final String API_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";

    private AsyncHttpClient client;
    private String location;
    private String radius;
    private String pageToken;
    private String keywords;
    private JSONArray array;

    public PlacesApi() {
        client = new AsyncHttpClient();
        location = "";
        radius = "";
        array = new JSONArray();
    }

    public void setLocation(double lat, double lng) {
        this.location = "&location=" + lat + "," + lng;
    }

    public void setRadius(int radius) {
        this.radius = "&radius=" + radius;
    }

    public void getTopPlaces() {
        pageToken = "";
        array = new JSONArray();
        getPlaces();
    }

    public void setKeywords(String keywords) {
        this.keywords = "&keyword=" + keywords;
    }

    public void getMorePlaces() {
        getPlaces();
    }

    private void getPlaces() {
        String url = API_BASE_URL + "key=" + API_KEY + location + radius + pageToken + keywords;

        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    pageToken = "&pagetoken=" + response.getString("next_page_token");
                    JSONArray results = response.getJSONArray("results");
                    for (int i = 0; i < results.length(); i++) {
                        boolean isCity = false;
                        JSONArray types = results.getJSONObject(i).getJSONArray("types");
                        for (int j = 0; j < types.length(); j++) {
                            if ("locality".equals(types.getString(j))) {
                                isCity = true;
                                break;
                            }
                        }
                        if (!isCity) {
                            array.put(results.getJSONObject(i));
                        }
                    }

//                     for testing
//                     MainActivity.setArray(array);
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

    public void setDetails(final Place place) {
        String id = place.getPlaceId();
        String url = API_DETAILS_URL + "key=" + API_KEY + "&placeid=" + id;

        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONObject details = null;
                try {
                    details = response.getJSONObject("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    place.setPhoneNumber(details.getString("formatted_phone_number"));
                } catch (JSONException e) {
                    place.setPhoneNumber("Not available!");
                }

                try {
                    JSONArray arrayTemp = details.getJSONObject("opening_hours").getJSONArray("weekday_text");
                    ArrayList<String> list = new ArrayList<>();
                    for(int i = 0; i < arrayTemp.length(); i++){
                        list.add(arrayTemp.getString(i));
                    }
                    place.setOpenHours(list);
                } catch (JSONException e) {
                    ArrayList<String> list = new ArrayList<>();
                    list.add("Not available!");
                    place.setOpenHours(list);
                }

                try {
                    place.setPrice(details.getInt("price_level"));
                } catch (JSONException e) {
                    place.setPrice(-1);
                }

                place.setDetailsFinished();
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
