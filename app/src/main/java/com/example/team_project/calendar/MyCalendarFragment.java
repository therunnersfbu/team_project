package com.example.team_project.calendar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.team_project.R;
import com.example.team_project.model.User;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import butterknife.internal.ListenerClass;

public class MyCalendarFragment extends Fragment{
    private Unbinder unbinder;
    private CompactCalendarView compactCalendar;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
    private Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    private TextView mCurrentDate;
    private RecyclerView rvCal;
    private Long epochTime;
    private ParseUser user = ParseUser.getCurrentUser();
    private ArrayList<String> addedEvents = (ArrayList<String>) user.get(User.KEY_ADDED_EVENTS);
    private ArrayList<String> theDaysEvents;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;
    private String splitindicator = "\\(\\)";
    private TextView tvAddress;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_my_calendar, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCal = view.findViewById(R.id.rvCal);
        tvAddress = view.findViewById(R.id.tvAddress);
        theDaysEvents = new ArrayList<>();


        mCurrentDate = view.findViewById(R.id.current_Date);
        String currentDate = dateFormat.format(calendar.getTime());
        mCurrentDate.setText(currentDate);

        compactCalendar = view.findViewById(R.id.compactcalendar_view);
        compactCalendar.setUseThreeLetterAbbreviation(true);

        // method in order to list the days events when the fragment is clicked
        Date c = Calendar.getInstance().getTime();
        retrieveEvents(c);

        // add each individual event to calendar
        if (addedEvents != null) {
            for (int x = 0; x < addedEvents.size(); x++) {
                Event event = null;
                try {
                    event = new Event(Color.BLACK, myMilliSecConvert(addedEvents.get(x).split(splitindicator)[0]));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                compactCalendar.addEvent(event);
            }
        }

        // retrieve events on clicked on day and display in recycler view
        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                theDaysEvents.clear();
                retrieveEvents(dateClicked);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                mCurrentDate.setText(dateFormat.format(firstDayOfNewMonth));
            }
        });

        setRecyclerView(view);
    }

    private void setRecyclerView(View view) {
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new CalendarAdapter(getContext(),theDaysEvents);
        rvCal.setLayoutManager(mLayoutManager);
        rvCal.setAdapter(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback((CalendarAdapter) mAdapter));
        itemTouchHelper.attachToRecyclerView(rvCal);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public long myMilliSecConvert(String date) throws ParseException {
        Date milliDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        epochTime = milliDate.getTime();
        return epochTime;
    }

    private void retrieveEvents(Date date) {
        String numberDate = simpleDateFormat.format(date);
        if (addedEvents != null) {
            for (int x = 0; x < addedEvents.size(); x++) {
                String[] eventarray = addedEvents.get(x).split(splitindicator);
                if (numberDate.equals(eventarray[0])) {
                    String eventName = eventarray[2];
                    theDaysEvents.add(eventName);
                }
            }
            if (theDaysEvents.size() == 0) {
                theDaysEvents.add("NONE!");
            }
        }
    }

}
