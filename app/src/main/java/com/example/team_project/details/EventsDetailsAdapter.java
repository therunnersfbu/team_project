package com.example.team_project.details;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;
import com.bumptech.glide.Glide;
import com.example.team_project.BottomNavActivity;
import com.example.team_project.ComposeReviewActivity;
import com.example.team_project.PublicVariables;
import com.example.team_project.R;
import com.example.team_project.account.OtherUserActivity;
import com.example.team_project.api.EventsApi;
import com.example.team_project.api.PlacesApi;
import com.example.team_project.fragments.EventsFragment;
import com.example.team_project.model.Event;
import com.example.team_project.model.Place;
import com.example.team_project.model.PlaceEvent;
import com.example.team_project.model.Post;
import com.example.team_project.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

// adapter for the item layouts used on the events details page. Includes header and item views that inflate in a RecyclerView
//TODO: mXXX
//TODO: folder for events, maps, etc (each main feature should have own folder)
public class EventsDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements EventsApi.GetEvents, PlacesApi.GetPlaces {

    private static final String TAG = EventsDetailsAdapter.class.getSimpleName();
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    //TODO private static string tags for eventsID etc
    private List<Post> mPosts;
    private String id;
    private boolean type;
    private String distance;
    private String coords;
    private Event mEvent;
    private Place mPlace;
    private DatePickerDialog picker;
    private HeaderViewHolder viewHolder;
    private ItemViewHolder viewHolderP;
    private Context context;
    private boolean isLocal;
    private float lastX;
    private RecyclerView mRecyclerView;

    public EventsDetailsAdapter(ArrayList<Post> mPosts, String id, Boolean type, String distance, Context context) {
        this.id = id;
        this.type = type;
        this.distance = distance;
        this.context = context;
        this.mPosts = mPosts;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tvEventName) TextView tvEventName;
        @BindView(R.id.tvDistance) TextView tvDistance;
        @BindView(R.id.tvAddress) TextView tvAddress;
        @BindView(R.id.ivAdd) ImageView ivAdd;
        @BindView(R.id.ivLike) ImageView ivLike;
        @BindView(R.id.vfGallery) ViewFlipper vfGallery;

        @Nullable @BindView(R.id.tvVenue) TextView tvVenue;
        @Nullable @BindView(R.id.tvNumber) TextView tvNumber;
        @Nullable @BindView(R.id.tvPrice) TextView tvPrice;
        @Nullable @BindView(R.id.tvHours) TextView tvHours;
        @Nullable @BindView(R.id.tvDate) TextView tvDate;

        @OnClick(R.id.btnReview)
        public void writeReview(Button button) {
            Intent intent = new Intent(button.getContext(), ComposeReviewActivity.class);
            intent.putExtra("eventID", id);
            String location;

            if(type) {
                location = mPlace.getLocation();
                intent.putExtra("eventName", mPlace.getPlaceName());
            } else {
                location = mEvent.getLocation();
                intent.putExtra("eventName", mEvent.getEventName());
            }

            intent.putExtra("location", location);
            button.getContext().startActivity(intent);
        }

