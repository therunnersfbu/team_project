package com.example.team_project.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.team_project.DetailsActivity;
import com.example.team_project.R;
import com.example.team_project.model.Post;
import com.example.team_project.model.User;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
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
    ParseUser user = ParseUser.getCurrentUser();
    ArrayList<Post> reviewCoordinatesList;
    ImageButton mapicon;

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
        googleMap.setOnMyLocationClickListener(onMyLocationClickListener);
        enableMyLocationIfPermitted();

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.setMinZoomPreference(3);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(getContext(), "Info window clicked",
                Toast.LENGTH_SHORT).show();
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
            googleMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(39.8283, -98.5795) , 0) );
        }
    }


    private void showDefaultLocation() {
        Toast.makeText(getContext(), "Location permission not granted, " +
                        "showing default location",
                Toast.LENGTH_SHORT).show();
        LatLng redmond = new LatLng(39.8283, -98.5795);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(redmond));
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


    private GoogleMap.OnMyLocationButtonClickListener onMyLocationButtonClickListener =
            new GoogleMap.OnMyLocationButtonClickListener() {
                @Override
                public boolean onMyLocationButtonClick() {
                    googleMap.setMinZoomPreference(5);
                    return false;
                }
            };

    private GoogleMap.OnMyLocationClickListener onMyLocationClickListener =
        new GoogleMap.OnMyLocationClickListener() {
            @Override
            public void onMyLocationClick(@NonNull Location location) {

                googleMap.setMinZoomPreference(12);

                CircleOptions circleOptions = new CircleOptions();

                circleOptions.center(new LatLng(location.getLatitude(),
                        location.getLongitude()));

                circleOptions.radius(200);
                circleOptions.fillColor(Color.RED);
                circleOptions.strokeWidth(6);

                googleMap.addCircle(circleOptions);

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
                    Log.e("MapFragment", "error with query");
                    e.printStackTrace();
                    return;
                }

                for(int i = 0; i < posts.size(); i++) {
                    Post post = posts.get(i);
                    String[] reviewCoordinates = post.getCoordinates().split("\\s+");;
                    String review = post.getReview();
                    String name = post.getEventPlace().getName();
                    double latitude = Double.parseDouble(reviewCoordinates[0]);
                    double longitude = Double.parseDouble(reviewCoordinates[1]);
                    Marker reviewmarker = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(latitude, -longitude))
                            .title(name)
                            .snippet(review));
                }
            }
        });
    }

}
