package com.example.team_project;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.team_project.api.EventsApi;
import com.example.team_project.api.PlacesApi;
import com.example.team_project.model.Event;
import com.example.team_project.model.Place;
import com.example.team_project.model.Post;
import com.example.team_project.model.User;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// adapter for the item layouts used on the events details page. Includes header and item views that inflate in a RecyclerView
public class EventsDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = EventsDetailsAdapter.class.getSimpleName();
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<Post> posts;
    private String id;
    private boolean type;
    private String distance;
    private DatePickerDialog picker;
    private HeaderViewHolder test;
    private ItemViewHolder testP;
    private Context context;

    public EventsDetailsAdapter(List<Post> posts, String id, Boolean type, String distance, Context context) {
        this.posts = posts;
        this.id = id;
        this.type = type;
        this.distance = distance;
        this.context = context;
    }
    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        public TextView tvEventName;
        public TextView tvDistance;
        public TextView tvVenue;
        public TextView tvDate;
        public TextView tvAddress;
        public TextView tvNumber;
        public TextView tvPrice;
        public TextView tvHours;
        public ImageView ivAdd;


        public HeaderViewHolder(@NonNull View view) {
            super(view);
            tvEventName = (TextView) view.findViewById(R.id.tvEventName);
            tvDistance = (TextView) view.findViewById(R.id.tvDistance);
            tvVenue = (TextView) view.findViewById(R.id.tvVenue);
            tvDate = (TextView) view.findViewById(R.id.tvDate);
            tvAddress = (TextView) view.findViewById(R.id.tvAddress);
            tvNumber = (TextView) view.findViewById(R.id.tvNumber);
            tvPrice = (TextView) view.findViewById(R.id.tvPrice);
            tvHours = (TextView) view.findViewById(R.id.tvHours);
            ivAdd = (ImageView) view.findViewById(R.id.ivAdd);

            if(!type) {
                EventsApi eApi = new EventsApi(EventsDetailsAdapter.this);
                eApi.getSingleEvent(id);
            }
            else {
                PlacesApi pApi = new PlacesApi(EventsDetailsAdapter.this);
                pApi.getDetails(id);
            }
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;

        public ItemViewHolder(@NonNull View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER) {
            if(!type) {
                View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_details, parent, false);
                test = new HeaderViewHolder(layoutView);
            } else {
                View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_place, parent, false);
                test = new HeaderViewHolder(layoutView);
            }
            return test;
        } else if (viewType == TYPE_ITEM) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_preview, parent, false);
            return new ItemViewHolder(layoutView);
        }

        throw new RuntimeException("No match for viewtype");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Post mPost = posts.get(position);
        if(holder instanceof HeaderViewHolder) {
            //TODO
            //((HeaderViewHolder) holder).tvEventName.setText(((Event) mPost.getEvent()).getEventName());
        } else if (holder instanceof ItemViewHolder) {
            //TODO
            //((ItemViewHolder) holder).tvName.setText(mPost.getUser().getUsername());
        }

    }

    private Post getPost(int position) {
        return posts.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (isHeader(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    private boolean isHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void finishedApi(final Event event) {
        test.tvEventName.setText(event.getEventName());
        test.tvDistance.setText(distance);
        test.tvAddress.setText(event.getAddress());
        test.tvDate.setText(event.getStartTime());
        test.tvVenue.setText(event.getVenueName());
        test.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser user = ParseUser.getCurrentUser();
                ArrayList<String> added = (ArrayList<String>) user.get(User.KEY_ADDED_EVENTS);
                String eventToAdd = event.getStartTime().substring(0, 10) + " " + event.getEventName();
                if (added.contains(eventToAdd)) {
                    Log.d(TAG, "already there");
                } else {
                    added.add(eventToAdd);
                    user.put(User.KEY_ADDED_EVENTS, added);
                    user.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d(TAG, "event added");
                            } else {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    public void finishedApiPlace(final Place place) {
        test.tvEventName.setText(place.getPlaceName());
        test.tvDistance.setText(distance);
        test.tvAddress.setText(place.getAddress());
        test.tvHours.setText(place.getOpenHours().get(0));
        test.tvNumber.setText(place.getPhoneNumber());
        test.tvPrice.setText(place.getPrice());
        test.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                ParseUser user = ParseUser.getCurrentUser();
                                ArrayList<String> added = (ArrayList<String>) user.get(User.KEY_ADDED_EVENTS);
                                String placeToAdd = year + "-" +
                                        ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1))
                                        + "-" +
                                        (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth)
                                        + " " + place.getPlaceName();
                                Log.d(TAG, placeToAdd);
                                if (added.contains(placeToAdd)) {
                                    Log.d(TAG, "already there");
                                } else {
                                    added.add(placeToAdd);
                                    user.put(User.KEY_ADDED_EVENTS, added);
                                    user.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            if (e == null) {
                                                Log.d(TAG, "place added");
                                            } else {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                }
                            }
                        }, year, month, day);
                picker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                picker.show();
            }
        });
    }
}