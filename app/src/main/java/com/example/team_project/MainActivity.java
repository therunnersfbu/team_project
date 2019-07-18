package com.example.team_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.team_project.api.PlacesApi;
import com.example.team_project.model.Place;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_main) Toolbar toolbar;

//     for testing
//     static JSONArray array;
//     static PlacesApi api;

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
    }

//    for testing
//    public static void setArray(JSONArray array) throws JSONException {
//        MainActivity.array = array;
//        Place place = Place.placeFromJson(array.getJSONObject(17));
//        MainActivity.api.setDetails(place);
//    }
}
