package com.example.team_project;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.team_project.api.EventsApi;
import com.example.team_project.api.PlacesApi;
import com.example.team_project.model.Event;
import com.example.team_project.model.Place;
import com.example.team_project.model.Post;

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
    private Event mEvent;
    private Place mPlace;

    private HeaderViewHolder test;
    private ItemViewHolder testP;

    public EventsDetailsAdapter(List<Post> posts, String id, Boolean type, String distance) {
        this.posts = posts;
        this.id = id;
        this.type = type;
        this.distance = distance;

    }
    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        private TextView tvEventName;
        private TextView tvDistance;
        private TextView tvVenue;
        private TextView tvDate;
        private TextView tvAddress;
        private TextView tvNumber;
        private TextView tvPrice;
        private TextView tvHours;
        private Button btnReview;


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
            btnReview = (Button) view.findViewById(R.id.btnReview);

            if(!type) {
                EventsApi eApi = new EventsApi(EventsDetailsAdapter.this);
                eApi.getSingleEvent(id);
            }
            else {
                PlacesApi pApi = new PlacesApi(EventsDetailsAdapter.this);
                pApi.getDetails(id);
            }

            btnReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ComposeReviewActivity.class);
                    intent.putExtra("eventID", id);
                    if(type) {
                        intent.putExtra("eventName", mPlace.getPlaceName());
                    } else {
                        intent.putExtra("eventName", mEvent.getEventName());
                    }
                    v.getContext().startActivity(intent);
                }
            });
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

    public void finishedApi(Event event) {
        test.tvEventName.setText(event.getEventName());
        test.tvDistance.setText(distance);
        test.tvAddress.setText(event.getAddress());
        test.tvDate.setText(event.getStartTime());
        test.tvVenue.setText(event.getVenueName());
        mEvent = event;
    }

    public void finishedApiPlace(Place place) {
        test.tvEventName.setText(place.getPlaceName());
        test.tvDistance.setText(distance);
        test.tvAddress.setText(place.getAddress());
        test.tvHours.setText(place.getOpenHours().get(0));
        test.tvNumber.setText(place.getPhoneNumber());
        test.tvPrice.setText(place.getPrice());
        mPlace = place;
    }
}
