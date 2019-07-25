package com.example.team_project;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
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


public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    CalendarAdapter mAdapter;
    CompactCalendarView compactCalendar;
    Context context;


    public SwipeToDeleteCallback(CalendarAdapter adapter) {
        super(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
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
        // get events grom parse
        ArrayList<String> parseevents = (ArrayList<String>) user.get(User.KEY_ADDED_EVENTS);
        Log.d("SwipeToDeleteCallBack", "parse events" + parseevents);
        // get list of events in the adapter
        ArrayList<String> rvEvents = mAdapter.events;
        Log.d("SwipeToDeleteCallBack", "rv events" + rvEvents);

        // TODO why does event crash when you delete the last event

        // declare event we want to delete that was swiped
        String eventToDelete = rvEvents.get(position);
        Log.d("SwipeToDeleteCallBack", "event to delete" + eventToDelete);

        //if (parseevents.contains(eventToDelete)) {
        for (int x = 0; x < parseevents.size(); x++) {
            if (eventToDelete.equals(parseevents.get(x).substring(11))) {
                Log.d("SwipeToDeleteCallBack", "in loop");
                parseevents.remove(x);
                Log.d("SwipeToDeleteCallBack", "new parse events list" + parseevents);
                user.put(User.KEY_ADDED_EVENTS, parseevents);
            }
        }

        // need to make an event at designated date


        user.saveInBackground();
        mAdapter.deleteItem(position);
        // TODO remove dot
        //compactCalendar.removeEvent(circle_event);
        mAdapter.notifyDataSetChanged();
    }
}
