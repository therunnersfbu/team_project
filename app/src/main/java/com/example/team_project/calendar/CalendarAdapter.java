package com.example.team_project.calendar;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.team_project.PublicVariables;
import com.example.team_project.R;
import com.example.team_project.api.DirectionsApi;
import com.example.team_project.details.DetailsActivity;
import com.example.team_project.model.PlaceEvent;
import com.example.team_project.model.User;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

// the Calendar Adapter allows for the spots information to be seen within the recycler view of the CalendarFragment
// and allows the user to click a spot in the recycler view and be sent to the Details Activity for that specific event
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    ArrayList<String> events;
    Context context;
    private ParseUser user = ParseUser.getCurrentUser();
    private ArrayList<String> parseevents = (ArrayList<String>) user.get(User.KEY_ADDED_EVENTS);
    private int count;
    private TextView tvEventName;
    private TextView tvAddress;
    private TextView tvEventPlace;
    private ImageView ivEventImage;
    Fragment mMapFragment;


    public CalendarAdapter(Context context, ArrayList<String> theDaysEvents) {
        this.events = theDaysEvents;
        this.context = context;
        //mMapFragment = mFragment;
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
            tvEventName.setText("NONE!");
            tvAddress.setVisibility(View.GONE);
            tvEventPlace.setVisibility(View.GONE);
            ivEventImage.setVisibility(View.GONE);
        }else {
            for (int x = 0; x < parseevents.size(); x++) {
                tvAddress.setVisibility(View.VISIBLE);
                tvEventPlace.setVisibility(View.VISIBLE);
                ivEventImage.setVisibility(View.VISIBLE);
                String[] mParseEvent = parseevents.get(x).split(PublicVariables.splitindicator);
                if (events.get(i).equals(mParseEvent[2])) {
                    tvEventName.setText(mParseEvent[2]);
                    tvAddress.setText(mParseEvent[3]);
                    String eventApiId = mParseEvent[1];
                    //mMapFragment.getSpotType
                    if ('E' != eventApiId.charAt(0)) {
                        tvEventPlace.setText("Place");
                        ivEventImage.setImageResource(R.drawable.sky);
                        break;
                    } else {
                        tvEventPlace.setText("Event");
                        ivEventImage.setImageResource(R.drawable.event);
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
        events.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{



        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            tvEventName = itemView.findViewById(R.id.tvEventName);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvEventPlace = itemView.findViewById(R.id.tvType);
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
                    if (eventname.equals(parseevents.get(x).split(PublicVariables.splitindicator)[2])) {
                        String eventapi = parseevents.get(x).split(PublicVariables.splitindicator)[1];
                        DirectionsApi api = new DirectionsApi(CalendarAdapter.this);
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
        Boolean isEvent;
        String eventApiId = parseevents.get(count).split(PublicVariables.splitindicator)[1];
        if ('E' != eventApiId.charAt(0)) {
            isEvent = true;
        } else {
            isEvent = false;
        }

        Intent intent = new Intent(context, DetailsActivity.class);
        intent.putExtra("eventID", eventApiId);
        intent.putExtra("type", isEvent);
        intent.putExtra("distance", distanceApi);
        context.startActivity(intent);
    }

    public PlaceEvent query(String id) {
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
