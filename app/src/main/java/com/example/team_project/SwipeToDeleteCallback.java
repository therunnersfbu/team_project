package com.example.team_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.team_project.fragments.MyCalendarFragment;
import com.example.team_project.model.Event;
import com.example.team_project.model.User;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;


public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    CalendarAdapter mAdapter;
    CompactCalendarView compactCalendar;
    Context context;
    MyCalendarFragment dot;



    public SwipeToDeleteCallback(CalendarAdapter adapter) {
        super(0, ItemTouchHelper.RIGHT);
        mAdapter = adapter;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        int position = viewHolder.getAdapterPosition();

        ParseUser user = ParseUser.getCurrentUser();
        ArrayList<String> parseevents = (ArrayList<String>) user.get(User.KEY_ADDED_EVENTS);
        Log.d("SwipeToDeleteCallBack", "parse events" + parseevents);

        ArrayList<String> rvEvents = mAdapter.events;
            String eventToDelete = rvEvents.get(position);

            for (int x = 0; x < parseevents.size(); x++) {
                if (eventToDelete.equals(parseevents.get(x).substring(11))) {
                    parseevents.remove(x);
                    user.put(User.KEY_ADDED_EVENTS, parseevents);
                }
            }

            user.saveInBackground();
            mAdapter.deleteItem(position);
            mAdapter.notifyDataSetChanged();
        }
    }

