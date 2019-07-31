package com.example.team_project.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
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

import com.example.team_project.details.DetailsActivity;
import com.example.team_project.R;
import com.example.team_project.model.Post;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
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

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    private Unbinder unbinder;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap googleMap;
    ArrayList<Post> reviewCoordinatesList;
    ImageButton mapicon;

    // TODO make initial view closer

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);

        Drawable loginActivityBackground = view.findViewById(R.id.mapicon).getBackground();
        loginActivityBackground.setAlpha(230);

        mapicon = view.findViewById(R.id.mapicon);
        mapicon.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               enableMyLocationIfPermitted();
               googleMap.setMinZoomPreference(3);
           }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        reviewCoordinatesList = new ArrayList<>();
        queryReviews();

        googleMap.setOnMyLocationButtonClickListener(onMyLocationButtonClickListener);
        enableMyLocationIfPermitted();

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.setMinZoomPreference(3);
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {

        final LatLng windowPosition = marker.getPosition();
        ParseQuery<Post> reviewQuery = new ParseQuery<Post>(Post.class);
        //when we get post back we'll also get the full details of the user
        reviewQuery.setLimit(1000);
        reviewQuery.include(Post.KEY_USER);
        reviewQuery.include(Post.KEY_EVENT_PLACE);

        reviewQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
            if (e != null) {
                Log.e("MapFragment", "error with query");
                e.printStackTrace();
                return;
            }

            for (int i = 0; i < posts.size(); i++) {
                Post post = posts.get(i);
                String[] reviewCoordinates = post.getCoordinates().split("\\s+");
                double latitude = Double.parseDouble(reviewCoordinates[0]);
                double longitude = Double.parseDouble(reviewCoordinates[1]);
                LatLng markerPosition = new LatLng(latitude, longitude);
                Boolean type;
                if (markerPosition.equals(windowPosition)) {
                    String eventApiId = post.getEventPlace().getAppId();
                    if ('E' != eventApiId.charAt(0)) {
                        type = true;
                    } else {
                        type = false;
                    }
                    Intent intent = new Intent(getActivity(), DetailsActivity.class);
                    intent.putExtra("eventID", eventApiId);
                    intent.putExtra("type", type);
                    intent.putExtra("distance", "unknown");
                    startActivity(intent);
                    break;
                }
            }
            }
        });
    }

    private void enableMyLocationIfPermitted() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else if (googleMap != null) {
            googleMap.setMyLocationEnabled(true);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.9577, -121.2908) , 6));
        }
    }


    private void showDefaultLocation() {
        Toast.makeText(getContext(), "Location permission not granted, " +
                        "showing default location",
                Toast.LENGTH_SHORT).show();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.8283, -98.5795) , 0));
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
                    googleMap.setMinZoomPreference(5);
                    return false;
                }
            };


    protected void queryReviews(){
        ParseQuery<Post> reviewQuery = new ParseQuery<Post>(Post.class);
        //when we get post back we'll also get the full details of the user
        reviewQuery.setLimit(1000);
        reviewQuery.include(Post.KEY_USER);
        reviewQuery.include(Post.KEY_EVENT_PLACE);

        reviewQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
            if (e != null) {
                Log.e("MapFragment", "error with query: " + e.getMessage());
                e.printStackTrace();
                return;
            }

            // TODO add loop for liked events
                // TODO vustomize width of info window
                // TODO added markers for added events?
                //TODO in Marker start with Review:
            for(int i = 0; i < posts.size(); i++) {
                Post post = posts.get(i);
                String[] reviewCoordinates = post.getCoordinates().split("\\s+");
                String review = post.getReview();
                String name = post.getEventPlace().getName();
                double latitude = Double.parseDouble(reviewCoordinates[0]);
                double longitude = Double.parseDouble(reviewCoordinates[1]);
                Marker reviewmarker = googleMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .title(name)
                        .snippet(review));
                googleMap.setOnInfoWindowClickListener(MapFragment.this);
            }
            }
        });
    }

}
