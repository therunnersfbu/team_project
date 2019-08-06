package com.example.team_project.search;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.team_project.R;
import com.example.team_project.details.DetailsActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

// adapter for spots results after user search
public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {
    private final boolean isPlace;
    private static String ID_TAG= "eventID";
    private static String IS_PLACE_TAG= "type";
    private static String DISTANCE_TAG= "distance";
    private final List<String> mResultsList;
    private final List<String> mDistances;
    private final List<String> mIds;
    private final List<String> mAddresses;

    public ResultsAdapter(ArrayList<String> resultsList, ArrayList<String> distances, ArrayList<String> ids, boolean isPlace,  ArrayList<String> addresses) {
        this.isPlace = isPlace;
        this.mResultsList = resultsList;
        this.mDistances = distances;
        this.mIds = ids;
        this.mAddresses = addresses;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.tvDistance) TextView tvDistance;
        @BindView(R.id.tvAddress) TextView tvAddress;
        @BindView(R.id.ivSpotImage) ImageView ivSpotImage;

        public ViewHolder(@NonNull View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(v.getContext(), DetailsActivity.class);
                intent.putExtra(ID_TAG, mIds.get(position));
                intent.putExtra(IS_PLACE_TAG, isPlace);
                intent.putExtra(DISTANCE_TAG, mDistances.get(position));
                v.getContext().startActivity(intent);
            }
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
        holder.tvName.setText(mResultsList.get(position));
        holder.tvDistance.setText(mDistances.get(position));
        holder.tvAddress.setText(mAddresses.get(position));
        if (isPlace){
            holder.ivSpotImage.setImageResource(R.drawable.sky);
        }else{
            holder.ivSpotImage.setImageResource(R.drawable.event);
        }
    }

    @Override
    public int getItemCount() {
        return mResultsList.size();
    }
}
