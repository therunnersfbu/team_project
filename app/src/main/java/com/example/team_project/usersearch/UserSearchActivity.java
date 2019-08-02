package com.example.team_project.usersearch;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.EditText;
import com.example.team_project.R;
import com.example.team_project.model.Post;
import com.example.team_project.model.User;
import com.example.team_project.utils.ContextProvider;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserSearchActivity extends AppCompatActivity {

    private ArrayList<ParseUser> mUserList;
    private UserSearchAdapter mAdapter;

    @BindView(R.id.rvResults) RecyclerView mRvResults;
    @BindView(R.id.etSearchBar) EditText mSearchBar;

    @OnClick(R.id.btnSearch)
    public void search(Button button) {
        String searchText = mSearchBar.getText().toString();
        ParseQuery query = new ParseQuery("User");
        query.setLimit(1000);
        query.whereContains(User.KEY_USERNAME, searchText);

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    mUserList.clear();
                    mUserList.addAll(objects);
                    mAdapter.notifyDataSetChanged();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);
        ButterKnife.bind(this);

        mUserList = new ArrayList<>();
        mAdapter = new UserSearchAdapter(mUserList, new ContextProvider() {
            @Override
            public Context getContext() {
                return UserSearchActivity.this;
            }
        });
        mRvResults.setLayoutManager(new LinearLayoutManager(this));
        mRvResults.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mUserList.clear();
        mAdapter.notifyDataSetChanged();
    }
}
