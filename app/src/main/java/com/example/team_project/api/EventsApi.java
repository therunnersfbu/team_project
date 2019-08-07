package com.example.team_project.api;

import android.util.Log;

import com.example.team_project.PublicVariables;
import com.example.team_project.details.EventsDetailsAdapter;
import com.example.team_project.fragments.EventsFragment;
import com.example.team_project.search.SearchActivity;
import com.example.team_project.model.Event;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import cz.msebera.android.httpclient.Header;

public class EventsApi {

    private final  String API_BASE_URL = "http://api.eventful.com/json/events/search?";
    private final String API_SINGLE_URL = "http://api.eventful.com/json/events/get?";
    private final  String  API_KEY = PublicVariables.eventfulApi;

    private AsyncHttpClient client;
    private int page;
    private String location;
    private String keywords;
    private String date;
    private JSONArray array;
    private Object classToNotify;
    private int pageCount;

    public EventsApi(Object classToNotify) {
        if (classToNotify == null) {
            throw  new IllegalArgumentException();
        }
        this.classToNotify = classToNotify;
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
            ((EndlessScrollingClass) classToNotify).setCanGetMore(false);
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

                    ((GetEvents) classToNotify).gotEvents(array);
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
                    ((GetEvents) classToNotify).gotEvent(event);
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

    public interface GetEvents {
        void gotEvents(JSONArray eventsApi);
        void gotEvent(Event eventApi);
    }

    public interface EndlessScrollingClass {
        void setCanGetMore(boolean canGetMore);
    }
}
