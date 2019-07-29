package com.example.team_project.calendar;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;

import com.example.team_project.model.User;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {

    CalendarAdapter mAdapter;
    CompactCalendarView compactCalendar;
    Context context;
    Long epochTime;



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

        // delete the event in the parse database
        for (int x = 0; x < parseevents.size(); x++) {
            if (eventToDelete.equals(parseevents.get(x).substring(11))) {
                parseevents.remove(x);
                user.put(User.KEY_ADDED_EVENTS, parseevents);
            }
        }
        Log.d("SwipeToDeleteCallBack", "new parse events" + parseevents);

        if (eventToDelete != "NONE!"){
            mAdapter.deleteItem(position);
        }
        user.saveInBackground();
        mAdapter.notifyDataSetChanged();
    }

    public long myMilliSecConvert(String date) throws ParseException {
        Date milliDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        epochTime = milliDate.getTime();
        return epochTime;
    }
}


