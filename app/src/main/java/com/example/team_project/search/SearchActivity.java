package com.example.team_project.search;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.TextView;
import com.example.team_project.PublicVariables;
import com.example.team_project.R;
import com.example.team_project.api.DirectionsApi;
import com.example.team_project.api.EventsApi;
import com.example.team_project.api.PlacesApi;
import com.example.team_project.location.LocationActivity;
import com.example.team_project.model.Event;
import com.example.team_project.model.Place;
import com.example.team_project.model.PlaceEvent;
import com.example.team_project.utils.EndlessRecyclerViewScrollListener;
import com.parse.ParseException;
import com.parse.ParseQuery;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Arrays;
import butterknife.BindView;
import butterknife.ButterKnife;
import android.support.annotation.Nullable;
import butterknife.OnClick;

// Search page that populates with events that correspond to user-selected keywords
public class SearchActivity extends AppCompatActivity {
    // permission codes and constants
    private static String CATEGORY_TAG = "category";
    private static String NAME_TAG = "name";
    private static String FUTURE_TAG = "Future";
    private static String API_ID_KEY = "apiId";
    private static String CLASS_NAME_TAG = "PlaceEvent";
    private static String LONGITUDE_TAG = "longitude";
    private static String LATITUDE_TAG = "latitude";
    private static double DEFAULT_COORD = 0.0;
    private static int DEFAULT_CAT_VALUE = -1;
    private static final int USER_SEARCH = -2;
    private static int REQUEST_CODE = 1;
    private static int PLACES_RADIUS = 10000;
    private static int EVENTS_RADIUS = 60;
    // tag recycler view
    private boolean isTags = true;
    private HorizontalScrollAdapter mAdapter;
    private RecyclerView.LayoutManager resultsManager;
    private LinearLayoutManager horizontalLayout;
    // tag items
    private ArrayList<String> mSubTags;
    private ArrayList<String> mTaggedResults;
    private ArrayList<String> mTagReference;
    //results recycler view
    private ResultsAdapter mResultsAdapter;
    private RecyclerView.LayoutManager myManager;
    private LinearLayoutManager verticalLayout;
    //result items
    private int category;
    private boolean isPlace = true;
    private boolean canGetMore = true;
    private String mUserInput = "";
    private ArrayList<String> mResults;
    private ArrayList<Event> mEventList;
    private ArrayList<Place> mPlaceList;
    private ArrayList<String> mDistances;
    private ArrayList<String> mIds;
    //api clients
    private EventsApi eApi;
    private PlacesApi pApi;
    //location services
    private double longitude;
    private double latitude;
    private boolean isCurLoc = true;
    private String newLoc = "";
    private String newLocName;
    //layout items
    @BindView(R.id.rvTags) RecyclerView rvTags;
    @BindView(R.id.rvResults) RecyclerView rvResults;
    @BindView(R.id.etLocation) TextView tvLocation;
    @BindView(R.id.etSearch) TextView etSearch;

