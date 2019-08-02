package com.example.team_project.calendar;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import com.example.team_project.utils.ContextProvider;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

// TODO fix on Click
// the Calendar Adapter allows for the spots information to be seen within the recycler view of the CalendarFragment
// and allows the user to click a spot in the recycler view and be sent to the Details Activity for that specific event
public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> implements DirectionsApi.GetSingleDistance {
    private ArrayList<String> mEvents;
    private Context mContext;
    private ParseUser user = ParseUser.getCurrentUser();
    private ArrayList<String> parseevents = (ArrayList<String>) user.get(User.KEY_ADDED_EVENTS);
    private int mCount;
    private TextView mTVEventName;
    private TextView mTVAddress;
    private TextView mTVEventPlace;
    private ImageView mIVEventImage;


    public CalendarAdapter(ContextProvider cp, ArrayList<String> theDaysEvents) {
        this.mEvents = theDaysEvents;
        this.mContext = cp.getContext();
    }

    public Context getmContext() {
        return this.mContext;
    }

    public ArrayList<String> getmEvents() {
        return this.mEvents;
    }

    @NonNull
    @Override
    public CalendarAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_calevent, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CalendarAdapter.ViewHolder viewHolder, int i) {
            for (int x = 0; x < parseevents.size(); x++) {
                String[] mParseEvent = parseevents.get(x).split(PublicVariables.splitindicator);
                if (mEvents.get(i).equals(mParseEvent[2])) {
                    mTVEventName.setText(mParseEvent[2]);
                    mTVAddress.setText(mParseEvent[3]);
                    String eventApiId = mParseEvent[1];
                    if ('E' != eventApiId.charAt(0)) {
                        mTVEventPlace.setText("Place");
                        mIVEventImage.setImageResource(R.drawable.sky);
                        break;
                    } else {
                        mTVEventPlace.setText("Event");
                        mIVEventImage.setImageResource(R.drawable.event);
                        break;
                    }
                }
        }
    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }

    public void deleteItem(int position) {
        mEvents.remove(position);
        notifyItemRemoved(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            mTVEventName = itemView.findViewById(R.id.tvEventName);
            mTVAddress = itemView.findViewById(R.id.tvAddress);
            mTVEventPlace = itemView.findViewById(R.id.tvType);
            mIVEventImage = itemView.findViewById(R.id.ivEventImage);
            itemView.setOnClickListener(this);
        }

        // to click event and be taken to DetailsActivity
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                String eventname = mEvents.get(position);
                for (int x = 0; x < parseevents.size(); x++) {
                    if (eventname.equals(parseevents.get(x).split(PublicVariables.splitindicator)[2])) {
                        String eventapi = parseevents.get(x).split(PublicVariables.splitindicator)[1];
                        DirectionsApi api = new DirectionsApi(CalendarAdapter.this);
                        PlaceEvent mParseEvent = query(eventapi);
                        mCount = x;
                        api.addDestination(mParseEvent.getCoordinates().replace(" ", ","));
                        api.getDistance();
                    }
                }
            }
        }
    }

    public void gotDistance(String distanceApi) {
        Boolean isEvent;
        String eventApiId = parseevents.get(mCount).split(PublicVariables.splitindicator)[1];
        if ('E' != eventApiId.charAt(0)) {
            isEvent = true;
        } else {
            isEvent = false;
        }
        // string must be hardcoded because you cannot store strings in String.xml and retrieve
        Intent intent = new Intent(mContext, DetailsActivity.class);
        intent.putExtra("eventID", eventApiId);
        intent.putExtra("type", isEvent);
        intent.putExtra("distance", distanceApi);
        mContext.startActivity(intent);
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
