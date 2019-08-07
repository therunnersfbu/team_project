package com.example.team_project;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.team_project.model.User;
import com.nex3z.flowlayout.FlowLayout;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SurveyActivity extends AppCompatActivity {
    @BindView(R.id.flSurvey) FlowLayout flSurvey;
    @BindView(R.id.tvGreeting) TextView tvGreeting;
    @BindView(R.id.btnSignUp) Button btnSignUp;

    @OnClick(R.id.btnSignUp)
    public void signupBK(Button button) {
        boolean selectedOne = false;
        for (int i = 0; i < categories.size(); i++) {
            if (categories.get(i)) {
                selectedOne = true;
                break;
            }
        }
        if (!selectedOne) {
            for (int i = 0; i < tags.size(); i++) {
                if (tags.get(i)) {
                    selectedOne = true;
                    break;
                }
            }
        }
        if (selectedOne) {
            if (retaking) {
                retake();
            } else {
                signup();
            }
        } else {
            Toast.makeText(this, "Please select at least one item.", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<View> items;
    private ArrayList<Boolean> itemsSelected;
    private ArrayList<Boolean> categories;
    private ArrayList<Boolean> tags;
    private String username;
    private String name;
    private String email;
    private String password;
    private boolean retaking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        ButterKnife.bind(this);

        retaking = getIntent().getBooleanExtra("retaking", false);
        if (!retaking) {
            username = getIntent().getStringExtra("username");
            name = getIntent().getStringExtra("name");
            email = getIntent().getStringExtra("email");
            password = getIntent().getStringExtra("password");
        } else {
            btnSignUp.setText("Update");
        }

        items = new ArrayList<>();
        itemsSelected = new ArrayList<>();
        categories = new ArrayList<>();
        tags = new ArrayList<>();

        for (int i = 0; i < 13; i++) {
            itemsSelected.add(false);
        }
        for (int i = 0; i < 12; i++) {
            categories.add(false);
        }
        for (int i = 0; i < 20; i++) {
            tags.add(false);
        }
        for (String i : PublicVariables.SURVEY_ITEMS) {
            View view = createSurveyItem(i);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickOnItem(v);
                }
            });
            items.add(view);
            flSurvey.addView(view);
        }
        if (retaking) {
            setRetaking();
        }
    }

    private void setRetaking() {
        ParseUser user = ParseUser.getCurrentUser();
        ArrayList<Boolean> tags = (ArrayList<Boolean>) user.get(User.KEY_TAGS);
        ArrayList<Boolean> categories = (ArrayList<Boolean>) user.get(User.KEY_CATEGORIES);
        if (categories.get(0)) {
            clickOnItem(items.get(0));
        }
        if (categories.get(4)) {
            clickOnItem(items.get(1));
        }
        if (categories.get(5)) {
            clickOnItem(items.get(2));
        }
        if (categories.get(6)) {
            clickOnItem(items.get(3));
        }
        if (categories.get(7)) {
            clickOnItem(items.get(4));
        }
        if (categories.get(8)) {
            clickOnItem(items.get(5));
        }
        if (categories.get(9)) {
            clickOnItem(items.get(6));
        }
        if (categories.get(10)) {
            clickOnItem(items.get(7));
        }
        if (categories.get(11)) {
            clickOnItem(items.get(8));
        }
        if (tags.get(2)) {
            clickOnItem(items.get(9));
        }
        if (tags.get(8)) {
            clickOnItem(items.get(10));
        }
        if (tags.get(9)) {
            clickOnItem(items.get(11));
        }
        if (tags.get(18)) {
            clickOnItem(items.get(12));
        }
    }

    private void clickOnItem(View view) {
        int index = items.indexOf(view);

        /*
        index:                  categories:                 tags:
        0 - restaurant          0 - breakfast               0 - TrendyCity verified
        1 - sightseeing         1 - brunch                  1 - bottomless
        2 - nightlife           2 - lunch                   2 - upscale
        3 - shopping            3 - dinner                  3 - young
        4 - concerts            4 - sights                  4 - dress cute
        5 - fairs               5 - nightlife               5 - rooftop
        6 - beauty              6 - shopping                6 - dress comfy
        7 - working out         7 - concerts                7 - insta-worthy
        8 - parks               8 - pop-up events           8 - outdoors
        9 - upscale             9 - beauty                  9 - indoors
        10 - outdoors           10 - active                 10 - clubby
        11 - indoors            11 - parks                  11 - mall
        12 - family friendly                                12 - food available
                                                            13 - barber
                                                            14 - spa
                                                            15 - classes
                                                            16 - trails
                                                            17 - gyms
                                                            18 - family friendly
                                                            19 - museums
        */

        switch (index) {
            case 0:
                setColor(index);
                setCategories(0, 1, 2, 3);
                setTags();
                break;
            case 1:
                setColor(index);
                setCategories(4);
                setTags();
                break;
            case 2:
                setColor(index);
                setCategories(5);
                setTags();
                break;
            case 3:
                setColor(index);
                setCategories(6);
                setTags();
                break;
            case 4:
                setColor(index);
                setCategories(7);
                setTags();
                break;
            case 5:
                setColor(index);
                setCategories(8);
                setTags();
                break;
            case 6:
                setColor(index);
                setCategories(9);
                setTags();
                break;
            case 7:
                setColor(index);
                setCategories(10);
                setTags();
                break;
            case 8:
                setColor(index);
                setCategories(11);
                setTags();
                break;
            case 9:
                setColor(index);
                setCategories();
                setTags(2);
                break;
            case 10:
                setColor(index);
                setCategories();
                setTags(8);
                break;
            case 11:
                setColor(index);
                setCategories();
                setTags(9);
                break;
            case 12:
                setColor(index);
                setCategories();
                setTags(18);
                break;
            default:
                break;
        }
    }

    private void setColor(int index) {
        itemsSelected.set(index, !itemsSelected.get(index));
        CardView card = (CardView) items.get(index);
        if (itemsSelected.get(index)) {
            card.setCardBackgroundColor(getResources().getColor(R.color.tagSelected));
        } else {
            card.setCardBackgroundColor(getResources().getColor(R.color.tagNotSelected));
        }
    }

    private void setCategories(int... cattegList) {
        for (int i : cattegList) {
            categories.set(i, !categories.get(i));
        }
    }

    private void setTags(int... tagList) {
        for (int i : tagList) {
            tags.set(i, !tags.get(i));
        }
    }

    private View createSurveyItem(String name) {
        LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_survey, null);
        TextView tvName = view.findViewById(R.id.tvName);
        tvName.setText(name);
        return view;
    }

    // User Signup
    private void signup() {
        // create user to save with all properties
        ParseUser user = new ParseUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.put(User.KEY_NAME, name);
        user.put(User.KEY_CATEGORIES, categories);
        user.put(User.KEY_TAGS, tags);
        user.put(User.KEY_VERIFIED, false);
        user.put(User.KEY_ADDED_EVENTS, new ArrayList<String>());
        user.put(User.KEY_LIKED_EVENTS, new ArrayList<String>());
        user.put(User.KEY_TOAST, true);

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("SurveyActivity", "Sign up successful");
                    setResult(RESULT_OK, new Intent());
                    finish();
                } else {
                    Log.e("SurveyActivity", "Sign Up failure");
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void retake() {
        ParseUser user = ParseUser.getCurrentUser();
        user.put(User.KEY_CATEGORIES, categories);
        user.put(User.KEY_TAGS, tags);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    finish();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }
}
