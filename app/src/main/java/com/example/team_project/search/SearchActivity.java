package com.example.team_project.search;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
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
import butterknife.OnClick;

// Search page that populates with events that correspond to user-selected keywords
public class SearchActivity extends AppCompatActivity {

    // permission codes and constant strings
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int USER_SEARCH = -2;
    private static String CATEGORY_TAG = "category";
    private static String NAME_TAG = "name";
    private static String LOCATION_TAG = "location";
    private static String FUTURE_TAG = "Future";
    private static int DEFAULT_CAT_VALUE = -1;
    private static int REQUEST_CODE = 1;
    // tag recycler view
    private boolean isTags = true;
    private HorizontalScrollAdapter mAdapter;
    private RecyclerView.LayoutManager resultsManager;
    private LinearLayoutManager horizontalLayout;
    // tag items
    private ArrayList<String> mSubTags;
    private ArrayList<String> mTaggedResults;
    private String[] primTagRef;
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

    @BindView(R.id.rvTags) RecyclerView rvTags;
    @BindView(R.id.rvResults) RecyclerView rvResults;
    @BindView(R.id.etLocation) TextView tvLocation;
    @BindView(R.id.etSearch) TextView etSearch;

    @OnClick(R.id.etLocation)
    public void locationAct(TextView view) {
        Intent intent = new Intent(view.getContext(), LocationActivity.class);
        intent.putExtra(CATEGORY_TAG, category);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @OnClick(R.id.btnCancel)
    public void cancel(Button button) {
        finish();
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
        primTagRef = new String[]{"TrendyCity verified", "bottomless", "upscale", "young",
                "dress cute", "rooftop", "dress comfy", "insta-worthy", "outdoors", "indoors",
                "clubby", "mall", "food available", "barber", "spa", "classes", "trails",
                "gyms", "family friendly", "museums"};
        mTagReference = new ArrayList<>(Arrays.asList(primTagRef));
        //results recycler view
        rvResults.setLayoutManager(resultsManager);
        mResultsAdapter = new ResultsAdapter(mResults, mDistances, mIds, isPlace);
        rvResults.setLayoutManager(verticalLayout);
        rvResults.setAdapter(mResultsAdapter);
        //result items
        category = getIntent().getIntExtra(CATEGORY_TAG, DEFAULT_CAT_VALUE);
        isPlace = isPlace(category);
        //location services
        newLocName = getIntent().getStringExtra(NAME_TAG);

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
            ActivityCompat.requestPermissions(SearchActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
            setMyLocation();
        }
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

    @Override
    protected void onResume() {
        isCurLoc = PublicVariables.isCurLoc;
        newLoc = PublicVariables.newLoc;
        newLocName = PublicVariables.newLocName;
        super.onResume();
        if(isCurLoc){
            if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SearchActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PublicVariables.isCurLoc = true;
        setMyLocation();
    }

    private void setMyLocation() {
        longitude = getIntent().getDoubleExtra("longitude", 0.0);
        latitude = getIntent().getDoubleExtra("latitude", 0.0);
        initializeCategory(category);
        populateList();
    }

    // sets the appropriate tags and keyword depending on category
    private void initializeCategory(int category) {
        switch (category) {
            case -2:
                pApi.setKeywords(etSearch.getText().toString());
                break;
            case 0:
                mUserInput = "breakfast";
                pApi.setKeywords(mUserInput);
                mSubTags.clear();
                mSubTags.add("upscale");
                mSubTags.add("dress cute");
                mSubTags.add("dress comfy");
                mSubTags.add("insta-worthy");
                mSubTags.add("family friendly");
                mSubTags.add("TrendyCity verified");
                break;
            case 1:
                mUserInput = "brunch";
                pApi.setKeywords(mUserInput);
                mSubTags.clear();
                mSubTags.add("bottomless");
                mSubTags.add("upscale");
                mSubTags.add("dress cute");
                mSubTags.add("dress comfy");
                mSubTags.add("insta-worthy");
                mSubTags.add("family friendly");
                mSubTags.add("TrendyCity verified");
                break;
            case 2:
                mUserInput = "sweets";
                pApi.setKeywords("dessert");
                mSubTags.clear();
                mSubTags.add("upscale");
                mSubTags.add("dress cute");
                mSubTags.add("dress comfy");
                mSubTags.add("insta-worthy");
                mSubTags.add("family friendly");
                mSubTags.add("TrendyCity verified");
                break;
            case 3:
                mUserInput = "dinner";
                pApi.setKeywords(mUserInput);
                mSubTags.clear();
                mSubTags.add("upscale");
                mSubTags.add("dress cute");
                mSubTags.add("dress comfy");
                mSubTags.add("insta-worthy");
                mSubTags.add("family friendly");
                mSubTags.add("TrendyCity verified");
                break;
            case 4:
                mUserInput = "sights";
                pApi.setKeywords("museum");
                mSubTags.clear();
                mSubTags.add("upscale");
                mSubTags.add("insta-worthy");
                mSubTags.add("family friendly");
                mSubTags.add("museum");
                mSubTags.add("TrendyCity verified");
                break;
            case 5:
                mUserInput = "nightlife";
                pApi.setKeywords("bar");
                mSubTags.clear();
                mSubTags.add("upscale");
                mSubTags.add("young");
                mSubTags.add("clubby");
                mSubTags.add("food available");
                mSubTags.add("rooftop");
                mSubTags.add("TrendyCity verified");
                break;
            case 6:
                mUserInput = "shopping";
                pApi.setKeywords(mUserInput);
                mSubTags.clear();
                mSubTags.add("upscale");
                mSubTags.add("mall");
                mSubTags.add("TrendyCity verified");
                break;
            case 7:
                mUserInput = "concerts";
                eApi.setKeywords(mUserInput);
                mSubTags.clear();
                mSubTags.add("indoors");
                mSubTags.add("outdoors");
                mSubTags.add("upscale");
                mSubTags.add("food available");
                mSubTags.add("family friendly");
                mSubTags.add("TrendyCity verified");
                break;
            case 8:
                mUserInput = "pop-up events";
                eApi.setKeywords("fair");
                mSubTags.clear();
                mSubTags.add("food available");
                mSubTags.add("family friendly");
                mSubTags.add("TrendyCity verified");
                break;
            case 9:
                mUserInput = "beauty";
                pApi.setKeywords("salon");
                mSubTags.clear();
                mSubTags.add("barber");
                mSubTags.add("spa");
                mSubTags.add("family friendly");
                mSubTags.add("TrendyCity verified");
                break;
            case 10:
                mUserInput = "active";
                pApi.setKeywords("gym");
                mSubTags.clear();
                mSubTags.add("classes");
                mSubTags.add("trails");
                mSubTags.add("gyms");
                mSubTags.add("TrendyCity verified");
                break;
            case 11:
                mUserInput = "parks";
                pApi.setKeywords(mUserInput);
                mSubTags.clear();
                mSubTags.add("food available");
                mSubTags.add("family friendly");
                mSubTags.add("TrendyCity verified");
                break;
            default:
                return;
        }

    }

    // query results according to user input
    private void populateList() {
        mEventList.clear();
        mPlaceList.clear();
        mResults.clear();
        mIds.clear();
        if (!isPlace) {
            eApi.setDate(FUTURE_TAG);
            eApi.setLocation(latitude, longitude, 60);
        } else {
            pApi.setLocation(latitude, longitude);
            pApi.setRadius(10000);
        }
        mAdapter.notifyDataSetChanged();
        if(category!=-2) {
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
        boolean isStored = true;
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
                        if(mTags == null ||mPlaceEvent.getTags().get(tagIndex)==0)
                        {
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
        } else {
            for (int i = 0; i < array.length(); i++) {
                isStored=true;
                Place place = Place.placeFromJson(array.getJSONObject(i), false);
                String mId = place.getPlaceId();
                if(!mTaggedResults.isEmpty()) {
                    PlaceEvent mPlaceEvent = query(mId);
                    for(int j = 0; j<mTaggedResults.size(); j++)
                    {
                        int tagIndex = mTagReference.indexOf(mTaggedResults.get(j));
                        ArrayList<Integer> mTags = new ArrayList<>();
                        if(mPlaceEvent== null) {isStored = false; break;}
                        mTags.addAll(mPlaceEvent.getTags());
                        if(mTags == null ||mPlaceEvent.getTags().get(tagIndex)==0)
                        {
                            isStored = false;
                        }

                    }
                }
                if(isStored)
                {
                    mPlaceList.add(place);
                    dApi.addDestination(place.getLocation());
                    mResults.add(place.getPlaceName());
                    mIds.add(place.getPlaceId());
                }
            }
        }

        dApi.getDistance();
    }

    // get the distance from search results to current location
    public void getDistances(ArrayList<String> result) {
        mDistances.addAll(result);
        mResultsAdapter.notifyDataSetChanged();
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

    // find PlaceEvent parse object with same ID as api object
    private PlaceEvent query(String id) {
        ParseQuery<PlaceEvent> query = new ParseQuery("PlaceEvent");
        query.whereContains("apiId", id);
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