package com.example.team_project.api;

import android.app.Activity;
import android.util.Log;
import android.widget.Adapter;

import com.example.team_project.DetailsActivity;
import com.example.team_project.EventsDetailsAdapter;
import com.example.team_project.MainActivity;
import com.example.team_project.SearchActivity;
import com.example.team_project.model.Event;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

import cz.msebera.android.httpclient.Header;

public class EventsApi {
    private final  String API_BASE_URL = "http://api.eventful.com/json/events/search?";
    private final String API_SINGLE_URL = "http://api.eventful.com/json/events/get?";
    private final  String  API_KEY = "sM8TM8LQGWR9Zkwr";

    private AsyncHttpClient client;
    private int page;
    private String location;
    private String keywords;
    private String date;
    private JSONArray array;
    private Object source;
    private int pageCount;

    public EventsApi(Object source) {
        this.source = source;
        client = new AsyncHttpClient();
        page = 1;
        location = "";
        keywords = "";
        date = "";
        pageCount = Integer.MAX_VALUE;
    }

    public void setLocation(String location) {
        this.location = "&location=" + location;
    }

    public void setLocation(double lat, double lng, int radius) {
        this.location = "&location=" + lat + "," + lng + "&within=" + radius;
    }

    public void setKeywords(String keywords) {
        this.keywords = "&keywords=" + keywords;
    }

    public void setDate(String date) {
        this.date = "&date=" + date;
    }

    public void getTopEvents() {
        page = 1;
        getEvents();
    }

    public void getMoreEvents() {
        page++;
        if (page + 1 >= pageCount) {
            ((SearchActivity) source).setCanGetMore(false);
        }
        getEvents();
    }

    private void getEvents() {
        String url = API_BASE_URL + "app_key=" + API_KEY + keywords + location + date + "&page_number=" + page;

        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    array = response.getJSONObject("events").getJSONArray("event");
                    pageCount = Integer.parseInt(response.getString("page_count"));
                    //TODO Memory leak
                    ((SearchActivity) source).apiFinished(array);

//                    for testing
//                    MainActivity.setArray(array);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("EventsApi", responseString);
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

    public void getSingleEvent(String id) {
        String url = API_SINGLE_URL + "app_key=" + API_KEY + "&id=" + id;

        client.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    Event event  = Event.eventFromJson(response, true);
                    ((EventsDetailsAdapter) source).finishedApi(event);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("EventsApi", responseString);
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
