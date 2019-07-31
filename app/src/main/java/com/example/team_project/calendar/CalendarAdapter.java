package com.example.team_project.calendar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.team_project.R;
import com.example.team_project.details.DetailsActivity;
import com.example.team_project.model.Event;
import com.example.team_project.model.Post;
import com.example.team_project.model.User;
import com.example.team_project.search.SearchActivity;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.parse.ParseUser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.SocketHandler;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    ArrayList<String> events;
    String mRecentlyDeletedItem;
    int mRecentlyDeletedItemPosition;
    String splitindicator = "\\(\\)";
    Context context;
    ParseUser user = ParseUser.getCurrentUser();
    ArrayList<String> parseevents = (ArrayList<String>) user.get(User.KEY_ADDED_EVENTS);


    public CalendarAdapter(Context context, ArrayList<String> theDaysEvents) {
        this.events = theDaysEvents;
        this.context = context;
    }

    @NonNull
    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_calevent, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarAdapter.ViewHolder viewHolder, int i) {
        viewHolder.tvEventName.setText(events.get(i));

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



    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView tvEventName;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            itemView.setOnClickListener(this);
        }

        // to click event and be taken to DetailsActivity
        @Override
        public void onClick(View v) {
            //gets item position
            int position = getAdapterPosition();
            //make sure the position is valid
            if (position != RecyclerView.NO_POSITION) {
                //get the event at the position
                String eventname = events.get(position);
                for (int x = 0; x < parseevents.size(); x++) {

                    // match event
                    if (eventname.equals(parseevents.get(x).split(splitindicator)[3])) {
                        // get the apiId and distance
                        String distance = parseevents.get(x).split(splitindicator)[2];
                        Boolean type;
                        String eventApiId = parseevents.get(x).split(splitindicator)[1];
                        if ('E' != eventApiId.charAt(0)) {
                            type = true;
                        } else {
                            type = false;
                        }

                        Intent intent = new Intent(context, DetailsActivity.class);
                        intent.putExtra("eventID", eventApiId);
                        intent.putExtra("type", type);
                        intent.putExtra("distance", distance);
                        context.startActivity(intent);

                    }
                }
            }
        }
    }
}
