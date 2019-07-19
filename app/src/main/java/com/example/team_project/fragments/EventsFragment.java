package com.example.team_project.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.team_project.CardViewAdapter;
import com.example.team_project.MainActivity;
import com.example.team_project.R;
import com.example.team_project.SearchActivity;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EventsFragment extends Fragment {
    private Unbinder unbinder;

    private RecyclerView rvSuggested;
    private boolean isTags;
    private ArrayList<String> names;
    private CardViewAdapter adapter;
    private Button btnSearchBar;
    RecyclerView.LayoutManager myManager;
    LinearLayoutManager horizontalLayout;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, parent, false);
        unbinder = ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isTags =false;
        rvSuggested = view.findViewById(R.id.rvSuggestions);
        btnSearchBar = view.findViewById(R.id.btnSearchBar);
        myManager = new LinearLayoutManager(getContext());
        rvSuggested.setLayoutManager(myManager);
        addItems();
        adapter = new CardViewAdapter(names, isTags);
        horizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvSuggested.setLayoutManager(horizontalLayout);
        rvSuggested.setAdapter(adapter);
        btnSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                getContext().startActivity(intent);
            }
        });


    }

    private void addItems() {

        names = new ArrayList<>();
        names.add("ONE");
        names.add("TWO");
        names.add("THREE");
        names.add("FOUR");
        names.add("FIVE");
        names.add("SIX");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
