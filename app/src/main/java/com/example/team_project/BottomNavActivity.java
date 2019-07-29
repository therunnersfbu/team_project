package com.example.team_project;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.team_project.account.ProfileActivity;
import com.example.team_project.fragments.EventsFragment;
import com.example.team_project.fragments.MapFragment;
import com.example.team_project.calendar.MyCalendarFragment;
import com.example.team_project.location.LocationAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BottomNavActivity extends AppCompatActivity {
    @BindView(R.id.toolbar_main) Toolbar toolbar;
    @BindView(R.id.bottom_navigation) BottomNavigationView bottomNavigationView;

    public static BottomNavActivity bottomNavAct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        LocationAdapter.isCurLoc = true;

        BottomNavActivity.bottomNavAct = this;

        final FragmentManager fragmentManager = getSupportFragmentManager();

        // define your fragments here
        final Fragment fragment1 = new EventsFragment();
        final Fragment fragment2 = new MapFragment();
        final Fragment fragment3 = new MyCalendarFragment();

        // handle navigation selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;
                        switch (item.getItemId()) {
                            case R.id.action_events:
                                fragment = fragment1;
                                break;
                            case R.id.action_map:
                                fragment = fragment2;
                                break;
                            case R.id.action_my_calendar:
                            default:
                                fragment = fragment3;
                                break;
                        }
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                        return true;
                    }
                });
        // Set default selection
        bottomNavigationView.setSelectedItemId(R.id.action_events);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_profile) {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
