package com.example.team_project.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.team_project.BottomNavActivity;
import com.example.team_project.Constants;
import com.example.team_project.CustomInfoWindowAdapter;
import com.example.team_project.PublicVariables;
import com.example.team_project.R;
import com.example.team_project.api.DirectionsApi;
import com.example.team_project.details.DetailsActivity;
import com.example.team_project.model.PlaceEvent;
import com.example.team_project.model.Post;
import com.example.team_project.model.User;
import com.example.team_project.utils.ContextProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

// The MapFragment displays all of the user's liked and reviewed spots on the map, along with their actual review
// the liked spots are shown with pink markers and the reviewed events are shown with red markers
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, DirectionsApi.GetSingleDistance {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Unbinder mUnbinder;
    private GoogleMap mGoogleMap;
    private ParseUser user;
    private ArrayList<String> likedEvents;
    private ArrayList<String> addedEvents;
    private int mMaxLimit = 1000;
    private int mMinZoom = 3;
    private String mCurrentSpotId;
    private Toast toast;
    private static LatLng mUnitedStates = new LatLng(39.8283, -98.5795);
    private ArrayList<String> mMarkerCoordinates;
    private Boolean showToast;

    private WeakReference<DirectionsApi.GetSingleDistance> mGetSingleDistance;

    @BindView(R.id.mapicon) ImageButton mMapIcon;
    @BindString(R.string.map_frag_tag) String mMapFragTag;
    @BindString(R.string.user_error_message) String mUserErrorMessage;
    @BindString(R.string.review) String mReview;
    @BindString(R.string.event_id) String mEventId;
    @BindString(R.string.type) String mType;
    @BindString(R.string.distance) String mDistance;
    @BindString(R.string.place_event_class_name) String mPlaceEventClassName;
    @BindString(R.string.location_permission_denied) String mLocationPermissionDenied;
    @BindString(R.string.query_error_message) String mQueryErrorMessage;
    @BindString(R.string.liked_event_snippet) String mLikedEventSnippet;
    @BindString(R.string.saved_event_snippet) String mSavedEventSnippet;

    @OnClick(R.id.mapicon)
    public void mapiconClick(ImageButton button) {
        enableMyLocationIfPermitted();
        mGoogleMap.setMinZoomPreference(mMinZoom);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        initUserData();
        return view;
    }

    // ensures and checks if there is user data available and if so it initializes the list
    private void initUserData() {
        user = ParseUser.getCurrentUser();
        if (user != null) {
            user.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    likedEvents = (ArrayList<String>) user.get(User.KEY_LIKED_EVENTS);
                    addedEvents = (ArrayList<String>) user.get(User.KEY_ADDED_EVENTS);
                    mMarkerCoordinates = new ArrayList<>();
                }
            });
        }else{
            Log.d(mMapFragTag, mUserErrorMessage);
            return;
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mGetSingleDistance = new WeakReference<>((DirectionsApi.GetSingleDistance) this);

        showToast = user.getBoolean(User.KEY_TOAST);
        if (showToast){
            showInitialToast(view);
            user.put(User.KEY_TOAST, !showToast);
        }


        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        Drawable loginActivityBackground = mMapIcon.getBackground();
        loginActivityBackground.setAlpha(230);

        mMapIcon = view.findViewById(R.id.mapicon);
        mMapIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mUnitedStates , 0));
                mGoogleMap.setMinZoomPreference(mMinZoom);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        mGoogleMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setCompassEnabled(true);
        mGoogleMap.setMinZoomPreference(mMinZoom);
        enableMyLocationIfPermitted();
        queryReviews();
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        mCurrentSpotId = marker.getTag().toString();
        DirectionsApi api = new DirectionsApi(mGetSingleDistance.get());
        api.setOrigin(BottomNavActivity.currentLat, BottomNavActivity.currentLng);
        PlaceEvent parseEvent = query(mCurrentSpotId);
        api.addDestination(parseEvent.getCoordinates().replace(" ", ","));
        api.getDistance();
    }

    @Override
    public void gotDistance(String distanceApi) {
        Boolean spotType = getSpotType(mCurrentSpotId);
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(mEventId, mCurrentSpotId);
        intent.putExtra(mType, spotType);
        intent.putExtra(mDistance, distanceApi);
        startActivity(intent);
    }

    public PlaceEvent query(String currentSpotId) {
        ParseQuery<PlaceEvent> query = new ParseQuery(mPlaceEventClassName);
        query.whereContains(PlaceEvent.KEY_API, currentSpotId);
        PlaceEvent mPlaceEvent = null;
        try {
            mPlaceEvent = query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mPlaceEvent;
    }

    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mGoogleMap != null) {
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(BottomNavActivity.currentLat, BottomNavActivity.currentLng) , 11));

        }
    }

    private void showDefaultLocation() {
        Toast.makeText(getContext(),mLocationPermissionDenied,
                Toast.LENGTH_SHORT).show();
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.8283, -98.5795) , 0));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableMyLocationIfPermitted();
                } else {
                    showDefaultLocation();
                }
                return;
            }
        }
    }

    public GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(BottomNavActivity.currentLat, BottomNavActivity.currentLng) , mMinZoom));
                    return false;
                }
            };

    protected void queryReviews(){
        final ParseQuery<Post> reviewQuery = new ParseQuery<>(Post.class);
        reviewQuery.setLimit(mMaxLimit);
        reviewQuery.include(Post.KEY_EVENT_PLACE);
        reviewQuery.include(Post.KEY_USER);
        reviewQuery.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        reviewQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
            if (e != null) {
                Log.e(mMapFragTag, mQueryErrorMessage + e.getMessage());
                e.printStackTrace();
                return;
            }

            for(int i = 0; i < posts.size(); i++) {
                Post post = posts.get(i);
                String review = mReview  + post.getReview();
                String name = post.getEventPlace().getName();
                String reviewId = post.getEventPlace().getAppId();
                String coordinates = post.getEventPlace().getCoordinates();
                mMarkerCoordinates.add(coordinates);
                Log.d("mapfragment", "Marker Coordinates in reviewd: " + mMarkerCoordinates);

                Float color = BitmapDescriptorFactory.HUE_YELLOW;
                if (coordinates != null){
                    makeMapMarker(coordinates, reviewId, name, review, color);
                }
            }
                queryLikedEvents();
            }
        });

    }

    protected void queryLikedEvents(){
        ParseQuery placeEventQuery = new ParseQuery(mPlaceEventClassName);
        placeEventQuery.setLimit(mMaxLimit);
        ArrayList<String> likedEventIds = new ArrayList<>();
        for (int i= 0; i < likedEvents.size();i++){
            String eventId = likedEvents.get(i).split(Constants.splitindicator)[0];
            likedEventIds.add(eventId);
        }
        placeEventQuery.whereContainedIn(PlaceEvent.KEY_API, likedEventIds);

        placeEventQuery.findInBackground(new FindCallback<PlaceEvent>() {
            @Override
            public void done(List<PlaceEvent> placeEvents, ParseException e) {
            if (e != null) {
                Log.e(mMapFragTag, mQueryErrorMessage + e.getMessage());
                e.printStackTrace();
                return;
            }
            for (int i = 0; i < placeEvents.size(); i++) {
                String placeEventCoord = placeEvents.get(i).getCoordinates();
                Log.d("mapfragment", "Marker Coordinates in Liked: " + mMarkerCoordinates);
                Log.d("mapfragment", "Liked event coordinate: " + placeEventCoord);
                if (!mMarkerCoordinates.contains(placeEventCoord)) {
                    Log.d("mapfragment", "inloop in liked events!");
                    String placeEventName = placeEvents.get(i).getName();
                    String likedSpotId = placeEvents.get(i).getAppId();
                    Float color = BitmapDescriptorFactory.HUE_RED;
                    String snippet = mLikedEventSnippet;
                    mMarkerCoordinates.add(placeEventCoord);
                    makeMapMarker(placeEventCoord, likedSpotId, placeEventName, snippet, color);
                }
            }
            queryAddedEvents();
            }
        });
    }

    protected void queryAddedEvents(){
        ParseQuery placeEventQuery = new ParseQuery(mPlaceEventClassName);
        placeEventQuery.setLimit(mMaxLimit);
        ArrayList<String> addedEventApis = new ArrayList<>();
        for (int i= 0; i < addedEvents.size();i++){
            String addedSpotId = addedEvents.get(i).split(Constants.splitindicator)[1];
            addedEventApis.add(addedSpotId);
        }
        placeEventQuery.whereContainedIn(PlaceEvent.KEY_API, addedEventApis);

        placeEventQuery.findInBackground(new FindCallback<PlaceEvent>() {
            @Override
            public void done(List<PlaceEvent> placeEvents, ParseException e) {
                if (e != null) {
                    Log.e(mMapFragTag, mQueryErrorMessage + e.getMessage());
                    e.printStackTrace();
                    return;
                }
                for (int i = 0; i < placeEvents.size(); i++) {
                    String placeEventCoord = placeEvents.get(i).getCoordinates();
                    if (!mMarkerCoordinates.contains(placeEventCoord)) {
                        String placeEventName = placeEvents.get(i).getName();
                        String addedSpotId = placeEvents.get(i).getAppId();
                        Float color = BitmapDescriptorFactory.HUE_BLUE;
                        String snippet = mSavedEventSnippet;
                        makeMapMarker(placeEventCoord, addedSpotId, placeEventName, snippet, color);
                    }
                }
            }
        });
    }

    protected void makeMapMarker(String coordinateString, String id, String placeEventName, String snippet, Float color) {
        String[] coordinates = coordinateString.split("\\s+");
        double latitude = Double.parseDouble(coordinates[0]);
        double longitude = Double.parseDouble(coordinates[1]);
        Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title(placeEventName)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.defaultMarker(color)));
        marker.setTag(id);
        mGoogleMap.setOnInfoWindowClickListener(MapFragment.this);
        mGoogleMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(new ContextProvider() {
            @Override
            public Context getContext() {
                return getActivity();
            }
        }));
    }

    private Boolean getSpotType(String id){
        if ('E' != id.charAt(0)) {
            PublicVariables.isEvent = true;
        } else {
            PublicVariables.isEvent = false;
        }
        return PublicVariables.isEvent;
    }

    private void showInitialToast(View view){
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.toast_map,
                (ViewGroup) view.findViewById(R.id.custom_map_toast_container));
        toast = new Toast(getContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (toast != null) {
            toast.cancel();
        }
    }
}

