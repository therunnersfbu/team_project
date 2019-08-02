package com.example.team_project.search;

import butterknife.BindView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.team_project.BottomNavActivity;
import com.example.team_project.R;
import com.example.team_project.details.DetailsActivity;
import com.example.team_project.fragments.EventsFragment;
import com.example.team_project.utils.ContextProvider;

import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;

// This adapter handles horizontal scrolling for the Tag and Suggestion CardViews. Used in Search Activity
public class HorizontalScrollAdapter extends RecyclerView.Adapter<HorizontalScrollAdapter.ViewHolder> {
    private final List<String> mTagsList;
    private final boolean isTags;
    private static ArrayList<String> mAddTagsToSearch;
    private static String mTagToAdd;
    private Context mContext;

    public HorizontalScrollAdapter(List<String> horizontalList, boolean isTags, ContextProvider cp) {
        this.mTagsList = horizontalList;
        this.isTags = isTags;
        this.mContext = cp.getContext();
        mAddTagsToSearch = new ArrayList<>();
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
                    v.getBackground().setColorFilter(Color.parseColor("#6238c9"), PorterDuff.Mode.DARKEN);
                    mAddTagsToSearch.add(mTagToAdd);
                }
                else {
                    v.getBackground().setColorFilter(Color.parseColor("#3e91f7"), PorterDuff.Mode.LIGHTEN);
                    mAddTagsToSearch.remove(mTagToAdd);
                }
                notifyDataSetChanged();
                ((SearchActivity) mContext).setNewSearchText(mAddTagsToSearch);
            } else {
                final Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra(DetailsActivity.EVENT_ID, EventsFragment.idList.get(getAdapterPosition()));
                intent.putExtra(DetailsActivity.DISTANCE, EventsFragment.distances.get(getAdapterPosition()));
                intent.putExtra(DetailsActivity.TYPE, EventsFragment.type);
                mContext.startActivity(intent);
            }
        }
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
    }

    @Override
    public int getItemCount() {
        return mTagsList.size();
    }
}
