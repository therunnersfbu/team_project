package com.example.team_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.team_project.api.AutocompleteApi;
import com.example.team_project.api.PlacesApi;
import com.example.team_project.model.Place;
import com.google.android.gms.common.api.Api;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class LocationActivity extends AppCompatActivity {
    private ArrayList<String> mLocNames;
    private ArrayList<String> mLocIds;
    private ArrayList<String> mLocations;
    private PlacesApi pApi;
    private RecyclerView rvLocResults;
    private EditText etSearch;
    private AutocompleteApi LApi;
    private LocationAdapter mLocationAdapter;
    private Button btnSearch;

    RecyclerView.LayoutManager resultsManager;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        etSearch = findViewById(R.id.etSearch);
        rvLocResults = findViewById(R.id.rvLocResults);
        mLocations = new ArrayList<>();
        resultsManager = new LinearLayoutManager(this);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mLocIds = new ArrayList<>();
        mLocNames = new ArrayList<>();
        rvLocResults.setLayoutManager(resultsManager);
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocNames.clear();
                mLocations.clear();
                mLocIds.clear();
                populateList();
            }
        });
        mLocationAdapter = new LocationAdapter(mLocations, mLocNames, mLocIds);
        rvLocResults.setLayoutManager(linearLayoutManager);
        rvLocResults.setAdapter(mLocationAdapter);
        LApi = new AutocompleteApi(this);
    }

    public void apiFinishedLocation(ArrayList<String> array, ArrayList<String> names) throws JSONException {
        for (int i = 0; i < array.size(); i++) {
            pApi = new PlacesApi(this);
            pApi.getLocation(array.get(i));
            mLocNames.add(names.get(i));
           // mLocIds.add(array.get(i));
        }
    }

    public void apiFinishedGetLocation(String location, String name) throws JSONException {
      //  mLocNames.add(name);
        mLocations.add(location);
        mLocationAdapter.notifyDataSetChanged();
    }

    private void populateList() {
        LApi.setInput(etSearch.getText().toString());
        LApi.getTopPlaces();
    }
}
