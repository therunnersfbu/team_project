package com.example.team_project.search;

import butterknife.BindView;
import android.support.annotation.NonNull;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.team_project.R;
import com.example.team_project.details.DetailsActivity;
import java.util.ArrayList;
import java.util.List;
import butterknife.ButterKnife;

// adapter for spots results after user search
public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ViewHolder> {
    private final boolean isPlace;
    private final List<String> mTagList;
    private final List<String> mDistances;
    private final List<String> mIds;

    public ResultsAdapter(ArrayList<String> tagList, ArrayList<String> distances, ArrayList<String> ids, boolean isPlace) {
        this.isPlace = isPlace;
        this.mTagList = tagList;
        this.mDistances = distances;
        this.mIds = ids;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.tvDistance) TextView tvDistance;

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
                intent.putExtra("eventID", mIds.get(position));
                intent.putExtra("isPlace", isPlace);
                intent.putExtra("distance", mDistances.get(position));
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
        holder.tvName.setText(mTagList.get(position));
        holder.tvDistance.setText(mDistances.get(position));
    }

    @Override
    public int getItemCount() {
        return mTagList.size();
    }
}
