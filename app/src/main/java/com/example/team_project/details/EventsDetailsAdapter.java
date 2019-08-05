package com.example.team_project.details;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
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
import com.example.team_project.Constants;
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
import com.example.team_project.utils.ContextProvider;
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
public class EventsDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements EventsApi.GetEvents, PlacesApi.GetPlaces {

    private static final String TAG = EventsDetailsAdapter.class.getSimpleName();
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final String EVENT_ID = "eventID";
    private final String EVENT_NAME = "eventName";
    private final String LOCATION = "location";
    private final String LIKED_TOAST = "Liked!";
    private float lastX;
    private boolean isPlace;
    private List<Post> mPosts;
    private RecyclerView mRecyclerView;
    private String mId;
    private String mDistance;
    private String mCoords;
    private Event mEvent;
    private Place mPlace;
    private DatePickerDialog mPicker;
    private HeaderViewHolder mViewHolder;
    private Context mContext;
    private AdapterCallback mCallback;

    public EventsDetailsAdapter(ArrayList<Post> posts, String id, boolean isPlace, String distance, ContextProvider context) {
        this.mId = id;
        this.isPlace = isPlace;
        this.mDistance = distance;
        this.mContext = context.getContext();
        this.mPosts = posts;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mRecyclerView = recyclerView;
    }

    public void setOnItemClickedListener(EventsDetailsAdapter.AdapterCallback callback) {
        this.mCallback = callback;
    }

    public interface AdapterCallback {
        void onItemClicked();
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
            intent.putExtra(EVENT_ID, mId);
            String location;
            if(isPlace) {
                location = mPlace.getLocation();
                intent.putExtra(EVENT_NAME, mPlace.getPlaceName());
            } else {
                location = mEvent.getLocation();
                intent.putExtra(EVENT_NAME, mEvent.getEventName());
            }
            intent.putExtra(LOCATION, location);
            button.getContext().startActivity(intent);
        }