        public HeaderViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);

            //TODO enum place / event
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

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private boolean expanded;

        @BindDrawable(R.drawable.ic_person_black_24dp) Drawable defaultPic;
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.ivProfilePic) ImageView ivProfilePic;
        @BindView(R.id.tvBody) TextView tvBody;

        public ItemViewHolder(@NonNull View view) {
            super(view);

            ButterKnife.bind(this, view);
            expanded = false;
            view.setOnClickListener(this);
        }

        public void bind(Post post) {
            final ParseUser user = post.getUser();
            tvName.setText(user.getString(User.KEY_NAME));
            tvBody.setText(post.getReview());
            ivProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BottomNavActivity.targetUser = user;
                    final Intent intent = new Intent(DetailsActivity.detailsAct, OtherUserActivity.class);
                    DetailsActivity.detailsAct.startActivity(intent);
                }
            });

            ParseFile imageFile = user.getParseFile(User.KEY_PROFILE_PIC);
            if (imageFile != null) {
                Glide.with(context)
                        .load(imageFile.getUrl())
                        .placeholder(defaultPic)
                        .error(defaultPic)
                        .into(ivProfilePic);
            } else {
                Glide.with(context)
                        .load(defaultPic)
                        .placeholder(defaultPic)
                        .error(defaultPic)
                        .into(ivProfilePic);
            }
        }

        @Override
        public void onClick(View v) {
            if (expanded) {
                tvBody.setSingleLine(true);
                tvBody.setEllipsize(TextUtils.TruncateAt.END);
            } else {
                tvBody.setSingleLine(false);
                tvBody.setEllipsize(null);
            }
            expanded = !expanded;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER) {
            if(!type) {
                View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_details, parent, false);
                viewHolder = new HeaderViewHolder(layoutView);
            } else {
                View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_place, parent, false);
                viewHolder = new HeaderViewHolder(layoutView);
            }
            return viewHolder;
        } else if (viewType == TYPE_ITEM) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment_preview, parent, false);
            return new ItemViewHolder(layoutView);
        }

        throw new RuntimeException("No match for viewtype");
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).bind(mPosts.get(position));
        }

    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    @Override
    public void gotEvents(JSONArray eventsApi) {

    }

    @Override
    public void gotEvent(final Event eventApi) {
        viewHolder.tvEventName.setText(eventApi.getEventName());
        viewHolder.tvDistance.setText(distance);
        viewHolder.tvAddress.setText(eventApi.getAddress());
        viewHolder.tvDate.setText(eventApi.getStartTime());
        viewHolder.tvVenue.setText(eventApi.getVenueName());
        coords = eventApi.getLocation();
        mEvent = eventApi;

        ParseUser user = ParseUser.getCurrentUser();
        ArrayList<String> liked = (ArrayList<String>) user.get(User.KEY_LIKED_EVENTS);
        String toLike = eventApi.getEventId() + PublicVariables.separator
                + eventApi.getEventName() + PublicVariables.separator + eventApi.getAddress();
        if (liked.contains(toLike)) {
            viewHolder.ivLike.setActivated(true);
        }

        viewHolder.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPlaceEventExists(eventApi.getEventName());
                ParseUser user = ParseUser.getCurrentUser();
                ArrayList<String> added = (ArrayList<String>) user.get(User.KEY_ADDED_EVENTS);
                String eventToAdd = eventApi.getStartTime().substring(0, 10) + PublicVariables.separator +
                        eventApi.getEventId() + PublicVariables.separator + eventApi.getEventName() +
                        PublicVariables.separator + eventApi.getAddress();
                if (added.contains(eventToAdd)) {
                    Toast.makeText(context, "Event already added", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "already there");
                } else {
                    added.add(eventToAdd);
                    user.put(User.KEY_ADDED_EVENTS, added);
                    Toast.makeText(context, "Event added to your calendar", Toast.LENGTH_LONG).show();
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

        viewHolder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPlaceEventExists(eventApi.getEventName());
                v.setActivated(!v.isActivated());
                ParseUser user = ParseUser.getCurrentUser();
                ArrayList<String> liked = (ArrayList<String>) user.get(User.KEY_LIKED_EVENTS);
                String toLike = eventApi.getEventId() + PublicVariables.separator + eventApi.getEventName() +
                        PublicVariables.separator + eventApi.getAddress();
                if (!liked.remove(toLike)) {
                    liked.add(toLike);
                }
                user.put(User.KEY_LIKED_EVENTS, liked);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d(TAG, "liked!");
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        setImages();
    }

    @Override
    public void gotPlaces(JSONArray placesApi) {

    }

    @Override
    public void gotPlace(final Place placeApi) {
        viewHolder.tvEventName.setText(placeApi.getPlaceName());
        viewHolder.tvDistance.setText(distance);
        viewHolder.tvAddress.setText(placeApi.getAddress());
        viewHolder.tvHours.setText(placeApi.getOpenHours().get(0));
        viewHolder.tvNumber.setText(placeApi.getPhoneNumber());
        viewHolder.tvPrice.setText(placeApi.getPrice());
        coords = placeApi.getLocation();
        mPlace = placeApi;

        ParseUser user = ParseUser.getCurrentUser();
        ArrayList<String> liked = (ArrayList<String>) user.get(User.KEY_LIKED_EVENTS);
        String toLike = placeApi.getPlaceId() + PublicVariables.separator + placeApi.getPlaceName() +
                PublicVariables.separator + placeApi.getAddress();
        if (liked.contains(toLike)) {
            viewHolder.ivLike.setActivated(true);
        }

        viewHolder.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPlaceEventExists(placeApi.getPlaceName());
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
                                        + PublicVariables.separator + placeApi.getPlaceId() + PublicVariables.separator +
                                        placeApi.getPlaceName() + PublicVariables.separator + placeApi.getAddress();
                                Log.d(TAG, placeToAdd);
                                if (added.contains(placeToAdd)) {
                                    Toast.makeText(context, "Event already added", Toast.LENGTH_LONG).show();
                                    Log.d(TAG, "already there");
                                } else {
                                    added.add(placeToAdd);
                                    user.put(User.KEY_ADDED_EVENTS, added);
                                    Toast.makeText(context, "Event added to your calendar", Toast.LENGTH_LONG).show();
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

        viewHolder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPlaceEventExists(placeApi.getPlaceName());
                v.setActivated(!v.isActivated());
                ParseUser user = ParseUser.getCurrentUser();
                ArrayList<String> liked = (ArrayList<String>) user.get(User.KEY_LIKED_EVENTS);
                String toLike = placeApi.getPlaceId() + PublicVariables.separator + placeApi.getPlaceName() +
                        PublicVariables.separator + placeApi.getAddress();
                if (!liked.remove(toLike)) {
                    liked.add(toLike);
                }
                user.put(User.KEY_LIKED_EVENTS, liked);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d(TAG, "liked!");
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        setImages();
    }

    private void checkPlaceEventExists(final String name) {
        final ArrayList<PlaceEvent> placeEventList = new ArrayList<>();
        final PlaceEvent[] placeEvent = {new PlaceEvent()};
        ParseQuery parseQuery = new ParseQuery("PlaceEvent");
        parseQuery.setLimit(1000);

        parseQuery.findInBackground(new FindCallback<PlaceEvent>() {
            @Override
            public void done(List<PlaceEvent> objects, ParseException e) {
                if (e == null) {
                    placeEventList.addAll(objects);
                    for (int i = 0; i < placeEventList.size(); i++) {
                        if (id.equals(placeEventList.get(i).getAppId())) {
                            return;
                        }
                    }

                    placeEvent[0] = new PlaceEvent();
                    ArrayList<Boolean> categories = new ArrayList<>();
                    ArrayList<Integer> tags = new ArrayList<>();
                    for (int i = 0; i < 12; i++) {
                        categories.add(false);
                    }
                    for (int i = 0; i < 20; i++) {
                        tags.add(0);
                    }
                    if (EventsFragment.categoryToMark > -1) {
                        categories.set(EventsFragment.categoryToMark, true);
                    }

                    placeEvent[0].put(PlaceEvent.KEY_API, id);
                    placeEvent[0].put(PlaceEvent.KEY_CATEGORIES, categories);
                    placeEvent[0].put(PlaceEvent.KEY_TAGS, tags);
                    placeEvent[0].setName(name);
                    placeEvent[0].setCoordinates(coords);
                    placeEvent[0].saveInBackground();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setImages() {
        int count = 0;
        for (Post i : mPosts) {
            ParseFile file = i.getImage();
            if (file != null) {
                count++;
                viewHolder.vfGallery.addView(createGalleryItem(file));
            }
        }

        if (count == 0) {
            viewHolder.vfGallery.addView(createPlaceholder());
        } else if (count > 1) {
            viewHolder.vfGallery.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent touchevent) {
                    switch (touchevent.getAction()) {
                        // when user first touches the screen to swap
                        case MotionEvent.ACTION_DOWN: {
                            lastX = touchevent.getX();
                            break;
                        }
                        case MotionEvent.ACTION_UP: {
                            float currentX = touchevent.getX();

                            // if left to right swipe on screen
                            if (lastX < currentX) {
                                // If no more View/Child to flip
                                if (viewHolder.vfGallery.getDisplayedChild() == 0)
                                    break;

                                // set the required Animation type to ViewFlipper
                                // The Next screen will come in form Left and current Screen will go OUT from Right
                                viewHolder.vfGallery.setInAnimation(context, R.anim.in_from_left);
                                viewHolder.vfGallery.setOutAnimation(context, R.anim.out_to_right);
                                // Show the next Screen
                                viewHolder.vfGallery.showNext();
                            }

                            // if right to left swipe on screen
                            if (lastX > currentX) {
                                if (viewHolder.vfGallery.getDisplayedChild() == 1)
                                    break;
                                // set the required Animation type to ViewFlipper
                                // The Next screen will come in form Right and current Screen will go OUT from Left
                                viewHolder.vfGallery.setInAnimation(context, R.anim.in_from_right);
                                viewHolder.vfGallery.setOutAnimation(context, R.anim.out_to_left);
                                // Show The Previous Screen
                                viewHolder.vfGallery.showPrevious();
                            }
                            break;
                        }
                    }
                    return true;
                }
            });
        }
    }

    private ImageView createGalleryItem(ParseFile file) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView view = (ImageView) inflater.inflate(R.layout.item_gallery, null);
        Glide.with(context).load(file.getUrl()).into(view);
        return view;
    }

    private  TextView createPlaceholder() {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return (TextView) inflater.inflate(R.layout.item_gallery_placeholder, null);
    }
}
