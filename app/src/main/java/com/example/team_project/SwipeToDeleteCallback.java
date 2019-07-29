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
import com.example.team_project.model.User;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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

        compactCalendar = viewHolder.itemView.findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);
        // add dot for each individual event to calendar
        if (parseevents != null) {
            for (int x = 0; x < parseevents.size(); x++) {
                Event event = null;
                try {
                    event = new Event(Color.BLACK, myMilliSecConvert(parseevents.get(x).substring(0, 10)));
                    Log.d("SwipeToDeleteCallBack", "event" + event);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                compactCalendar.addEvent(event);
            }
        }

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


