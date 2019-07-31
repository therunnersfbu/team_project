package com.example.team_project.details;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
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
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// adapter for the item layouts used on the events details page. Includes header and item views that inflate in a RecyclerView
//TODO: mXXX
//TODO: folder for events, maps, etc (each main feature should have own folder)
public class EventsDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = EventsDetailsAdapter.class.getSimpleName();
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    //TODO private static string tags for eventsID etc
    private final String separator = "()";
    private List<Post> mPosts;
    private String id;
    private boolean type;
    private String distance;
    private String coords;
    private Event mEvent;
    private Place mPlace;
    private DatePickerDialog picker;
    private HeaderViewHolder test;
    private ItemViewHolder testP;
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

        private TextView tvEventName;
        private TextView tvDistance;
        private TextView tvVenue;
        private TextView tvDate;
        private TextView tvAddress;
        private TextView tvNumber;
        private TextView tvPrice;
        private TextView tvHours;
        private Button btnReview;
        private ImageView ivAdd;
        private ImageView ivLike;
        private ViewFlipper vfGallery;

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
            ivAdd = (ImageView) view.findViewById(R.id.ivAdd);
            ivLike = (ImageView) view.findViewById(R.id.ivLike);
            vfGallery = (ViewFlipper) view.findViewById(R.id.vfGallery);

            //TODO enum place / event
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
                    String location;
                    if(type) {
                        location = mPlace.getLocation();
                        intent.putExtra("eventName", mPlace.getPlaceName());
                    } else {
                        location = mEvent.getLocation();
                        intent.putExtra("eventName", mEvent.getEventName());
                    }
                    intent.putExtra("location", location);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvName;
        private ImageView ivProfilePic;
        private TextView tvBody;
        private boolean expanded;

        public ItemViewHolder(@NonNull View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            ivProfilePic = (ImageView) view.findViewById(R.id.ivProfilePic);
            tvBody = (TextView) view.findViewById(R.id.tvBody);
            expanded = false;

//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                ((ViewGroup) view.findViewById(R.id.clReview)).getLayoutTransition()
//                        .enableTransitionType(LayoutTransition.CHANGING);
//            }

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
                        .placeholder(R.drawable.ic_person_black_24dp)
                        .error(R.drawable.ic_person_black_24dp)
                        .into(ivProfilePic);
            } else {
                Glide.with(context)
                        .load(R.drawable.ic_person_black_24dp)
                        .placeholder(R.drawable.ic_person_black_24dp)
                        .error(R.drawable.ic_person_black_24dp)
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
        if (holder instanceof ItemViewHolder) {
            ((ItemViewHolder) holder).bind(mPosts.get(position));
        }

    }

    private Post getPost(int position) {
        return mPosts.get(position);
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

    public void finishedApi(final Event event) {
        test.tvEventName.setText(event.getEventName());
        test.tvDistance.setText(distance);
        test.tvAddress.setText(event.getAddress());
        test.tvDate.setText(event.getStartTime());
        test.tvVenue.setText(event.getVenueName());
        coords = event.getLocation();
        mEvent = event;
        ParseUser user = ParseUser.getCurrentUser();
        ArrayList<String> liked = (ArrayList<String>) user.get(User.KEY_LIKED_EVENTS);
        String toLike = event.getEventId() + separator + event.getEventName() + separator + event.getAddress();
        if (liked.contains(toLike)) {
            test.ivLike.setActivated(true);
        }
        test.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPlaceEventExists(event.getEventName());
                ParseUser user = ParseUser.getCurrentUser();
                ArrayList<String> added = (ArrayList<String>) user.get(User.KEY_ADDED_EVENTS);
                String eventToAdd = event.getStartTime().substring(0, 10) + separator + event.getEventId() + separator + event.getEventName() + separator + event.getAddress();
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

        test.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPlaceEventExists(event.getEventName());
                v.setActivated(!v.isActivated());
                ParseUser user = ParseUser.getCurrentUser();
                ArrayList<String> liked = (ArrayList<String>) user.get(User.KEY_LIKED_EVENTS);
                String toLike = event.getEventId() + separator + event.getEventName() + separator + event.getAddress();
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

    public void finishedApiPlace(final Place place) {
        test.tvEventName.setText(place.getPlaceName());
        test.tvDistance.setText(distance);
        test.tvAddress.setText(place.getAddress());
        test.tvHours.setText(place.getOpenHours().get(0));
        test.tvNumber.setText(place.getPhoneNumber());
        test.tvPrice.setText(place.getPrice());
        coords = place.getLocation();
        mPlace = place;
        ParseUser user = ParseUser.getCurrentUser();
        ArrayList<String> liked = (ArrayList<String>) user.get(User.KEY_LIKED_EVENTS);
        String toLike = place.getPlaceId() + separator + place.getPlaceName() + separator + place.getAddress();
        if (liked.contains(toLike)) {
            test.ivLike.setActivated(true);
        }
        test.ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPlaceEventExists(place.getPlaceName());
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
                                        + separator + place.getPlaceId() + separator + place.getPlaceName() + separator + place.getAddress();
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

        test.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPlaceEventExists(place.getPlaceName());
                v.setActivated(!v.isActivated());
                ParseUser user = ParseUser.getCurrentUser();
                ArrayList<String> liked = (ArrayList<String>) user.get(User.KEY_LIKED_EVENTS);
                String toLike = place.getPlaceId() + separator + place.getPlaceName() + separator + place.getAddress();
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
                test.vfGallery.addView(createGalleryItem(file));
            }
        }

        if (count == 0) {
            test.vfGallery.addView(createPlaceholder());
        } else if (count > 1) {
            test.vfGallery.setOnTouchListener(new View.OnTouchListener() {
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
                                if (test.vfGallery.getDisplayedChild() == 0)
                                    break;

                                // set the required Animation type to ViewFlipper
                                // The Next screen will come in form Left and current Screen will go OUT from Right
                                test.vfGallery.setInAnimation(context, R.anim.in_from_left);
                                test.vfGallery.setOutAnimation(context, R.anim.out_to_right);
                                // Show the next Screen
                                test.vfGallery.showNext();
                            }

                            // if right to left swipe on screen
                            if (lastX > currentX) {
                                if (test.vfGallery.getDisplayedChild() == 1)
                                    break;
                                // set the required Animation type to ViewFlipper
                                // The Next screen will come in form Right and current Screen will go OUT from Left
                                test.vfGallery.setInAnimation(context, R.anim.in_from_right);
                                test.vfGallery.setOutAnimation(context, R.anim.out_to_left);
                                // Show The Previous Screen
                                test.vfGallery.showPrevious();
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
        Glide.with(context)
                .load(file.getUrl())
                .into(view);
        return view;
    }

    private  TextView createPlaceholder() {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return (TextView) inflater.inflate(R.layout.item_gallery_placeholder, null);
    }
}
