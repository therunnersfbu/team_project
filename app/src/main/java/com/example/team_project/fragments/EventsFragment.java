package com.example.team_project.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.example.team_project.CardViewAdapter;
import com.example.team_project.MainActivity;
import com.example.team_project.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EventsFragment extends Fragment {
    private Unbinder unbinder;

    private Context context;
    private SearchView svSearch;
    private RecyclerView rvSuggested;
    private ArrayList<String> names;
    private int position;
    private CardViewAdapter adapter;
    RecyclerView.LayoutManager myManager;
    LinearLayoutManager horizontalLayout;
    View childView;



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
        rvSuggested = view.findViewById(R.id.rvSuggestions);
        myManager = new LinearLayoutManager(getContext());
        rvSuggested.setLayoutManager(myManager);
        addItems();
        adapter = new CardViewAdapter(names);
        horizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvSuggested.setLayoutManager(horizontalLayout);
        rvSuggested.setAdapter(adapter);


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
