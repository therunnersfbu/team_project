package com.example.team_project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private RecyclerView rvTags;
    private RecyclerView rvResults;
    private boolean isTags;
    private ArrayList<String> names;
    private ArrayList<String> results;
    private CardViewAdapter adapter;
    private ResultsAdapter resultsAdapter;
    RecyclerView.LayoutManager myManager;
    RecyclerView.LayoutManager resultsManager;
    LinearLayoutManager horizontalLayout;
    LinearLayoutManager verticalLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isTags = true;
        setContentView(R.layout.activity_search);
        rvTags = findViewById(R.id.rvTags);
        rvResults = findViewById(R.id.rvResults);
        myManager = new LinearLayoutManager(this);
        resultsManager = new LinearLayoutManager(this);
        rvTags.setLayoutManager(myManager);
        rvResults.setLayoutManager(resultsManager);
        addTags();
        addResults();
        adapter = new CardViewAdapter(names, isTags);
        resultsAdapter = new ResultsAdapter(results);
        horizontalLayout = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        verticalLayout = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvTags.setLayoutManager(horizontalLayout);
        rvTags.setAdapter(adapter);
        rvResults.setLayoutManager(verticalLayout);
        rvResults.setAdapter(resultsAdapter);
    }

    private void addResults() {
        results = new ArrayList<>();
        results.add("One");
        results.add("Two");
        results.add("Three");
        results.add("Four");
        results.add("Five");
        results.add("Six");
    }

    private void addTags() {
        names = new ArrayList<>();
        names.add("ONE");
        names.add("TWO");
        names.add("THREE");
        names.add("FOUR");
        names.add("FIVE");
        names.add("SIX");

    }
}