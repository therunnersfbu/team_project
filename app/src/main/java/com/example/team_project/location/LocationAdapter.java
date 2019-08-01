package com.example.team_project.location;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.team_project.PublicVariables;
import com.example.team_project.R;
import com.example.team_project.api.PlacesApi;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

//Updates the list of location options from the API JSON results after each search
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ViewHolder> implements PlacesApi.GetLocation  {
    private final List<String> mNames;
    private final List<String> mIds;
    private AdapterCallback mCallback;
    private PlacesApi pApi;

    public LocationAdapter(ArrayList<String> mNames, ArrayList<String> mIds) {
        this.mNames = mNames;
        this.mIds = mIds;
    }

    public void setOnItemClickedListener(AdapterCallback callback) {
        this.mCallback = callback;
    }

    // adapter callback to return to Location Activity on result
    public interface AdapterCallback {
        void onItemClicked();
    }

    //set onclick on each view item
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
                pApi = new PlacesApi(LocationAdapter.this);
                pApi.getLocation(mIds.get(position));
                PublicVariables.newLocName = mNames.get(position);
                PublicVariables.isCurLoc = false;
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

    @Override
    public void gotLocation(String locationApi) {
        PublicVariables.newLoc = locationApi;
        mCallback.onItemClicked();
    }
}
