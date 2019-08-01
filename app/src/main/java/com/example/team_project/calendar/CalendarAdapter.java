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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.team_project.BottomNavActivity;
import com.example.team_project.R;
import com.example.team_project.api.DirectionsApi;
import com.example.team_project.details.DetailsActivity;
import com.example.team_project.fragments.MapFragment;
import com.example.team_project.model.Event;
import com.example.team_project.model.PlaceEvent;
import com.example.team_project.model.Post;
import com.example.team_project.model.User;
import com.example.team_project.search.SearchActivity;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.SocketHandler;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    ArrayList<String> events;
    private String mRecentlyDeletedItem;
    private int mRecentlyDeletedItemPosition;
    private String splitindicator = "\\(\\)";
    Context context;
    private ParseUser user = ParseUser.getCurrentUser();
    private ArrayList<String> parseevents = (ArrayList<String>) user.get(User.KEY_ADDED_EVENTS);
    private int count;


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
        if (events.contains("NONE!")) {
            viewHolder.tvEventName.setText("NONE!");
            viewHolder.tvAddress.setVisibility(View.GONE);
            viewHolder.tvType.setVisibility(View.GONE);
            viewHolder.ivEventImage.setVisibility(View.GONE);
        }else {
            for (int x = 0; x < parseevents.size(); x++) {
                viewHolder.tvAddress.setVisibility(View.VISIBLE);
                viewHolder.tvType.setVisibility(View.VISIBLE);
                viewHolder.ivEventImage.setVisibility(View.VISIBLE);
                // match event
                if (events.get(i).equals(parseevents.get(x).split(splitindicator)[2])) {
                    viewHolder.tvEventName.setText(parseevents.get(x).split(splitindicator)[2]);
                    viewHolder.tvAddress.setText(parseevents.get(x).split(splitindicator)[3]);
                    String eventApiId = parseevents.get(x).split(splitindicator)[1];
                    if ('E' != eventApiId.charAt(0)) {
                        viewHolder.tvType.setText("Place");
                        viewHolder.ivEventImage.setImageResource(R.drawable.sky);
                        break;
                    } else {
                        viewHolder.tvType.setText("Event");
                        viewHolder.ivEventImage.setImageResource(R.drawable.event);
                        break;
                    }
                }
            }
        }
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
        public TextView tvAddress;
        public TextView tvType;
        public ImageView ivEventImage;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvType = itemView.findViewById(R.id.tvType);
            ivEventImage = itemView.findViewById(R.id.ivEventImage);
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
                    if (eventname.equals(parseevents.get(x).split(splitindicator)[2])) {
                        // get the apiId and distance
                        String eventapi = parseevents.get(x).split(splitindicator)[1];
                        // create a new directions api object
                        DirectionsApi api = new DirectionsApi(CalendarAdapter.this);
                        api.setOrigin(BottomNavActivity.currentLat, BottomNavActivity.currentLng);

                        PlaceEvent mParseEvent = query(eventapi);

                        count = x;
                        api.addDestination(mParseEvent.getCoordinates().replace(" ", ","));
                        api.getDistance();
                    }
                }
            }
        }
    }

    public void gotDistance(String distanceApi) {
        Boolean type;
        String eventApiId = parseevents.get(count).split(splitindicator)[1];
        Log.d("CalAda", "id: " + eventApiId);
        if ('E' != eventApiId.charAt(0)) {
            type = true;
        } else {
            type = false;
        }

        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra("eventID", eventApiId);
        intent.putExtra("type", type);
        intent.putExtra("distance", distanceApi);
        context.startActivity(intent);
    }

    private PlaceEvent query(String id) {
        ParseQuery<PlaceEvent> query = new ParseQuery("PlaceEvent");
        query.whereContains("apiId", id);
        PlaceEvent mPlaceEvent = null;
        try {
            mPlaceEvent = (PlaceEvent) query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mPlaceEvent;
    }
}
