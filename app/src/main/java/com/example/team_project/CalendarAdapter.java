package com.example.team_project;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.team_project.model.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private List<Event> mEvents;
    Context context;
    // pass in the Tweets array into the constructor
    public CalendarAdapter(ArrayList<Event> events) {
        mEvents = events;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View eventView = inflater.inflate(R.layout.item_calevent, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(eventView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Event event = mEvents.get(position);

        viewHolder.tvEventName.setText((CharSequence) mEvents);
        //viewHolder.tvDescription.setText(tweet.body);
        //viewHolder.tvLocation.setText(getRelativeTimeAgo(tweet.createdAt));

    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvEventName;
        public TextView tvDescription;
        public TextView tvLocation;

        public ViewHolder(View itemView) {
            super(itemView);

            // perform findViewById lookups
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            // to enable details view
            //itemView.setOnClickListener(this);
        }
    }

    // TODO to click event and see detailview
    /*@Override
    public void onClick(View v) {
        //gets item position
        int position = getAdapterPosition();
        //make sure the position is valid
        if (position != RecyclerView.NO_POSITION) {
            //get the tweet at the position
            Tweet tweet = mTweets.get(position);
            //create intent for the new activity
            Intent intent = new Intent(context, DetailView.class);
            //serialize the tweet using the parceler,
            intent.putExtra(Tweet.class.getSimpleName(), Parcels.wrap(tweet));
            //show activity
            context.startActivity(intent);
        }
    } */

}