    //OnClick listeners for buttons
    @OnClick(R.id.etLocation)
    public void locationAct(TextView view) {
        Intent intent = new Intent(view.getContext(), LocationActivity.class);
        intent.putExtra(CATEGORY_TAG, category);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @OnClick(R.id.btnCancel)
    public void cancel(Button button) {
        finish();
        PublicVariables.isCurLoc = true;
        setMyLocation();
    }

    @OnClick(R.id.btnSearch)
    public void search(Button button) {
        category = USER_SEARCH;
        initializeCategory(category);
        populateList();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        initializeVars();
        //tag recycler view
        rvTags.setLayoutManager(myManager);
        mAdapter = new HorizontalScrollAdapter(mSubTags, isTags, this);
        rvTags.setLayoutManager(horizontalLayout);
        rvTags.setAdapter(mAdapter);
        // tag items
        mTagReference = new ArrayList<>(Arrays.asList(PublicVariables.primTagRef));
        //result items
        category = getIntent().getIntExtra(CATEGORY_TAG, DEFAULT_CAT_VALUE);
        isPlace = isPlace(category);
        //location services
        newLocName = getIntent().getStringExtra(NAME_TAG);
        //layout items
        tvLocation = findViewById(R.id.etLocation);
        //results recycler view
        rvResults.setLayoutManager(resultsManager);
        mResultsAdapter = new ResultsAdapter(mResults, mDistances, mIds, isPlace);
        rvResults.setLayoutManager(verticalLayout);
        rvResults.setAdapter(mResultsAdapter);
        // Adds the scroll listener to RecyclerView
        rvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(verticalLayout) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (canGetMore) {
                    if (!isPlace) {
                        eApi.getMoreEvents();
                    } else {
                        pApi.getMorePlaces();
                    }
                }
            }
        });
        //set location
        if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SearchActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }else{
            setMyLocation();
        }
    }

    // initialize variables used in this class
    public void initializeVars() {
        mEventList = new ArrayList<>();
        mPlaceList = new ArrayList<>();
        mResults = new ArrayList<>();
        mDistances = new ArrayList<>();
        mSubTags = new ArrayList<>();
        mTaggedResults = new ArrayList<>();
        mIds = new ArrayList<>();
        myManager = new LinearLayoutManager(this);
        resultsManager = new LinearLayoutManager(this);
        horizontalLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        verticalLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        eApi = new EventsApi(this);
        pApi = new PlacesApi(this);
    }

    // returns true if the spot is a Place type
    private boolean isPlace(int category) {
        if(category == 7 || category == 8) {
            return false;
        }
        return true;
    }

    // new results query after input filtered by tag
    public void setNewSearchText(ArrayList<String> addTagsToSearch) {
        mTaggedResults = addTagsToSearch;
        mEventList.clear();
        mPlaceList.clear();
        mResults.clear();
        mIds.clear();
        mResultsAdapter.notifyDataSetChanged();
        if (!isPlace) {
            eApi.getTopEvents();
        } else {
            pApi.getTopPlaces();
        }
    }

    //After LocationActivity has finished setting new current location, query new search results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                isCurLoc = PublicVariables.isCurLoc;
                newLoc = PublicVariables.newLoc;
                newLocName = PublicVariables.newLocName;
                super.onResume();
                if(isCurLoc){
                    if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(SearchActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                    }else{
                        setMyLocation();
                    }
                } else {
                    String[] newCords = newLoc.split("\\s+");
                    latitude = Double.parseDouble(newCords[0]);
                    longitude = Double.parseDouble(newCords[1]);
                    tvLocation.setText(newLocName);
                    initializeCategory(category);
                    populateList();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PublicVariables.isCurLoc = true;
        setMyLocation();
    }

    private void setMyLocation() {
        longitude = getIntent().getDoubleExtra(LONGITUDE_TAG, DEFAULT_COORD);
        latitude = getIntent().getDoubleExtra(LATITUDE_TAG, DEFAULT_COORD);
        initializeCategory(category);
        populateList();
    }

    // sets the appropriate tags and keyword depending on category
    private void initializeCategory(int category) {
        if(category==USER_SEARCH) {
            pApi.setKeywords(etSearch.getText().toString());
        }
        else {
            mSubTags.clear();
            mSubTags.addAll(PublicVariables.getTags(category));
            mUserInput = PublicVariables.getCategoryStr(category);
            pApi.setKeywords(PublicVariables.getUserInput(category));
        }
        mAdapter.notifyDataSetChanged();
    }

    // query results according to user input
    private void populateList() {
        mEventList.clear();
        mPlaceList.clear();
        mResults.clear();
        mIds.clear();
        if (!isPlace) {
            eApi.setDate(FUTURE_TAG);
            eApi.setLocation(latitude, longitude, EVENTS_RADIUS);
        } else {
            pApi.setLocation(latitude, longitude);
            pApi.setRadius(PLACES_RADIUS);
        }
        mAdapter.notifyDataSetChanged();
        if(category!=USER_SEARCH) {
            etSearch.setText(mUserInput);
        }
        if (!isPlace) {
            eApi.getTopEvents();
        } else {
            pApi.getTopPlaces();
        }
    }

    // API call finished, data available
    public void apiFinished(JSONArray array) throws JSONException {
        DirectionsApi dApi = new DirectionsApi(this);
        dApi.setOrigin(latitude, longitude);
        boolean isStored;
        if (!isPlace) {
            for (int i = 0; i < array.length(); i++) {
                isStored=true;
                Event event = Event.eventFromJson(array.getJSONObject(i), false);
                String mId = event.getEventId();
                if(!mTaggedResults.isEmpty()) {
                    PlaceEvent mPlaceEvent = query(mId);
                    for(int j = 0; j<mTaggedResults.size(); j++) {
                        int tagIndex = mTagReference.indexOf(mTaggedResults.get(j));
                        ArrayList<Integer> mTags = new ArrayList<>();
                        if(mPlaceEvent== null) {isStored = false; break;}
                        mTags.addAll(mPlaceEvent.getTags());
                        if(mTags == null ||mPlaceEvent.getTags().get(tagIndex)==0) {
                            isStored = false;
                        }

                    }
                }
                if(isStored) {
                    mIds.add(event.getEventId());
                    mEventList.add(event);
                    dApi.addDestination(event.getLocation());
                    mResults.add(event.getEventName());
                }
            }
            if(mResults.size()<20) {
                eApi.getMoreEvents();
            }
        } else {
            for (int i = 0; i < array.length(); i++) {
                isStored=true;
                Place place = Place.placeFromJson(array.getJSONObject(i), false);
                String mId = place.getPlaceId();
                if(!mTaggedResults.isEmpty()) {
                    PlaceEvent mPlaceEvent = query(mId);
                    for(int j = 0; j<mTaggedResults.size(); j++) {
                        int tagIndex = mTagReference.indexOf(mTaggedResults.get(j));
                        ArrayList<Integer> mTags = new ArrayList<>();
                        if(mPlaceEvent== null) {isStored = false; break;}
                        mTags.addAll(mPlaceEvent.getTags());
                        if(mTags == null ||mPlaceEvent.getTags().get(tagIndex)==0) {
                            isStored = false;
                        }
                    }
                }
                if(isStored) {
                    mPlaceList.add(place);
                    dApi.addDestination(place.getLocation());
                    mResults.add(place.getPlaceName());
                    mIds.add(place.getPlaceId());
                }
            }
            if(mResults.size()<20) {
                pApi.getMorePlaces();
            }
        }
        dApi.getDistance();
    }

    // get the distance from search results to current location
    public void getDistances(ArrayList<String> result) {
        mDistances.addAll(result);
        mResultsAdapter.notifyDataSetChanged();
    }

    // find PlaceEvent parse object with same ID as api object
    private PlaceEvent query(String id) {
        ParseQuery<PlaceEvent> query = new ParseQuery(CLASS_NAME_TAG);
        query.whereContains(API_ID_KEY, id);
        PlaceEvent mPlaceEvent = null;
        try {
            mPlaceEvent = (PlaceEvent) query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mPlaceEvent;
    }

    public void setCanGetMore(boolean canGetMore) {
        this.canGetMore = canGetMore;
    }

}