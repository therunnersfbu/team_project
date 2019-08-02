package com.example.team_project.calendar;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.team_project.PublicVariables;
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
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.Unbinder;

// The MyCalendarFragment displays the calendar and allows for the user to open the fragment with the display of that day's
// events and allows the user to click on variuous days and see all their added spots for that day
public class MyCalendarFragment extends Fragment{
    private Unbinder mUnbinder;
    private CompactCalendarView mCompactCalendar;
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
    private Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
    private TextView mCurrentDate;
    private RecyclerView mCalRV;
    private Long mEpochTime;
    private ParseUser user = ParseUser.getCurrentUser();
    private ArrayList<String> addedEvents = (ArrayList<String>) user.get(User.KEY_ADDED_EVENTS);
    private ArrayList<String> theDaysEvents;
    private RecyclerView.LayoutManager mLayoutManager;
    private LayoutInflater mNoneLayoutInflater;
    private RecyclerView.Adapter mAdapter;
    private TextView mNoneTV;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_my_calendar, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        //initUserData();
        return view;
    }

    // ensures and checks if there is user data available and if so it initializes the list
    /*private void initUserData() {
        user = ParseUser.getCurrentUser();
        if (user != null) {
            user.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject object, com.parse.ParseException e) {
                    addedEvents = (ArrayList<String>) user.get(User.KEY_ADDED_EVENTS);
                }
            });
        }else{
            Log.d(getResources().getString(R.string.calendar_frag_tag), getResources().getString(R.string.user_error_message));
            return;
        }
    }*/

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCalRV = view.findViewById(R.id.rvCal);
        mNoneTV = view.findViewById(R.id.tvNoEvent);
        theDaysEvents = new ArrayList<>();

        mCurrentDate = view.findViewById(R.id.current_Date);
        String currentDate = mDateFormat.format(calendar.getTime());
        mCurrentDate.setText(currentDate);

        mCompactCalendar = view.findViewById(R.id.compactcalendar_view);
        mCompactCalendar.setUseThreeLetterAbbreviation(true);

        Date mToday = Calendar.getInstance().getTime();
        retrieveEvents(mToday);
        addSpotDots();

        // retrieve events on clicked on day and display in recycler view
        mCompactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                theDaysEvents.clear();
                retrieveEvents(dateClicked);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                mCurrentDate.setText(mDateFormat.format(firstDayOfNewMonth));
            }
        });
        setRecyclerView(view);
    }

    private void setRecyclerView(View view) {
        mLayoutManager = new LinearLayoutManager(getContext());
        mAdapter = new CalendarAdapter(getContext(),theDaysEvents);
        mCalRV.setLayoutManager(mLayoutManager);
        mCalRV.setAdapter(mAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback((CalendarAdapter) mAdapter));
        itemTouchHelper.attachToRecyclerView(mCalRV);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    public long myMilliSecConvert(String date) throws ParseException {
        Date milliDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        mEpochTime = milliDate.getTime();
        return mEpochTime;
    }

    private void retrieveEvents(Date date) {
        String numberDate = mSimpleDateFormat.format(date);
        if (addedEvents != null) {
            for (int x = 0; x < addedEvents.size(); x++) {
                String[] eventarray = addedEvents.get(x).split(PublicVariables.splitindicator);
                if (numberDate.equals(eventarray[0])) {
                    String eventName = eventarray[2];
                    theDaysEvents.add(eventName);
                }
            }
            if (theDaysEvents.size() == 0) {
                mNoneTV.setVisibility(View.VISIBLE);

            }
        }
    }

    private void addSpotDots (){
        if (addedEvents != null) {
            for (int x = 0; x < addedEvents.size(); x++) {
                Event event = null;
                try {
                    event = new Event(Color.BLACK, myMilliSecConvert(addedEvents.get(x).split(PublicVariables.splitindicator)[0]));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mCompactCalendar.addEvent(event);
            }
        }
    }

}
