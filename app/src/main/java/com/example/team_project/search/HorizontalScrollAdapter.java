package com.example.team_project.search;

import butterknife.BindView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.team_project.PublicVariables;
import com.example.team_project.R;
import com.example.team_project.details.DetailsActivity;
import com.example.team_project.fragments.EventsFragment;
import com.example.team_project.model.PlaceEvent;
import com.example.team_project.model.Post;
import com.example.team_project.utils.ContextProvider;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;

// This adapter handles horizontal scrolling for the Tag and Suggestion CardViews. Used in Search Activity
public class HorizontalScrollAdapter extends RecyclerView.Adapter<HorizontalScrollAdapter.ViewHolder> {
    private final List<String> mTagsList;
    private final List<String> mIdList;
    private final boolean isTags;
    private static ArrayList<String> mAddTagsToSearch;
    private static String mTagToAdd;
    private static List<Post> mPosts;
    private Context mContext;
    private static String API_ID_KEY = "apiId";
    private static String CLASS_NAME_TAG = "PlaceEvent";

    public HorizontalScrollAdapter(List<String> horizontalList, List<String> idList, boolean isTags, ContextProvider cp) {
        this.mTagsList = horizontalList;
        this.isTags = isTags;
        this.mIdList = idList;
        this.mContext = cp.getContext();
        mAddTagsToSearch = new ArrayList<>();
        mPosts = new ArrayList<>();
        mTagToAdd = "";
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tvName) TextView tvName;

        public ViewHolder(@NonNull final View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
            tvName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    view.callOnClick();
                }
            });
        }

        @Override
        public void onClick(View v) {
            if (isTags) {
                mTagToAdd = tvName.getText().toString();
                if(!mAddTagsToSearch.contains(mTagToAdd)) {
                    v.getBackground().setColorFilter(ContextCompat.getColor(v.getContext(), R.color.filterSelected), PorterDuff.Mode.DARKEN);
                    mAddTagsToSearch.add(mTagToAdd);
                }
                else {
                    v.getBackground().setColorFilter(ContextCompat.getColor(v.getContext(), R.color.filterNotSelected), PorterDuff.Mode.LIGHTEN);
                    mAddTagsToSearch.remove(mTagToAdd);
                }
                notifyDataSetChanged();
                ((SearchActivity) mContext).setNewSearchText(mAddTagsToSearch);
            } else {
                final Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra(DetailsActivity.EVENT_ID, mIdList.get(getAdapterPosition()));
                intent.putExtra(DetailsActivity.DISTANCE, EventsFragment.distances.get(getAdapterPosition()));
                intent.putExtra(DetailsActivity.TYPE, PublicVariables.type);
                mContext.startActivity(intent);
            }
        }
    }

    private PlaceEvent query(String id) {
        ParseQuery<PlaceEvent> query = new ParseQuery(CLASS_NAME_TAG);
        query.whereContains(API_ID_KEY, id);
        PlaceEvent mPlaceEvent = null;
        try {
            mPlaceEvent = (PlaceEvent) query.getFirst();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return mPlaceEvent;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(isTags ? R.layout.item_tag : R.layout.item_suggestion, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(mTagsList.get(position));
        if(!isTags) {
            PlaceEvent mPlaceEvent = query(mIdList.get(position));
            if(mPlaceEvent!=null) {
                mPosts.clear();
                getPosts(mPlaceEvent, holder);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mTagsList.size();
    }

    public void getPosts(final PlaceEvent mPostEvent, final ViewHolder holder) {
        ParseQuery<Post> parseQuery = new ParseQuery(Post.class);
        parseQuery.include(Post.KEY_EVENT_PLACE);
        parseQuery.setLimit(20);
        parseQuery.whereEqualTo(Post.KEY_EVENT_PLACE, mPostEvent);
        parseQuery.findInBackground(new FindCallback<Post>() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    mPosts.addAll(objects);
                    for(int i = 0; i< mPosts.size(); i++) {
                        ParseFile mFile = mPosts.get(i).getImage();
                        if(mFile!=null) {
                            Drawable image = null;
                            try {
                                image = Drawable.createFromPath(mFile.getFile().getPath());
                            } catch (ParseException e1) {
                                e1.printStackTrace();
                            }
                            holder.itemView.findViewById(R.id.cvResult).setBackground(image);
                            mPosts.clear();
                            return;
                        }
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
