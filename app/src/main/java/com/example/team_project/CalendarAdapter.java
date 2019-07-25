package com.example.team_project;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    ArrayList<String> events;
    ArrayList<String> neweventlist;
    Context context;

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

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvEventName;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            tvEventName = itemView.findViewById(R.id.tvEventName);

            itemView.setOnTouchListener(new OnSwipeTouchListener(context) {
                public void onSwipeLeft() {

                    //make a new array list with what i want to remove and then user"remove" to get it
                    // get array list from parse then remove item and then send it back to parse
                    //get, then remove it, then put
                      //      notifydatasetchanged();
                }

            });
        }

    }
}
