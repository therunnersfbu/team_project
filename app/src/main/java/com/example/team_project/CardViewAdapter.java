package com.example.team_project;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.ViewHolder> {

    private final List<String> list;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;

        public ViewHolder(@NonNull View view) {
            super(view);

            tvName = (TextView) view.findViewById(R.id.tvName);
        }
    }

    public CardViewAdapter(List<String> horizontalList) {
        this.list = horizontalList;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suggestion, parent, false);
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
