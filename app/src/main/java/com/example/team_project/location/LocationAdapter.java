package com.example.team_project.location;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.team_project.R;

import java.util.ArrayList;
import java.util.List;

//Updates the list of location options from the API JSON results after each search
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> {

    private final List<String> mLocs;
    private final List<String> mNames;
    private final List<String> mIds;
    private final int mCategory;
    public static String mLocName;
    public static String mNewLoc;
    public static boolean isCurLoc;


    public LocationAdapter(ArrayList<String> mLocs, ArrayList<String> mNames, ArrayList<String> ids, int category) {
        this.mLocs = mLocs;
        this.mNames = mNames;
        this.mIds = ids;
        this.mCategory = category;
        isCurLoc = true;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvName;

        public ViewHolder(@NonNull View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.tvName);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(position != RecyclerView.NO_POSITION) {
                mLocName = mNames.get(position);
                mNewLoc = mLocs.get(position);
                isCurLoc = false;
                LocationActivity.locationActivity.finish();
                /*Intent intent = new Intent(v.getContext(), SearchActivity.class);
                intent.putExtra("newLocation", mLocs.get(position));
                intent.putExtra("isCurLoc", false);
                intent.putExtra("category", category);
                intent.putExtra("name", names.get(position));
                v.getContext().startActivity(intent);*/
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_location,viewGroup,false);
        return new LocationAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.tvName.setText(mNames.get(i));

    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

}
