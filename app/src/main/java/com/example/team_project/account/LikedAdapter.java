package com.example.team_project.account;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.team_project.R;
import com.example.team_project.details.DetailsActivity;
import java.util.ArrayList;
import java.util.List;

public class LikedAdapter extends RecyclerView.Adapter<LikedAdapter.ViewHolder> {

    private final List<String> list;
    private final List<String> distances;
    private final List<String> ids;
    private final List<String> address;

    public LikedAdapter(ArrayList<String> list, ArrayList<String> distances, ArrayList<String> ids, ArrayList<String> address) {
        this.list = list;
        this.distances = distances;
        this.ids = ids;
        this.address = address;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvName;
        private TextView tvDistance;

        public ViewHolder(@NonNull View view) {
            super(view);

            tvName = (TextView) view.findViewById(R.id.tvName);
            tvDistance = (TextView) view.findViewById(R.id.tvDistance);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                Intent intent = new Intent(v.getContext(), DetailsActivity.class);
                intent.putExtra("eventID", ids.get(position));
                intent.putExtra("type", (ids.get(position).substring(0, 1).equals("C")));
                intent.putExtra("distance", distances.get(position));
                v.getContext().startActivity(intent);
            }
        }
    }

    @NonNull
    @Override
    public LikedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_result, parent, false);
        return new LikedAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LikedAdapter.ViewHolder holder, int position) {
        holder.tvName.setText(list.get(position));
        holder.tvDistance.setText(address.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
