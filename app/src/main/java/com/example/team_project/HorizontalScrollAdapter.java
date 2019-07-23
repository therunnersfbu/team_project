package com.example.team_project;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

// This adapter handles horizontal scrolling for the Tag and Suggestion CardViews. Used in Search Activity

public class HorizontalScrollAdapter extends RecyclerView.Adapter<HorizontalScrollAdapter.ViewHolder> {

    private final List<String> list;
    private final boolean isTags;

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;

        public ViewHolder(@NonNull View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
        }
    }

    public HorizontalScrollAdapter(List<String> horizontalList, boolean isTags) {
        this.list = horizontalList;
        this.isTags = isTags;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(isTags? R.layout.item_tag : R.layout.item_suggestion, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(list.get(position));

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
