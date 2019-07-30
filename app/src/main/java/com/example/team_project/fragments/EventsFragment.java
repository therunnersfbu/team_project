package com.example.team_project.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.team_project.ComposeReviewActivity;
import com.example.team_project.HorizontalScrollAdapter;
import com.example.team_project.R;
import com.example.team_project.api.DirectionsApi;
import com.example.team_project.api.EventsApi;
import com.example.team_project.api.PlacesApi;
import com.example.team_project.model.Event;
import com.example.team_project.model.Place;
import com.example.team_project.model.User;
import com.example.team_project.search.SearchActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EventsFragment extends Fragment implements LocationListener, GoogleApiClient.OnConnectionFailedListener {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Unbinder unbinder;
    private RecyclerView rvSuggested;
    private boolean isTags;
    private ArrayList<String> names;
    private HorizontalScrollAdapter adapter;
    private ArrayList<View> btnCat;
    private Button btnSearchBar;
    private ImageButton mbtn;
    private double latitude;
    private double longitude;
    private Location location;
    private LocationManager locManager;
    RecyclerView.LayoutManager myManager;
    LinearLayoutManager horizontalLayout;


    private ProgressBar progressBar;
    private int progressStatus = 0;
    private TextView textView;
    private Handler handler = new Handler();

    public static int categoryToMark;
    public static ArrayList<String> distances;
    public static ArrayList<String> idList;
    public static boolean type;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, parent, false);
        unbinder = ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        type = true;
        isTags =false;
        rvSuggested = view.findViewById(R.id.rvSuggestions);
        btnSearchBar = view.findViewById(R.id.btnSearchBar);
        myManager = new LinearLayoutManager(getContext());
        rvSuggested.setLayoutManager(myManager);
        idList = new ArrayList<>();
        distances = new ArrayList<>();
        names = new ArrayList<>();
        adapter = new HorizontalScrollAdapter(names, isTags);
        horizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvSuggested.setLayoutManager(horizontalLayout);
        rvSuggested.setAdapter(adapter);
        btnCat = new ArrayList<>(Arrays.asList(view.findViewById(R.id.ibtnBreakfast), view.findViewById(R.id.ibtnBrunch),
                view.findViewById(R.id.ibtnLunch), view.findViewById(R.id.ibtnDinner), view.findViewById(R.id.ibtnSights),
                view.findViewById(R.id.ibtnNight), view.findViewById(R.id.ibtnShopping), view.findViewById(R.id.ibtnConcerts),
                view.findViewById(R.id.ibtnPop), view.findViewById(R.id.ibtnBeauty), view.findViewById(R.id.ibtnActive),
                view.findViewById(R.id.ibtnParks)));
        btnSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventsFragment.categoryToMark = -1;
                Intent intent = new Intent(getContext(), SearchActivity.class);
                getContext().startActivity(intent);
            }
        });


        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        textView = (TextView) view.findViewById(R.id.textView);
        // Start long running operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            textView.setText(progressStatus+"/"+progressBar.getMax());
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    private void setMyLocation() {
        locManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean network_enabled = locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (network_enabled) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                getSuggested();
            }
            else {
                locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        }

        Log.d("location", longitude + ", " + latitude);
    }

    private void getSuggested() {
        ParseUser user = ParseUser.getCurrentUser();
        ArrayList<Boolean> tags = (ArrayList<Boolean>) user.get(User.KEY_TAGS);
        ArrayList<Boolean> categories = (ArrayList<Boolean>) user.get(User.KEY_CATEGORIES);
        ArrayList<String> suggestedStr = new ArrayList<>();

        for (int i = 0; i < tags.size(); i++) {
            if (tags.get(i)) {
                suggestedStr.add(ComposeReviewActivity.getTagStr(i));
            }
        }
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i)) {
                suggestedStr.add(ComposeReviewActivity.getCategoryStr(i));
            }
        }

        Random rand = new Random();
        String keyword = suggestedStr.get(rand.nextInt(suggestedStr.size()));
        keyword = keyword.replace(" ", "+");

        if (keyword.equals("concert") || keyword.equals("fair")) {
            type = false;
            EventsApi api = new EventsApi(this);
            api.setLocation(latitude, longitude, 60);
            api.setDate("Future");
            api.setKeywords(keyword);
            api.getTopEvents();
        } else {
            PlacesApi api = new PlacesApi(this);
            api.setLocation(latitude, longitude);
            api.setRadius(10000);
            api.setKeywords(keyword);
            api.getTopPlaces();
        }
    }

    public void gotPlaces(JSONArray array) throws JSONException {
        DirectionsApi dApi = new DirectionsApi(this);
        dApi.setOrigin(latitude, longitude);
        int i = 0;

        while (i < array.length() && i < 6) {
            Place place = Place.placeFromJson(array.getJSONObject(i), false);
            dApi.addDestination(place.getLocation());
            idList.add(place.getPlaceId());
            names.add(place.getPlaceName());
            i++;
        }

        dApi.getDistance();
    }

    public void gotEvents(JSONArray array) throws  JSONException {
        DirectionsApi dApi = new DirectionsApi(this);
        dApi.setOrigin(latitude, longitude);
        int i = 0;

        while (i < array.length() && i < 6) {
            Event event = Event.eventFromJson(array.getJSONObject(i), false);
            dApi.addDestination(event.getLocation());
            idList.add(event.getEventId());
            names.add(event.getEventName());
            i++;
        }

        dApi.getDistance();
    }

    public void gotDistances(ArrayList<String> array) {
        distances = array;
        adapter.notifyDataSetChanged();
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
            }
        }
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
            getSuggested();
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
