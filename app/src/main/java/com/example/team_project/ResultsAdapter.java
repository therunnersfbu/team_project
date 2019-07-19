package com.example.team_project;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {

    private final List<String> list;

    public ResultsAdapter(ArrayList<String> list) {
        this.list = list;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;

        public ViewHolder(@NonNull View view) {
            super(view);

            tvName = (TextView) view.findViewById(R.id.tvName);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
        return new ResultsAdapter.ViewHolder(itemView);
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
