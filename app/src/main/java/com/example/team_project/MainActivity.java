package com.example.team_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.team_project.api.DirectionsApi;
import com.example.team_project.api.EventsApi;
import com.example.team_project.api.PlacesApi;
import com.example.team_project.model.Event;
import com.example.team_project.model.Place;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_main) Toolbar toolbar;

//     for testing
//     static PlacesApi api;
//     static EventsApi api;
//     static JSONArray array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        ((Button) findViewById(R.id.btnBottomTest)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), BottomNavActivity.class);
                startActivity(i);
            }
        });

//        for testing
//        MainActivity.api = new PlacesApi();
//        MainActivity.api.setRadius(3000);
//        MainActivity.api.setLocation(37.367890, -122.036905);
//        MainActivity.api.getTopPlaces();

//        MainActivity.api = new EventsApi();
//        MainActivity.api.setDate("Future");
//        MainActivity.api.setLocation("San+Francisco");
//        MainActivity.api.getTopEvents();

//        double[] origin = {37.480801, -122.159128};
//        double[] destination = {37.367890, -122.036905};
//        DirectionsApi.getDistance(origin, destination);
    }

//    for testing
//    public static void setArray(JSONArray array) throws JSONException {
//        MainActivity.array = array;
//        Place place;
//        for (int i = 0; i < array.length(); i++) {
//            place = Place.placeFromJson(array.getJSONObject(i));
//            api.setDetails(place);
//        }
//    }

//    public static void setArray(JSONArray array) throws JSONException {
//        MainActivity.array = array;
//        Event event;
//        for (int i = 0; i < array.length(); i++) {
//            event = Event.eventFromJson(array.getJSONObject(i));
//        }
//    }
}
