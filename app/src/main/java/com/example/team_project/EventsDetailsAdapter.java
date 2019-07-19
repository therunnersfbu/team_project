package com.example.team_project;

import android.content.ClipData;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.team_project.model.Event;
import com.example.team_project.model.Post;

import java.util.List;

public class EventsDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = EventsDetailsAdapter.class.getSimpleName();
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private List<Post> posts;

    public EventsDetailsAdapter(List<Post>posts) {
        this.posts = posts;

    }
    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        public TextView tvEventName;

        public HeaderViewHolder(@NonNull View view) {
            super(view);
            tvEventName = (TextView) view.findViewById(R.id.tvEventName);
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
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_header_details, parent, false);
            return new HeaderViewHolder(layoutView);
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
        if (isPositionHeader(position)) {
            return TYPE_HEADER;
        } else {
            return TYPE_ITEM;
        }
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }
}
