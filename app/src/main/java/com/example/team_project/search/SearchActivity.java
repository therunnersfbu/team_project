package com.example.team_project.search;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.team_project.R;
import com.example.team_project.api.AutocompleteApi;
import com.example.team_project.api.DirectionsApi;
import com.example.team_project.api.EventsApi;
import com.example.team_project.api.PlacesApi;
import com.example.team_project.location.LocationActivity;
import com.example.team_project.location.LocationAdapter;
import com.example.team_project.model.Event;
import com.example.team_project.model.Place;
import com.example.team_project.model.PlaceEvent;
import com.example.team_project.utils.EndlessRecyclerViewScrollListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

// Search page that populates with events that correspond to user-selected keywords
public class SearchActivity extends AppCompatActivity implements LocationListener, GoogleApiClient.OnConnectionFailedListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private RecyclerView rvTags;
    private RecyclerView rvResults;
    private boolean isTags;
    private ArrayList<String> mNames;
    private ArrayList<String> mResults;
    private HorizontalScrollAdapter mAdapter;
    private ResultsAdapter mResultsAdapter;
    private int category;
    private EndlessRecyclerViewScrollListener scrollListener;
    private ArrayList<Event> mEventList;
    private ArrayList<Place> mPlaceList;
    private double longitude;
    private double latitude;
    private Location location;
    private LocationManager locManager;
    private ArrayList<String> distances;
    private ArrayList<String> ids;
    private boolean isPlace;
    private EventsApi eApi;
    private PlacesApi pApi;
    private AutocompleteApi lApi;
    private boolean canGetMore;
    private TextView etSearch;
    private TextView tvLocation;
    private boolean isCurLoc;
    private String newLoc;
    private String newLocName;
    private Button btnCancel;
    private Button btnSearch;
    private ArrayList<String> mSubTags;
    private ArrayList<String> mTaggedResults;
    private String[] primTagRef;
    private ArrayList<String> tagReference;

    RecyclerView.LayoutManager myManager;
    RecyclerView.LayoutManager resultsManager;
    LinearLayoutManager horizontalLayout;
    LinearLayoutManager verticalLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initializeVars();

        primTagRef = new String[]{"TrendyCity verified", "bottomless", "upscale", "young",
                "dress cute", "rooftop", "dress comfy", "insta-worthy", "outdoors", "indoors",
                "clubby", "mall", "food available", "barber", "spa", "classes", "trails",
                "gyms", "family friendly", "museums"};
        tagReference = new ArrayList<String>(Arrays.asList(primTagRef));
        category = getIntent().getIntExtra("category", -1);
        isTags = true;
        isPlace = true;
        canGetMore = true;
        if(category == 7 || category == 8) isPlace = false;
        rvTags = findViewById(R.id.rvTags);
        rvResults = findViewById(R.id.rvResults);
        rvTags.setLayoutManager(myManager);
        rvResults.setLayoutManager(resultsManager);
        mAdapter = new HorizontalScrollAdapter(mSubTags, isTags, this);
        mResultsAdapter = new ResultsAdapter(mResults, distances, ids, isPlace);
        rvTags.setLayoutManager(horizontalLayout);
        rvTags.setAdapter(mAdapter);
        rvResults.setLayoutManager(verticalLayout);
        rvResults.setAdapter(mResultsAdapter);
        tvLocation = findViewById(R.id.etLocation);
        etSearch = findViewById(R.id.etSearch);
        isCurLoc = true;
        newLoc = "";
        newLocName = getIntent().getStringExtra("name");
        btnCancel = findViewById(R.id.btnCancel);
        btnSearch = findViewById(R.id.btnSearch);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                category = -2;
                populateList();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), LocationActivity.class);
                intent.putExtra("category", category);
                startActivity(intent);
            }
        });

        scrollListener = new EndlessRecyclerViewScrollListener(verticalLayout) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (canGetMore) {
                    Log.d("searchActivity", "endless scroll");
                    if (!isPlace) {
                        eApi.getMoreEvents();
                    } else {
                        pApi.getMorePlaces();
                    }
                }
            }
        };

        // Adds the scroll listener to RecyclerView
        rvResults.addOnScrollListener(scrollListener);

        if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(SearchActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
            Log.d("location", "first");
            setMyLocation();
        }
    }

    public void setNewSearchText(ArrayList<String> addTagsToSearch) {
        mTaggedResults = addTagsToSearch;
        String mSearch = etSearch.getText().toString();
        etSearch.setText(mSearch + " " + addTagsToSearch.get(addTagsToSearch.size()-1));
        mEventList.clear();
        mPlaceList.clear();
        mResults.clear();
        ids.clear();
        mResultsAdapter.notifyDataSetChanged();
        if (!isPlace) {
            eApi.getTopEvents();
        } else {
            pApi.getTopPlaces();
        }
    }

    @Override
    protected void onResume() {
        isCurLoc = LocationAdapter.isCurLoc;
        newLoc = LocationAdapter.mNewLoc;
        newLocName = LocationAdapter.mLocName;
        super.onResume();
        if(isCurLoc){
            if (ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SearchActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(SearchActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }else{
                Log.d("location", "first");
                setMyLocation();
            }
        } else {
            String[] newCords = newLoc.split("\\s+");
            latitude = Double.parseDouble(newCords[0]);
            longitude = Double.parseDouble(newCords[1]);
            tvLocation.setText(newLocName);
            populateList();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("location", "granted");
                    Log.d("location", "second");
                    setMyLocation();
                } else {
                    Log.d("location", "not granted");
                }
                return;
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LocationAdapter.isCurLoc = true;
        setMyLocation();

    }

    private void setMyLocation() {
        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean network_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (network_enabled) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more .
                Log.e("location", "no permission");
                return;
            }
            location = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if(location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
                longitude = location.getLongitude();
                latitude = location.getLatitude();
                populateList();
            }
            else {
                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        }

        Log.d("location", longitude + ", " + latitude);
    }

    private void populateList() {
        mEventList.clear();
        mPlaceList.clear();
        mResults.clear();
        ids.clear();
        String mCategory = "";
        if (!isPlace) {
            eApi.setDate("Future");
            eApi.setLocation(latitude, longitude, 60);
        } else {
            pApi.setLocation(latitude, longitude);
            pApi.setRadius(10000);
        }
        switch (category) {
            case -2:
                pApi.setKeywords(etSearch.getText().toString());
                break;
            case 0:
                mCategory = "breakfast";
                pApi.setKeywords(mCategory);
                mSubTags.clear();
                mSubTags.add("upscale");
                mSubTags.add("dress cute");
                mSubTags.add("dress comfy");
                mSubTags.add("insta-worthy");
                mSubTags.add("family friendly");
                mSubTags.add("TrendyCity verified");
                break;
            case 1:
                mCategory = "brunch";
                pApi.setKeywords(mCategory);
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
                mCategory = "sweets";
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
                mCategory = "dinner";
                pApi.setKeywords(mCategory);
                mSubTags.clear();
                mSubTags.add("upscale");
                mSubTags.add("dress cute");
                mSubTags.add("dress comfy");
                mSubTags.add("insta-worthy");
                mSubTags.add("family friendly");
                mSubTags.add("TrendyCity verified");
                break;
            case 4:
                mCategory = "sights";
                pApi.setKeywords("museum");
                mSubTags.clear();
                mSubTags.add("upscale");
                mSubTags.add("insta-worthy");
                mSubTags.add("family friendly");
                mSubTags.add("museum");
                mSubTags.add("TrendyCity verified");
                break;
            case 5:
                mCategory = "nightlife";
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
                mCategory = "shopping";
                pApi.setKeywords(mCategory);
                mSubTags.clear();
                mSubTags.add("upscale");
                mSubTags.add("mall");
                mSubTags.add("TrendyCity verified");
                break;
            case 7:
                mCategory = "concerts";
                eApi.setKeywords(mCategory);
                mSubTags.clear();
                mSubTags.add("indoors");
                mSubTags.add("outdoors");
                mSubTags.add("upscale");
                mSubTags.add("food available");
                mSubTags.add("family friendly");
                mSubTags.add("TrendyCity verified");
                break;
            case 8:
                mCategory = "pop-up events";
                eApi.setKeywords("fair");
                mSubTags.clear();
                mSubTags.add("food available");
                mSubTags.add("family friendly");
                mSubTags.add("TrendyCity verified");
                break;
            case 9:
                mCategory = "beauty";
                pApi.setKeywords("salon");
                mSubTags.clear();
                mSubTags.add("barber");
                mSubTags.add("spa");
                mSubTags.add("family friendly");
                mSubTags.add("TrendyCity verified");
                break;
            case 10:
                mCategory = "active";
                pApi.setKeywords("gym");
                mSubTags.clear();
                mSubTags.add("classes");
                mSubTags.add("trails");
                mSubTags.add("gyms");
                mSubTags.add("TrendyCity verified");
                break;
            case 11:
                mCategory = "parks";
                pApi.setKeywords(mCategory);
                mSubTags.clear();
                mSubTags.add("food available");
                mSubTags.add("family friendly");
                mSubTags.add("TrendyCity verified");
                break;
            default:
                return;
        }
        mAdapter.notifyDataSetChanged();
        if(category!=-2) {etSearch.setText(mCategory);}
        if (!isPlace) {
            eApi.getTopEvents();
        } else {
            pApi.getTopPlaces();
        }
    }

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
                    for(int j = 0; j<mTaggedResults.size(); j++)
                    {
                        int tagIndex = tagReference.indexOf(mTaggedResults.get(j));
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
                    ids.add(event.getEventId());
                    mEventList.add(event);
                    dApi.addDestination(event.getLocation());
                    mResults.add(event.getEventName());
                }
            }
        } else {
            for (int i = 0; i < array.length(); i++) {
                Place place = Place.placeFromJson(array.getJSONObject(i), false);
                String mId = place.getPlaceId();
                if(!mTaggedResults.isEmpty()) {
                    PlaceEvent mPlaceEvent = query(mId);
                    for(int j = 0; j<mTaggedResults.size(); j++)
                    {
                        int tagIndex = tagReference.indexOf(mTaggedResults.get(j));
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
                    ids.add(place.getPlaceId());
                }
            }
        }

        dApi.getDistance();
    }

    public void getDistances(ArrayList<String> result) {
        distances.addAll(result);
        mResultsAdapter.notifyDataSetChanged();
    }


    public void initializeVars()
    {
        mEventList = new ArrayList<>();
        mPlaceList = new ArrayList<>();
        mResults = new ArrayList<>();
        distances = new ArrayList<>();
        mSubTags = new ArrayList<>();
        mTaggedResults = new ArrayList<>();
        ids = new ArrayList<>();
        myManager = new LinearLayoutManager(this);
        resultsManager = new LinearLayoutManager(this);
        horizontalLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        verticalLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        eApi = new EventsApi(this);
        pApi = new PlacesApi(this);
        lApi = new AutocompleteApi(this);
    }

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

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.v("Location  Changed", location.getLatitude() + " and " + location.getLongitude());
            this.location = location;
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Log.d("location", longitude + ", " + latitude);
            locManager.removeUpdates(this);
            populateList();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void setCanGetMore(boolean canGetMore) {
        this.canGetMore = canGetMore;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}