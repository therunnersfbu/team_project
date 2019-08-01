package com.example.team_project.api;

import android.util.Log;
import com.example.team_project.location.LocationActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import cz.msebera.android.httpclient.Header;

//Calls to the Google Maps API to find relevant location choices
public class AutocompleteApi {
    private final String API_BASE_URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?";
    private final String  API_KEY = "AIzaSyAJwFw0rvA3FQzEmbC-iw6CXfyTr9PibgA";
    private final String PREDICTIONS_KEY = "predictions";
    private final String PLACE_ID = "place_id";
    private final String DESCRIPTION_KEY = "description";
    private final String KEY_ENDPOINT = "&key=";
    private final String INPUT_ENDPOINT = "&input=";
    private final String CLASS_TAG = "AutocompleteApi";
    private final int RESULT_LIMIT = 5;
    private AsyncHttpClient mClient;
    private String mKey;
    private String mInput;
    private ArrayList<String> mIds;
    private ArrayList<String> mNames;
    private Object mSource;

    public AutocompleteApi(Object source) {
        this.mSource = source;
        this.mKey = KEY_ENDPOINT + API_KEY;
        mClient = new AsyncHttpClient();
    }

    public void setInput(String mInput) {
        this.mInput = INPUT_ENDPOINT + mInput;
    }

    public void getTopPlaces() {
        String url = API_BASE_URL + mInput + mKey;
        mIds = new ArrayList<>();
        mNames = new ArrayList<>();
        mClient.get(url, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray(PREDICTIONS_KEY);
                    for (int i = 0; i<RESULT_LIMIT; i++) {
                        mIds.add(results.getJSONObject(i).getString(PLACE_ID));
                        mNames.add(results.getJSONObject(i).getString(DESCRIPTION_KEY));
                    }
                    ((LocationActivity) mSource).apiFinishedLocation(mIds, mNames);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(CLASS_TAG, responseString);
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
