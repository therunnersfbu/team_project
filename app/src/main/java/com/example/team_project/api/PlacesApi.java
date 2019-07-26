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
    private Object source;

    public PlacesApi(Object source) {
        this.source = source;
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
        array = new JSONArray();
        getPlaces();
    }

    private void getPlaces() {
        String url = API_BASE_URL + "key=" + API_KEY + location + radius + pageToken + keywords;

        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    pageToken = "&pagetoken=" + response.getString("next_page_token");
                } catch (JSONException e) {
                    ((SearchActivity) source).setCanGetMore(false);
                    pageToken = "";
                }
                try {
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
                    ((SearchActivity) source).apiFinished(array);
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

    public void getDetails(String id) {
        String url = API_DETAILS_URL + "key=" + API_KEY + "&placeid=" + id;

        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Place place = Place.placeFromJson(response.getJSONObject("result"), true);
                    //TODO Memory leak!
                    ((EventsDetailsAdapter) source).finishedApiPlace(place);
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

    public void getLocation(String id) {
        String url = API_DETAILS_URL + "key=" + API_KEY + "&placeid=" + id;

        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Place place = Place.placeFromJson(response.getJSONObject("result"), true);
                    //TODO Memory leak!
                    double[] doubLocs = place.getLocation();
                    String mLocs = doubLocs[1]+ " " + doubLocs[2];
                    ((LocationActivity) source).apiFinishedGetLocation(mLocs, place.getPlaceName());
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
