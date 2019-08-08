package com.example.team_project.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
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

import com.example.team_project.BottomNavActivity;
import com.example.team_project.PublicVariables;
import com.example.team_project.search.HorizontalScrollAdapter;
import com.example.team_project.R;
import com.example.team_project.api.DirectionsApi;
import com.example.team_project.api.EventsApi;
import com.example.team_project.api.PlacesApi;
import com.example.team_project.model.Event;
import com.example.team_project.model.Place;
import com.example.team_project.model.User;
import com.example.team_project.search.SearchActivity;
import com.example.team_project.utils.ContextProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.j2objc.annotations.Weak;
import com.parse.ParseUser;
import org.json.JSONArray;
import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Random;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class EventsFragment extends Fragment implements LocationListener, GoogleApiClient.OnConnectionFailedListener, EventsApi.GetEvents, PlacesApi.GetPlaces, DirectionsApi.GetDistances {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int DEFAULT_NO_CATEGORY_SELECTED = -1;
    private double mLatitude;
    private double mLongitude;
    private Unbinder mUnbinder;
    private Location mLocation;
    private LocationManager mLocManager;
    private RecyclerView.LayoutManager myManager;
    private LinearLayoutManager mHorizontalLayout;
    private ArrayList<String> mNames;
    private ArrayList<View> btnCat;
    private ArrayList<View> textCat;
    private HorizontalScrollAdapter mAdapter;
    private ImageButton mBtn;
    private TextView mText;
    private ArrayList<String> idList;

    private WeakReference<EventsApi.GetEvents> mGetEvents;
    private WeakReference<PlacesApi.GetPlaces> mGetPlaces;
    private WeakReference<DirectionsApi.GetDistances> mGetDistances;

    //TODO singleton
    public static int categoryToMark;
    public static ArrayList<String> distances;

    @BindView(R.id.rvSuggestions) RecyclerView rvSuggestions;
    @BindView(R.id.btnSearchBar) Button btnSearchBar;

    @OnClick(R.id.btnSearchBar)
    public void buttonSearch(Button button) {
        EventsFragment.categoryToMark = DEFAULT_NO_CATEGORY_SELECTED;
        Intent intent = new Intent(getContext(), SearchActivity.class);
        intent.putExtra("latitude", mLatitude);
        intent.putExtra("longitude", mLongitude);
        getContext().startActivity(intent);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, parent, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    // initi method
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //initialize with declaration
        mGetEvents = new WeakReference<>((EventsApi.GetEvents) this);
        mGetPlaces = new WeakReference<>((PlacesApi.GetPlaces) this);
        mGetDistances = new WeakReference<>((DirectionsApi.GetDistances) this);
        PublicVariables.type = true;
        myManager = new LinearLayoutManager(getContext());
        rvSuggestions.setLayoutManager(myManager);
        idList = new ArrayList<>();
        distances = new ArrayList<>();
        mNames = new ArrayList<>();
        mAdapter = new HorizontalScrollAdapter(mNames, idList,false, new ContextProvider() {
            @Override
            public Context getContext() {
                return getActivity();
            }
        });
        mHorizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        rvSuggestions.setLayoutManager(mHorizontalLayout);
        rvSuggestions.setAdapter(mAdapter);

        btnCat = new ArrayList<>(Arrays.asList(view.findViewById(R.id.ibtnBreakfast), view.findViewById(R.id.ibtnBrunch),
                view.findViewById(R.id.ibtnLunch), view.findViewById(R.id.ibtnDinner), view.findViewById(R.id.ibtnSights),
                view.findViewById(R.id.ibtnNight), view.findViewById(R.id.ibtnShopping), view.findViewById(R.id.ibtnConcerts),
                view.findViewById(R.id.ibtnPop), view.findViewById(R.id.ibtnBeauty), view.findViewById(R.id.ibtnActive),
                view.findViewById(R.id.ibtnParks)));
        textCat = new ArrayList<>(Arrays.asList(view.findViewById(R.id.tvBreakfast), view.findViewById(R.id.tvBrunch),
                view.findViewById(R.id.tvLunch), view.findViewById(R.id.tvDinner), view.findViewById(R.id.tvSights),
                view.findViewById(R.id.tvDrinks), view.findViewById(R.id.tvShopping), view.findViewById(R.id.tvConcerts),
                view.findViewById(R.id.tvPop), view.findViewById(R.id.tvBeauty), view.findViewById(R.id.tvActive),
                view.findViewById(R.id.tvParks)));

        for(int i = 0; i<btnCat.size(); i++) {
            final int index = i;
            mBtn = (ImageButton) btnCat.get(i);
            mBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mText = (TextView) textCat.get(index);
                    EventsFragment.categoryToMark = index;
                    Intent intent = new Intent(getContext(), SearchActivity.class);
                    intent.putExtra("category", index);
                    intent.putExtra("latitude", mLatitude);
                    intent.putExtra("longitude", mLongitude);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) getContext(), (View)mText, "category");
                    getContext().startActivity(intent, options.toBundle());
                }
            });
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }else{
            setMyLocation();
        }
    }

    private void setMyLocation() {
        mLocManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean network_enabled = mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (network_enabled) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more .
                Log.e("mLocation", "no permission");
                return;
            }
            mLocation = mLocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if(mLocation != null && mLocation.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
                mLongitude = mLocation.getLongitude();
                mLatitude = mLocation.getLatitude();
                getSuggested();
            }
            else {
                mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        }

        Log.d("mLocation", mLongitude + ", " + mLatitude);
    }

    private void getSuggested() {
        BottomNavActivity.currentLat = mLatitude;
        BottomNavActivity.currentLng = mLongitude;

        ParseUser user = ParseUser.getCurrentUser();
        ArrayList<Boolean> tags = (ArrayList<Boolean>) user.get(User.KEY_TAGS);
        ArrayList<Boolean> categories = (ArrayList<Boolean>) user.get(User.KEY_CATEGORIES);
        ArrayList<String> suggestedStr = new ArrayList<>();

        for (int i = 0; i < tags.size(); i++) {
            if (tags.get(i)) {
                suggestedStr.add(PublicVariables.getTagStr(i));
            }
        }
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i)) {
                suggestedStr.add(PublicVariables.getCategoryStr(i));
            }
        }

        Random rand = new Random();
        String keyword = suggestedStr.get(rand.nextInt(suggestedStr.size()));
        keyword = keyword.replace(" ", "+");

        //TODO make constant tags and radius
        if (keyword.equals("concert") || keyword.equals("fair")) {
            PublicVariables.type = false;
            EventsApi api = new EventsApi(mGetEvents.get());
            api.setLocation(mLatitude, mLongitude, 60);
            api.setDate("Future");
            api.setKeywords(keyword);
            api.getTopEvents();
        } else {
            PlacesApi api = new PlacesApi(mGetPlaces.get());
            api.setLocation(mLatitude, mLongitude);
            api.setRadius(10000);
            api.setKeywords(keyword);
            api.getTopPlaces();
        }
    }

    @Override
    public void gotEvents(JSONArray eventsApi) {
        DirectionsApi dApi = new DirectionsApi(mGetDistances.get());
        dApi.setOrigin(mLatitude, mLongitude);
        int i = 0;

        while (i < eventsApi.length() && i < 6) {
            Event event = null;
            try {
                event = Event.eventFromJson(eventsApi.getJSONObject(i), false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dApi.addDestination(event.getLocation());
            idList.add(event.getEventId());
            mNames.add(event.getEventName());

            i++;
        }

        dApi.getDistance();
    }

    @Override
    public void gotEvent(Event eventApi) {

    }

    @Override
    public void gotPlaces(JSONArray placesApi) {
        DirectionsApi dApi = new DirectionsApi(mGetDistances.get());
        dApi.setOrigin(mLatitude, mLongitude);
        int i = 0;

        //TODO make constant suggested place limit
        while (i < placesApi.length() && i < 6) {
            Place place = null;
            try {
                place = Place.placeFromJson(placesApi.getJSONObject(i), false);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dApi.addDestination(place.getLocation());
            idList.add(place.getPlaceId());
            mNames.add(place.getPlaceName());

            i++;
        }

        dApi.getDistance();
    }

    @Override
    public void gotPlace(Place placeApi) {

    }

    @Override
    public void gotDistances(ArrayList<String> distancesApi) {
        distances = distancesApi;
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("mLocation", "granted");
                    setMyLocation();
                } else {
                    Log.d("mLocation", "not granted");
                }
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.v("Location  Changed", location.getLatitude() + " and " + location.getLongitude());
            this.mLocation = location;
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();
            Log.d("mLocation", mLongitude + ", " + mLatitude);
            mLocManager.removeUpdates(this);
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
        mUnbinder.unbind();
    }
}
