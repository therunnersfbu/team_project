package com.example.team_project.search;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
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
import com.example.team_project.utils.ContextProvider;
import com.example.team_project.utils.EndlessRecyclerViewScrollListener;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.view.View.GONE;

// Search page that populates with events that correspond to user-selected keywords
public class SearchActivity extends AppCompatActivity implements PlacesApi.GetPlaces, EventsApi.GetEvents, DirectionsApi.GetDistances, EventsApi.EndlessScrollingClass {
    // permission codes and constants
    private static String CATEGORY_TAG = "category";
    private static String NAME_TAG = "name";
    private static String FUTURE_TAG = "Future";
    private static String API_ID_KEY = "apiId";
    private static String CLASS_NAME_TAG = "PlaceEvent";
    private static String LONGITUDE_TAG = "longitude";
    private static String LATITUDE_TAG = "latitude";
    private static String SPLITER = "\\s+";
    private static int EVENTS_SEARCH = -3;
    private static double DEFAULT_COORD = 0.0;
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
    private ArrayList<String> mSubTags = new ArrayList<>();
    private ArrayList<String> mTaggedResults = new ArrayList<>();
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
    private ArrayList<String> mResults = new ArrayList<>();
    private ArrayList<Event> mEventList = new ArrayList<>();
    private ArrayList<Place> mPlaceList = new ArrayList<>();
    private ArrayList<String> mDistances = new ArrayList<>();
    private ArrayList<String> mIds = new ArrayList<>();
    private ArrayList<String> mAddresses = new ArrayList<>();
    //api clients
    private EventsApi eApi;
    private PlacesApi pApi;
    //location services
    private double longitude;
    private double latitude;
    private boolean isCurLoc = true;
    private String newLoc = "";
    private String newLocName;

    private enum eventCategories {
        CONCERT,
        FAIR;
        public int isPlace() {
            switch(this) {
                case CONCERT: return 7;
                case FAIR: return 8;
                default: return -5;
            }
        }
    }

    private WeakReference<PlacesApi.GetPlaces> mGetPlaces;
    private WeakReference<EventsApi.GetEvents> mGetEvents;
    private WeakReference<DirectionsApi.GetDistances> mGetDistances;

    //layout items
    @BindView(R.id.pbSpinner) ProgressBar mProgressBar;
    @BindView(R.id.rvTags) RecyclerView mRecyclerView;
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

