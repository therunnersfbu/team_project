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
import com.example.team_project.calendar.SpotCalendarFragment;
import com.example.team_project.usersearch.UserSearchActivity;
import com.parse.ParseUser;
import butterknife.BindView;
import butterknife.ButterKnife;

// activity that shows the bottom navigation bar on the main screen
public class BottomNavActivity extends AppCompatActivity {

    private final int DEFAULT_TAB_ID = R.id.action_events;

    @BindView(R.id.toolbar_main) Toolbar toolbar;
    @BindView(R.id.bottom_navigation) BottomNavigationView bottomNavigationView;

    //TODO save as private
    public static ParseUser targetUser;
    public static double currentLat;
    public static double currentLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_nav);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        PublicVariables.isCurLoc = true;
        PublicVariables.googleApi = getString(R.string.google_maps_api_key);
        PublicVariables.eventfulApi = getString(R.string.eventful_api_key);

        //TODO do not save as public static var
        final FragmentManager fragmentManager = getSupportFragmentManager();

        // define your fragments here
        final Fragment eventsFragment = new EventsFragment();
        final Fragment mapFragment = new MapFragment();
        final Fragment calendarFragment = new SpotCalendarFragment();

        // handle navigation selection
        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment fragment;
                        switch (item.getItemId()) {
                            case R.id.action_events:
                                fragment = eventsFragment;
                                break;
                            case R.id.action_map:
                                fragment = mapFragment;
                                break;
                            case R.id.action_my_calendar:
                                fragment = calendarFragment;
                                break;
                            default:
                                throw new RuntimeException();
                        }
                        fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                        return true;
                    }
                });
        // Set default selection
        bottomNavigationView.setSelectedItemId(DEFAULT_TAB_ID);
    }

    @Override
    protected void onResume() {
        super.onResume();
        targetUser = ParseUser.getCurrentUser();
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
            startActivityForResult(i, 1);
        } else if (id == R.id.item_user_search) {
            Intent i = new Intent(this, UserSearchActivity.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
