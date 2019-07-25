package com.example.team_project;

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
import android.widget.Toast;

import com.example.team_project.api.DirectionsApi;
import com.example.team_project.api.EventsApi;
import com.example.team_project.api.PlacesApi;
import com.example.team_project.model.Event;
import com.example.team_project.model.Place;
import com.example.team_project.utils.EndlessRecyclerViewScrollListener;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.compat.ui.PlacePicker;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.Calendar;

// Search page that populates with events that correspond to user-selected keywords
public class SearchActivity extends AppCompatActivity implements LocationListener {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private final int PLACE_PICKER_REQUEST = 160;
    private RecyclerView rvTags;
    private RecyclerView rvResults;
    private boolean isTags;
    private ArrayList<String> names;
    private ArrayList<String> results;
    private HorizontalScrollAdapter adapter;
    private ResultsAdapter resultsAdapter;
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
    private boolean canGetMore;

    RecyclerView.LayoutManager myManager;
    RecyclerView.LayoutManager resultsManager;
    LinearLayoutManager horizontalLayout;
    LinearLayoutManager verticalLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initializeVars();

        category = getIntent().getIntExtra("category", -1);
        isTags = true;
        isPlace = true;
        canGetMore = true;
        if(category == 7 || category == 8) isPlace = false;
        rvTags = findViewById(R.id.rvTags);
        rvResults = findViewById(R.id.rvResults);
        rvTags.setLayoutManager(myManager);
        rvResults.setLayoutManager(resultsManager);
        addTags();
        adapter = new HorizontalScrollAdapter(names, isTags);
        resultsAdapter = new ResultsAdapter(results, distances, ids, isPlace);
        rvTags.setLayoutManager(horizontalLayout);
        rvTags.setAdapter(adapter);
        rvResults.setLayoutManager(verticalLayout);
        rvResults.setAdapter(resultsAdapter);

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
                // for ActivityCompat#requestPermissions for more details.
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
        if (!isPlace) {
            eApi.setDate("Future");
            eApi.setLocation(latitude, longitude, 60);
        } else {
            pApi.setLocation(latitude, longitude);
            pApi.setRadius(10000);
        }
        switch (category) {
            case 0:
                pApi.setKeywords("breakfast");
                break;
            case 1:
                pApi.setKeywords("brunch");
                break;
            case 2:
                pApi.setKeywords("lunch");
                break;
            case 3:
                pApi.setKeywords("dinner");
                break;
            case 4:
                pApi.setKeywords("museum");
                break;
            case 5:
                pApi.setKeywords("bar");
                break;
            case 6:
                pApi.setKeywords("shopping");
                break;
            case 7:
                eApi.setKeywords("concert");
                break;
            case 8:
                eApi.setKeywords("fair");
                break;
            case 9:
                pApi.setKeywords("salon");
                break;
            case 10:
                pApi.setKeywords("gym");
                break;
            case 11:
                pApi.setKeywords("park");
                break;
            default:
                return;
        }
        if (!isPlace) {
            eApi.getTopEvents();
        } else {
            pApi.getTopPlaces();
        }
    }

    public void apiFinished(JSONArray array) throws JSONException {
        DirectionsApi dApi = new DirectionsApi(this);
        dApi.setOrigin(latitude, longitude);

        if (!isPlace) {
            for (int i = 0; i < array.length(); i++) {
                Event event = Event.eventFromJson(array.getJSONObject(i), false);
                mEventList.add(event);
                dApi.addDestination(event.getLocation());
                results.add(event.getEventName());
                ids.add(event.getEventId());
            }
        } else {
            for (int i = 0; i < array.length(); i++) {
                Place place = Place.placeFromJson(array.getJSONObject(i), false);
                mPlaceList.add(place);
                dApi.addDestination(place.getLocation());
                results.add(place.getPlaceName());
                ids.add(place.getPlaceId());
            }
        }

        dApi.getDistance();
    }

    public void getDistances(ArrayList<String> result) {
        distances.addAll(result);
        resultsAdapter.notifyDataSetChanged();
    }

    private void addTags() {
        names = new ArrayList<>();
        names.add("TAG ONE");
        names.add("TAG TWO");
        names.add("TAG THREE");
        names.add("TAG FOUR");
        names.add("TAG FIVE");
        names.add("TAG SIX");

    }

    public void initializeVars()
    {
        mEventList = new ArrayList<>();
        mPlaceList = new ArrayList<>();
        results = new ArrayList<>();
        distances = new ArrayList<>();
        ids = new ArrayList<>();
        myManager = new LinearLayoutManager(this);
        resultsManager = new LinearLayoutManager(this);
        horizontalLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        verticalLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        eApi = new EventsApi(this);
        pApi = new PlacesApi(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.v("Location Changed", location.getLatitude() + " and " + location.getLongitude());
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
}