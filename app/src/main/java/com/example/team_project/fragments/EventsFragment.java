package com.example.team_project.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.team_project.HorizontalScrollAdapter;
import com.example.team_project.R;
import com.example.team_project.search.SearchActivity;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EventsFragment extends Fragment {
    private Unbinder unbinder;
    private RecyclerView rvSuggested;
    private boolean isTags;
    private ArrayList<String> names;
    private HorizontalScrollAdapter adapter;
    private ArrayList<View> btnCat;
    private Button btnSearchBar;
    private ImageButton mbtn;
    RecyclerView.LayoutManager myManager;
    LinearLayoutManager horizontalLayout;

    public static int categoryToMark;

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
        adapter = new HorizontalScrollAdapter(names, isTags);
        horizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvSuggested.setLayoutManager(horizontalLayout);
        rvSuggested.setAdapter(adapter);
        btnCat = new ArrayList<>(Arrays.asList(view.findViewById(R.id.ibtnBreakfast), view.findViewById(R.id.ibtnBrunch),
                view.findViewById(R.id.ibtnLunch), view.findViewById(R.id.ibtnDinner), view.findViewById(R.id.ibtnSights),
                view.findViewById(R.id.ibtnNight), view.findViewById(R.id.ibtnShopping), view.findViewById(R.id.ibtnConcerts),
                view.findViewById(R.id.ibtnPop), view.findViewById(R.id.ibtnBeauty), view.findViewById(R.id.ibtnActive),
                view.findViewById(R.id.ibtnParks)));
        btnSearchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventsFragment.categoryToMark = -1;
                Intent intent = new Intent(getContext(), SearchActivity.class);
                getContext().startActivity(intent);
            }
        });
        for(int i = 0; i<12; i++)
        {
            final int index = i;
            mbtn = (ImageButton) btnCat.get(i);
            mbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventsFragment.categoryToMark = index;
                    Intent intent = new Intent(getContext(), SearchActivity.class);
                    intent.putExtra("category", index);
                    getContext().startActivity(intent);
                }
            });
        }
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
