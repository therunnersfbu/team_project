package com.example.team_project.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import com.example.team_project.BottomNavActivity;
import com.example.team_project.PublicVariables;
import com.example.team_project.R;
import com.example.team_project.api.DirectionsApi;
import com.example.team_project.details.DetailsActivity;
import com.example.team_project.model.PlaceEvent;
import com.example.team_project.model.Post;
import com.example.team_project.model.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.Unbinder;

// The MapFragment displays all of the user's liked and reviewed spots on the map, along with their actual review
// the liked spots are shown with pink markers and the reviewed events are shown with red markers
public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, DirectionsApi.GetSingleDistance {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private Unbinder mUnbinder;
    private GoogleMap mGoogleMap;
    private ImageButton mMapIcon;
    private ParseUser user = ParseUser.getCurrentUser();
    private ArrayList<String> likedEvents = (ArrayList<String>) user.get(User.KEY_LIKED_EVENTS);
    private ArrayList<String> addedEvents = (ArrayList<String>) user.get(User.KEY_ADDED_EVENTS);
    private int mMaxLimit = 1000;
    private int mMinZoom = 3;
    private String mTAG = "MapFragment";
    private String mCurrentSpotId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        //initUserData();
        return view;
    }

    /*private void initUserData() {
        user = ParseUser.getCurrentUser();
        //check if data is available or not, if not fetch.
        if(not fetched){
            user.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, ParseException e) {
                    //init likedEvents and addedEvents here.
                }
            });
        }

    }*/
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        Drawable loginActivityBackground = view.findViewById(R.id.mapicon).getBackground();
        loginActivityBackground.setAlpha(230);

        mMapIcon = view.findViewById(R.id.mapicon);
        mMapIcon.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               enableMyLocationIfPermitted();
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
        queryLikedEvents();
        queryAddedEvents();
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        mCurrentSpotId = marker.getTag().toString();
        DirectionsApi api = new DirectionsApi(MapFragment.this);
        api.setOrigin(BottomNavActivity.currentLat, BottomNavActivity.currentLng);
        PlaceEvent parseEvent = query(mCurrentSpotId);
        api.addDestination(parseEvent.getCoordinates().replace(" ", ","));
        api.getDistance();
    }

    @Override
    public void gotDistance(String distanceApi) {
        Boolean spotType = getSpotType(mCurrentSpotId);
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        intent.putExtra(getResources().getString(R.string.event_id), mCurrentSpotId);
        intent.putExtra(getResources().getString(R.string.type), spotType);
        intent.putExtra(getResources().getString(R.string.distance), distanceApi);
        startActivity(intent);
    }

    public PlaceEvent query(String currentSpotId) {
        ParseQuery<PlaceEvent> query = new ParseQuery(getResources().getString(R.string.place_event_class_name));
        query.whereContains(PlaceEvent.KEY_API, currentSpotId);
        PlaceEvent mPlaceEvent = null;
        try {
            mPlaceEvent = (PlaceEvent) query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mPlaceEvent;
    }

    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mGoogleMap != null) {
            mGoogleMap.setMyLocationEnabled(true);
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(BottomNavActivity.currentLat, BottomNavActivity.currentLng) , 11));

        }
    }

    private void showDefaultLocation() {
        Toast.makeText(getContext(),R.string.location_permission_denied,
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
                mGoogleMap.setMinZoomPreference(mMinZoom);
                return false;
            }
        };

    protected void queryReviews(){
        final ParseQuery<Post> reviewQuery = new ParseQuery<Post>(Post.class);
        reviewQuery.setLimit(mMaxLimit);
        reviewQuery.include(Post.KEY_EVENT_PLACE);
        reviewQuery.include(Post.KEY_USER);
        reviewQuery.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        reviewQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
            if (e != null) {
                Log.e(mTAG, getResources().getString(R.string.query_error_message) + e.getMessage());
                e.printStackTrace();
                return;
            }

            for(int i = 0; i < posts.size(); i++) {
                Post post = posts.get(i);
                String review = getResources().getString(R.string.review) + post.getReview();
                String name = post.getEventPlace().getName();
                String reviewId = post.getEventPlace().getAppId();
                String coordinates = post.getEventPlace().getCoordinates();
                Float color = BitmapDescriptorFactory.HUE_RED;
                if (coordinates != null){
                    makeMapMarker(coordinates, reviewId, name, review, color);
            }}
            }
        });
    }

    protected void queryLikedEvents(){
        ParseQuery placeEventQuery = new ParseQuery(getResources().getString(R.string.place_event_class_name));//TODO make it constant.
        placeEventQuery.setLimit(mMaxLimit);
        ArrayList<String> likedEventIds = new ArrayList<>();
        for (int i= 0; i < likedEvents.size();i++){
            String eventId = likedEvents.get(i).split(PublicVariables.splitindicator)[0];
            likedEventIds.add(eventId);
        }
        placeEventQuery.whereContainedIn(PlaceEvent.KEY_API, likedEventIds);

        placeEventQuery.findInBackground(new FindCallback<PlaceEvent>() {
            @Override
            public void done(List<PlaceEvent> placeEvents, ParseException e) {
                if (e != null) {
                    Log.e(mTAG, getResources().getString(R.string.query_error_message) + e.getMessage());
                    e.printStackTrace();
                    return;
                }
                for (int i = 0; i < placeEvents.size(); i++) {
                    String placeEventCoord = placeEvents.get(i).getCoordinates();
                    String placeEventName = placeEvents.get(i).getName();
                    String likedSpotId = placeEvents.get(i).getAppId();
                    Float color = BitmapDescriptorFactory.HUE_ROSE;
                    String snippet = getResources().getString(R.string.liked_event_snippet);
                    makeMapMarker(placeEventCoord, likedSpotId, placeEventName, snippet, color);
                }
            }
        });
    }

    protected void queryAddedEvents(){
        ParseQuery placeEventQuery = new ParseQuery(getResources().getString(R.string.place_event_class_name));
        placeEventQuery.setLimit(mMaxLimit);
        ArrayList<String> addedEventApis = new ArrayList<>();
        for (int i= 0; i < addedEvents.size();i++){
            String addedSpotId = addedEvents.get(i).split(PublicVariables.splitindicator)[1];
            addedEventApis.add(addedSpotId);
        }
        placeEventQuery.whereContainedIn(PlaceEvent.KEY_API, addedEventApis);

        placeEventQuery.findInBackground(new FindCallback<PlaceEvent>() {
            @Override
            public void done(List<PlaceEvent> placeEvents, ParseException e) {
                if (e != null) {
                    Log.e(mTAG, getResources().getString(R.string.query_error_message) + e.getMessage());
                    e.printStackTrace();
                    return;
                }
                for (int i = 0; i < placeEvents.size(); i++) {
                    String placeEventCoord = placeEvents.get(i).getCoordinates();
                    String placeEventName = placeEvents.get(i).getName();
                    String addedSpotId = placeEvents.get(i).getAppId();
                    Float color = BitmapDescriptorFactory.HUE_BLUE;
                    String snippet = getResources().getString(R.string.liked_event_snippet);
                    makeMapMarker(placeEventCoord, addedSpotId, placeEventName, snippet, color);
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
    }

    public Boolean getSpotType(String id){
        if ('E' != id.charAt(0)) {
            PublicVariables.isEvent = true;
        } else {
            PublicVariables.isEvent = false;
        }
        return PublicVariables.isEvent;
    }
}