    @OnClick(R.id.btnSearch)
    public void search(Button button) {
        mProgressBar.setVisibility(View.VISIBLE);
        if(isPlace) {
            category = USER_SEARCH;
        }
        else {
            category = EVENTS_SEARCH;
        }
        hideKeyboard(this);
        initializeCategory(category);
        populateList();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mProgressBar.setVisibility(GONE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);
        mGetPlaces = new WeakReference<>((PlacesApi.GetPlaces) this);
        mGetEvents = new WeakReference<>((EventsApi.GetEvents) this);
        mGetDistances = new WeakReference<>((DirectionsApi.GetDistances) this);

        eApi = new EventsApi(mGetEvents.get());
        pApi = new PlacesApi(mGetPlaces.get());
        // layout managers
        myManager = new LinearLayoutManager(this);
        resultsManager = new LinearLayoutManager(this);
        horizontalLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        verticalLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //tag recycler view
        mRecyclerView.setLayoutManager(myManager);
        mAdapter = new HorizontalScrollAdapter(mSubTags, mSubTags, isTags, new ContextProvider() {
            @Override
            public Context getContext() {
                return SearchActivity.this;
            }
        });
        mRecyclerView.setLayoutManager(horizontalLayout);
        mRecyclerView.setAdapter(mAdapter);
        // tag items
        mTagReference = new ArrayList<>(Arrays.asList(PublicVariables.primTagRef));
        //result items
        category = getIntent().getIntExtra(CATEGORY_TAG, USER_SEARCH);
        isPlace = isPlace(category);
        //location services
        newLocName = getIntent().getStringExtra(NAME_TAG);
        //results recycler view
        rvResults.setLayoutManager(resultsManager);
        mResultsAdapter = new ResultsAdapter(mResults, mDistances, mIds, isPlace, mAddresses);
        rvResults.setLayoutManager(verticalLayout);
        rvResults.setAdapter(mResultsAdapter);
        //set progressbar to invisible if user input window open
        if(category==USER_SEARCH) {
            mProgressBar.setVisibility(GONE);
        }
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

    // returns true if the spot is a Place type
    private boolean isPlace(int category) {
        if(category == eventCategories.CONCERT.isPlace()|| category == eventCategories.FAIR.isPlace()) {
            return false;
        }
        return true;
    }

    // new results query after input filtered by tag
    public void setNewSearchText(ArrayList<String> addTagsToSearch) {
        mProgressBar.setVisibility(View.VISIBLE);
        mTaggedResults = addTagsToSearch;
        clearLists();
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
        mProgressBar.setVisibility(GONE);
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
                    String[] newCords = newLoc.split(SPLITER);
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
        if(category == EVENTS_SEARCH) {
            if(!etSearch.getText().toString().isEmpty()) {
                eApi.setKeywords(etSearch.getText().toString());
            }
        }
        else if(category==USER_SEARCH) {
            if(!etSearch.getText().toString().isEmpty()) {
                pApi.setKeywords(etSearch.getText().toString());
            }
            etSearch.requestFocus();
        }
        else if(!isPlace) {
            mSubTags.clear();
            mSubTags.addAll(PublicVariables.getTags(category));
            mUserInput = PublicVariables.getCategoryStr(category);
            eApi.setKeywords(PublicVariables.getUserInput(category));
        }
        else {
            mSubTags.clear();
            mSubTags.addAll(PublicVariables.getTags(category));
            mUserInput = PublicVariables.getCategoryStr(category);
            pApi.setKeywords(PublicVariables.getUserInput(category));
        }
        mAdapter.notifyDataSetChanged();
    }

    private void clearLists() {
        mEventList.clear();
        mPlaceList.clear();
        mResults.clear();
        mIds.clear();
    }

    // query results according to user input
    private void populateList() {
        clearLists();
        if (!isPlace) {
            eApi.setDate(FUTURE_TAG);
            eApi.setLocation(latitude, longitude, EVENTS_RADIUS);
        } else {
            pApi.setLocation(latitude, longitude);
            pApi.setRadius(PLACES_RADIUS);
        }
        mAdapter.notifyDataSetChanged();
        if(category!=USER_SEARCH&&category!=EVENTS_SEARCH) {
            etSearch.setText(mUserInput);
        }
        if (!isPlace) {
            eApi.getTopEvents();
        } else {
            pApi.getTopPlaces();
        }
    }

    // get events from api
    @Override
    public void gotEvents(JSONArray eventsApi) {
        DirectionsApi dApi = new DirectionsApi(mGetDistances.get());
        dApi.setOrigin(latitude, longitude);
        boolean isStored;
        for (int i = 0; i < eventsApi.length(); i++) {
            isStored=true;
            Event event = null;
            try {
                event = Event.eventFromJson(eventsApi.getJSONObject(i), false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                mAddresses.add(event.getAddress());
            }
        }
        if(mEventList.isEmpty()) {
            mProgressBar.setVisibility(GONE);
        }
        dApi.getDistance();
    }

    @Override
    public void gotEvent(Event eventApi) {

    }

    // get places from api
    @Override
    public void gotPlaces(JSONArray placesApi) {
        DirectionsApi dApi = new DirectionsApi(mGetDistances.get());
        dApi.setOrigin(latitude, longitude);
        boolean isStored;

        for (int i = 0; i < placesApi.length(); i++) {
            isStored=true;
            Place place = null;
            try {
                place = Place.placeFromJson(placesApi.getJSONObject(i), false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                mAddresses.add(place.getAddress());
            }
        }
        if(mResults.isEmpty()) {
            mProgressBar.setVisibility(GONE);
        }
        dApi.getDistance();
    }

    @Override
    public void gotPlace(Place placeApi) {
    }

    // get the distance from search results to current location
    @Override
    public void gotDistances(ArrayList<String> distancesApi) {
        mProgressBar.setVisibility(GONE);
        mDistances.addAll(distancesApi);
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

    //hide emulator keyboard
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void setCanGetMore(boolean canGetMore) {
        this.canGetMore = canGetMore;
    }
}