package com.example.team_project.calendar;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.team_project.R;
import com.example.team_project.model.Event;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    ArrayList<String> events;
    ArrayList<Event> dotEvents;
    String mRecentlyDeletedItem;
    int mRecentlyDeletedItemPosition;
    CompactCalendarView compactCalendar;


    public CalendarAdapter(ArrayList<String> theDaysEvents) {
        this.events = theDaysEvents;
    }


    @NonNull
    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_calevent, viewGroup, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull CalendarAdapter.ViewHolder viewHolder, int i) {
        //where we associate data with actual row
        viewHolder.tvEventName.setText(" - " + events.get(i));

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void deleteItem(int position) {
        mRecentlyDeletedItem = events.get(position);
        mRecentlyDeletedItemPosition = position;
        events.remove(position);
        notifyItemRemoved(position);
    }
    /*public void updateDots(ArrayList<String> parseevents) {
        for (int x = 0; x < parseevents.size(); x++) {
            com.github.sundeepk.compactcalendarview.domain.Event event = null;
            try {
                event = new com.github.sundeepk.compactcalendarview.domain.Event(Color.BLACK, myMilliSecConvert(parseevents.get(x).substring(0, 10)));
                Log.d("SwipeToDeleteCallBack", "event" + event);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            compactCalendar.addEvent(event);
        }
    }
    public long myMilliSecConvert(String date) throws ParseException {
        Date milliDate = new SimpleDateFormat("yyyy-MM-dd").parse(date);
        long epochTime = milliDate.getTime();
        return epochTime;
    }*/


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvEventName;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
        }

    }
}
