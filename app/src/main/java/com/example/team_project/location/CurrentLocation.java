package com.example.team_project.location;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.Calendar;

public class CurrentLocation implements LocationListener, GoogleApiClient.OnConnectionFailedListener{

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static String LOCATION_TAG = "location";
    private double longitude;
    private double latitude;
    private boolean isCurLoc = true;
    private Location mLocation;
    private LocationManager mLocManager;
    private String newLoc = "";
    private String newLocName;
    private Context context;

    public CurrentLocation(Context context) {
        this.context = context;
    }

    public void setMyLocation() {
        mLocManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean network_enabled = mLocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (network_enabled) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.e(LOCATION_TAG, "no permission");
                return;
            }
            mLocation = mLocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(mLocation != null && mLocation.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
                longitude = mLocation.getLongitude();
                latitude = mLocation.getLatitude();
 //               populateList();
            }
            else {
                mLocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            }
        }
        Log.d(LOCATION_TAG, longitude + ", " + latitude);
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setMyLocation();
                } else {
                }
                return;
            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.v(LOCATION_TAG, location.getLatitude() + " and " + location.getLongitude());
            this.mLocation = location;
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            Log.d(LOCATION_TAG, longitude + ", " + latitude);
            mLocManager.removeUpdates(this);
 //           initializeCategory(category);
  //          populateList();
        }
    }

    public double getLongitude() {
        return longitude;
    }
    public double getLatitude() {
        return latitude;
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
}