        public HeaderViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
            if(!isPlace) {
                EventsApi eApi = new EventsApi(EventsDetailsAdapter.this);
                eApi.getSingleEvent(mId);
            }
            else {
                PlacesApi pApi = new PlacesApi(EventsDetailsAdapter.this);
                pApi.getDetails(mId);
            }
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private boolean isExpanded;
        @BindDrawable(R.drawable.default_profile_pic) Drawable defaultPic;
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.ivProfilePic) ImageView ivProfilePic;
        @BindView(R.id.tvBody) TextView tvBody;

        public ItemViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
            isExpanded = false;
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
                    final Intent intent = new Intent(mContext, OtherUserActivity.class);
                    mContext.startActivity(intent);
                }
            });
            ParseFile imageFile = user.getParseFile(User.KEY_PROFILE_PIC);
            if (imageFile != null) {
                Glide.with(mContext)
                        .load(imageFile.getUrl())
                        .placeholder(defaultPic)
                        .error(defaultPic)
                        .into(ivProfilePic);
            } else {
                Glide.with(mContext)
                        .load(defaultPic)
                        .placeholder(defaultPic)
                        .error(defaultPic)
                        .into(ivProfilePic);
            }
        }

        @Override
        public void onClick(View v) {
            if (isExpanded) {
                tvBody.setSingleLine(true);
                tvBody.setEllipsize(TextUtils.TruncateAt.END);
            } else {
                tvBody.setSingleLine(false);
                tvBody.setEllipsize(null);
            }
            isExpanded = !isExpanded;
        }
    }

    //inflate correct layout file depending on header event, header place, or item comment
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEADER) {
            if(!isPlace) {
                View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_details, parent, false);
                mViewHolder = new HeaderViewHolder(layoutView);
            } else {
                View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_place, parent, false);
                mViewHolder = new HeaderViewHolder(layoutView);
            }
            return mViewHolder;
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

    // determine whether the view is of HEADER or ITEM type
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
        mViewHolder.tvEventName.setText(eventApi.getEventName());
        mViewHolder.tvDistance.setText(mDistance);
        mViewHolder.tvAddress.setText(eventApi.getAddress());
        mViewHolder.tvDate.setText(eventApi.getStartTime());
        mViewHolder.tvVenue.setText(eventApi.getVenueName());
        mCoords = eventApi.getLocation();
        mEvent = eventApi;

        ParseUser user = ParseUser.getCurrentUser();
        ArrayList<String> liked = (ArrayList<String>) user.get(User.KEY_LIKED_EVENTS);
        String toLike = eventApi.getEventId() + Constants.separator
                + eventApi.getEventName() + Constants.separator + eventApi.getAddress();

        if (liked.contains(toLike)) {
            mViewHolder.ivLike.setActivated(true);
        }
        mViewHolder.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPlaceEventExists(eventApi.getEventName());
                ParseUser user = ParseUser.getCurrentUser();
                ArrayList<String> added = (ArrayList<String>) user.get(User.KEY_ADDED_EVENTS);
                String eventToAdd = eventApi.getStartTime().substring(0, 10) + Constants.separator +
                        eventApi.getEventId() + Constants.separator + eventApi.getEventName() +
                        Constants.separator + eventApi.getAddress();
                if (added.contains(eventToAdd)) {
                    Toast.makeText(mContext, "Event already added", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "already there");
                } else {
                    added.add(eventToAdd);
                    user.put(User.KEY_ADDED_EVENTS, added);
                    Toast.makeText(mContext, "Event added to your calendar", Toast.LENGTH_LONG).show();
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

        //change color and alert parse dashboard when item is liked for event
        mViewHolder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPlaceEventExists(eventApi.getEventName());
                v.setActivated(!v.isActivated());
                ParseUser user = ParseUser.getCurrentUser();
                ArrayList<String> liked = (ArrayList<String>) user.get(User.KEY_LIKED_EVENTS);
                String toLike = eventApi.getEventId() + Constants.separator + eventApi.getEventName() +
                        Constants.separator + eventApi.getAddress();
                if (!liked.remove(toLike)) {
                    liked.add(toLike);
                }
                user.put(User.KEY_LIKED_EVENTS, liked);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d(TAG, LIKED_TOAST);
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        setImages();
        mCallback.onItemClicked();
    }

    @Override
    public void gotPlaces(JSONArray placesApi) {

    }

    @Override
    public void gotPlace(final Place placeApi) {
        mViewHolder.tvEventName.setText(placeApi.getPlaceName());
        mViewHolder.tvDistance.setText(mDistance);
        mViewHolder.tvAddress.setText(placeApi.getAddress());
        mViewHolder.tvHours.setText(placeApi.getOpenHours().get(0));
        mViewHolder.tvNumber.setText(placeApi.getPhoneNumber());
        mViewHolder.tvPrice.setText(placeApi.getPrice());
        mCoords = placeApi.getLocation();
        mPlace = placeApi;

        ParseUser user = ParseUser.getCurrentUser();
        ArrayList<String> liked = (ArrayList<String>) user.get(User.KEY_LIKED_EVENTS);
        String toLike = placeApi.getPlaceId() + Constants.separator + placeApi.getPlaceName() +
                Constants.separator + placeApi.getAddress();
        if (liked.contains(toLike)) {
            mViewHolder.ivLike.setActivated(true);
        }

        mViewHolder.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPlaceEventExists(placeApi.getPlaceName());
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date mPicker dialog
                mPicker = new DatePickerDialog(mContext,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                ParseUser user = ParseUser.getCurrentUser();
                                ArrayList<String> added = (ArrayList<String>) user.get(User.KEY_ADDED_EVENTS);
                                String placeToAdd = year + "-" +
                                        ((monthOfYear + 1) < 10 ? "0" + (monthOfYear + 1) : (monthOfYear + 1))
                                        + "-" +
                                        (dayOfMonth < 10 ? "0" + dayOfMonth : dayOfMonth)
                                        + Constants.separator + placeApi.getPlaceId() + Constants.separator +
                                        placeApi.getPlaceName() + Constants.separator + placeApi.getAddress();
                                Log.d(TAG, placeToAdd);
                                if (added.contains(placeToAdd)) {
                                    Toast.makeText(mContext, "Event already added", Toast.LENGTH_LONG).show();
                                    Log.d(TAG, "already there");
                                } else {
                                    added.add(placeToAdd);
                                    user.put(User.KEY_ADDED_EVENTS, added);
                                    Toast.makeText(mContext, "Event added to your calendar", Toast.LENGTH_LONG).show();
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
                mPicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                mPicker.show();
            }
        });

        //change color and alert parse dashboard when item is liked for place
        mViewHolder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPlaceEventExists(placeApi.getPlaceName());
                v.setActivated(!v.isActivated());
                ParseUser user = ParseUser.getCurrentUser();
                ArrayList<String> liked = (ArrayList<String>) user.get(User.KEY_LIKED_EVENTS);
                String toLike = placeApi.getPlaceId() + Constants.separator + placeApi.getPlaceName() +
                        Constants.separator + placeApi.getAddress();
                if (!liked.remove(toLike)) {
                    liked.add(toLike);
                }
                user.put(User.KEY_LIKED_EVENTS, liked);
                user.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d(TAG, LIKED_TOAST);
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        setImages();
        mCallback.onItemClicked();
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
                        if (mId.equals(placeEventList.get(i).getAppId())) {
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

                    placeEvent[0].put(PlaceEvent.KEY_API, mId);
                    placeEvent[0].put(PlaceEvent.KEY_CATEGORIES, categories);
                    placeEvent[0].put(PlaceEvent.KEY_TAGS, tags);
                    placeEvent[0].setName(name);
                    placeEvent[0].setCoordinates(mCoords);
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
                mViewHolder.vfGallery.addView(createGalleryItem(file));
            }
        }

        if (count == 0) {
            mViewHolder.vfGallery.addView(createPlaceholder());
        } else if (count > 1) {
            mViewHolder.vfGallery.setOnTouchListener(new View.OnTouchListener() {
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
                                if (mViewHolder.vfGallery.getDisplayedChild() == 0)
                                    break;

                                // set the required Animation isPlace to ViewFlipper
                                // The Next screen will come in form Left and current Screen will go OUT from Right
                                mViewHolder.vfGallery.setInAnimation(mContext, R.anim.in_from_left);
                                mViewHolder.vfGallery.setOutAnimation(mContext, R.anim.out_to_right);
                                // Show the next Screen
                                mViewHolder.vfGallery.showNext();
                            }

                            // if right to left swipe on screen
                            if (lastX > currentX) {
                                if (mViewHolder.vfGallery.getDisplayedChild() == 1)
                                    break;
                                // set the required Animation isPlace to ViewFlipper
                                // The Next screen will come in form Right and current Screen will go OUT from Left
                                mViewHolder.vfGallery.setInAnimation(mContext, R.anim.in_from_right);
                                mViewHolder.vfGallery.setOutAnimation(mContext, R.anim.out_to_left);
                                // Show The Previous Screen
                                mViewHolder.vfGallery.showPrevious();
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
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView view = (ImageView) inflater.inflate(R.layout.item_gallery, null);
        Glide.with(mContext).load(file.getUrl()).into(view);
        return view;
    }

    private  ImageView createPlaceholder() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView view = (ImageView) inflater.inflate(R.layout.item_gallery_placeholder, null);
        return view;
    }
}
