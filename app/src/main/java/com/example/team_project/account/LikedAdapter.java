package com.example.team_project.account;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.team_project.Constants;
import com.example.team_project.R;
import com.example.team_project.details.DetailsActivity;
import com.example.team_project.model.User;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

public class LikedAdapter extends RecyclerView.Adapter<LikedAdapter.ViewHolder> {

    private List<String> names;
    private List<String> distances;
    private List<String> ids;
    private List<String> address;
    private Context context;

    public LikedAdapter(ArrayList<String> names, ArrayList<String> distances, ArrayList<String> ids, ArrayList<String> address, Context context) {
        this.names = names;
        this.distances = distances;
        this.ids = ids;
        this.address = address;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.tvDistance) TextView tvDistance;
        @BindView(R.id.ivSpotImage) ImageView ivSpotImage;

        @OnClick(R.id.ivLike)
        @Optional
        public void unlike(ImageView view) {
            if (context != null) {
                new AlertDialog.Builder(context)
                    .setTitle("Unlike spot")
                    .setMessage("Are you sure you want to unlike this spot?")

                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ParseUser user = ParseUser.getCurrentUser();
                            ArrayList<String> liked = (ArrayList<String>) user.get(User.KEY_LIKED_EVENTS);
                            String likeId = ids.get(getAdapterPosition());
                            String likeName = names.get(getAdapterPosition());
                            for (int i = 0; i < liked.size(); i++) {
                                String[] temp = liked.get(i).split(Constants.splitindicator);
                                if (temp[0].equals(likeId) && temp[1].equals(likeName)) {
                                    liked.remove(i);
                                    names.remove(getAdapterPosition());
                                    distances.remove(getAdapterPosition());
                                    ids.remove(getAdapterPosition());
                                    address.remove(getAdapterPosition());
                                    notifyItemRemoved(getAdapterPosition());

                                }
                            }
                            user.put(User.KEY_LIKED_EVENTS, liked);
                            user.saveInBackground();
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.no, null)
                    .show();
            }
        }

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
                intent.putExtra("eventID", ids.get(position));
                intent.putExtra("type", (ids.get(position).substring(0, 1).equals("C")));
                intent.putExtra("distance", distances.get(position));
                v.getContext().startActivity(intent);
            }
        }

        private void bind() {
            tvName.setText(names.get(getAdapterPosition()));
            tvDistance.setText(address.get(getAdapterPosition()));
            ivSpotImage.setImageResource(R.drawable.logo_no_background);

        }
    }

    @NonNull
    @Override
    public LikedAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemView;
        if (context != null) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_liked, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_other_user, parent, false);
        }
        return new LikedAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LikedAdapter.ViewHolder holder, int position) {
        holder.bind();
    }

    @Override
    public int getItemCount() {
        return names.size();
    }
}
