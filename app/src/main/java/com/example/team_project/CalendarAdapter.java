package com.example.team_project;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private List<String> mEvents;
    Context context;
    // pass in the Tweets array into the constructor
    public CalendarAdapter(ArrayList<String> theDaysEvents) {
        mEvents = theDaysEvents;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_calevent, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        String events = mEvents.get(position);
        viewHolder.bind(events);
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEventName;

        public ViewHolder(View itemView) {
            super(itemView);

            tvEventName = itemView.findViewById(R.id.tvEventName);

        }

        public void bind(String events) {
            // bind the view elements to the post
            tvEventName.setText(events);
        }
    }

}
