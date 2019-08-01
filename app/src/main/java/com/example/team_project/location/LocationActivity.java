package com.example.team_project.location;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.team_project.R;
import com.example.team_project.api.AutocompleteApi;
import com.example.team_project.api.PlacesApi;
import org.json.JSONException;
import java.util.ArrayList;

// used to change the current location to a location of the user's choosing.
public class LocationActivity extends AppCompatActivity implements LocationAdapter.AdapterCallback, AutocompleteApi.GetAutocomplete  {
    private ArrayList<String> mLocNames;
    private ArrayList<String> mLocIds;
    private ArrayList<String> mLocations;
    private PlacesApi pApi;
    private RecyclerView rvLocResults;
    private EditText etSearch;
    private AutocompleteApi LApi;
    private LocationAdapter mLocationAdapter;
    private Button btnSearch;
    private RecyclerView.LayoutManager resultsManager;
    private LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        // lists to hold information from location search results
        mLocations = new ArrayList<>();
        mLocIds = new ArrayList<>();
        mLocNames = new ArrayList<>();
        //layout items
        etSearch = findViewById(R.id.etSearch);
        rvLocResults = findViewById(R.id.rvLocResults);
        btnSearch = findViewById(R.id.btnSearch);
        //layout managers
        resultsManager = new LinearLayoutManager(this);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvLocResults.setLayoutManager(resultsManager);
        //adapter initialization
        mLocationAdapter = new LocationAdapter(mLocNames, mLocIds);
        mLocationAdapter.setOnItemClickedListener(this);
        rvLocResults.setLayoutManager(linearLayoutManager);
        rvLocResults.setAdapter(mLocationAdapter);
        LApi = new AutocompleteApi(this);
        //on click listener for location result selection
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLocNames.clear();
                mLocations.clear();
                mLocIds.clear();
                populateList();
            }
        });
    }

    @Override
    public void apiFinishedLocation(ArrayList<String> array, ArrayList<String> names) {
        for (int i = 0; i < array.size(); i++) {
            mLocNames.add(names.get(i));
            mLocIds.add(array.get(i));
        }
        mLocationAdapter.notifyDataSetChanged();
    }

    private void populateList() {
        LApi.setInput(etSearch.getText().toString());
        LApi.getTopPlaces();
    }

    @Override
    public void onItemClicked() {
        Log.e("Test", "callback ");
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
