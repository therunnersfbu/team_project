package com.example.team_project.usersearch;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.team_project.BottomNavActivity;
import com.example.team_project.R;
import com.example.team_project.account.OtherUserActivity;
import com.example.team_project.model.User;
import com.example.team_project.utils.ContextProvider;
import com.parse.ParseFile;
import com.parse.ParseUser;
import java.util.ArrayList;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;

public class UserSearchAdapter extends RecyclerView.Adapter<UserSearchAdapter.ViewHolder> {

    private ArrayList<ParseUser> mUserList;
    private Context mContext;

    public UserSearchAdapter(ArrayList<ParseUser> userList, ContextProvider cp) {
        mUserList = userList;
        mContext = cp.getContext();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_user_search_result, viewGroup, false);
        return new UserSearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.bind();
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ParseUser user;

        @BindView(R.id.ivProfilePic) ImageView ivProfilePic;
        @BindView(R.id.tvName) TextView tvName;
        @BindView(R.id.tvUsername) TextView tvUsername;
        @BindDrawable(R.drawable.default_profile_pic) Drawable defaultPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            BottomNavActivity.targetUser = user;
            final Intent intent = new Intent(mContext, OtherUserActivity.class);
            mContext.startActivity(intent);
        }

        public void bind() {
            user = mUserList.get(getAdapterPosition());

            tvName.setText((String) user.get(User.KEY_NAME));
            tvUsername.setText(user.getUsername());

            ParseFile imageFile = user.getParseFile(User.KEY_PROFILE_PIC);
            if (imageFile != null) {
                Glide.with(mContext)
                        .load(imageFile.getUrl())
                        .placeholder(defaultPic)
                        .error(defaultPic)
                        .into(ivProfilePic);
            } else {
                Glide.with(mContext)
                        .load(defaultPic)
                        .placeholder(defaultPic)
                        .error(defaultPic)
                        .into(ivProfilePic);
            }
        }
    }
}
