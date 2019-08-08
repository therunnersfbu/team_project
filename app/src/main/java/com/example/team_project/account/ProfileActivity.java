package com.example.team_project.account;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.team_project.BottomNavActivity;
import com.example.team_project.Constants;
import com.example.team_project.PublicVariables;
import com.example.team_project.R;
import com.example.team_project.SurveyActivity;
import com.example.team_project.api.DirectionsApi;
import com.example.team_project.model.PlaceEvent;
import com.example.team_project.model.User;
import com.example.team_project.utils.BitmapScaler;
import com.google.j2objc.annotations.Weak;
import com.nex3z.flowlayout.FlowLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileActivity extends AppCompatActivity implements DirectionsApi.GetDistances {

    private final int YOUR_SELECT_PICTURE_REQUEST_CODE = 150;

    private ArrayList<String> liked;
    private ArrayList<String> distances;
    private ArrayList<String> ids;
    private ArrayList<String> address;
    private RecyclerView.LayoutManager likedManager;
    private LikedAdapter likedAdapter;
    private ParseUser user;
    private Uri outputFileUri;
    private String photoPath;
    private boolean isProfile;

    private WeakReference<DirectionsApi.GetDistances> mGetDistances;

    @BindView(R.id.rvLiked) RecyclerView rvLiked;
    @BindView(R.id.flSurvey) FlowLayout flSurvey;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.tvUsername) TextView tvUsername;
    @BindView(R.id.ivProfilePic) ImageView ivProfilePic;
    @BindView(R.id.ivHeaderImage) ImageView ivHeaderImage;
    @BindDrawable(R.drawable.default_profile_pic) Drawable defaultPic;
    @BindDrawable(R.drawable.header_default) Drawable defaultHeader;
    @BindDrawable(R.drawable.ic_add_black_24dp) Drawable surveyAdd;

    @OnClick(R.id.ivProfilePic)
    public void changeProfilePic(ImageView view) {
        isProfile = true;
        askFilePermission();
    }

    @OnClick(R.id.ivHeaderImage)
    public void changeHeaderImage(ImageView view) {
        isProfile = false;
        askFilePermission();
    }

    @OnClick(R.id.btnLogout)
    public void logout(Button button) {
        ParseUser.logOut();
        setResult(RESULT_OK, new Intent());
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        mGetDistances = new WeakReference<>((DirectionsApi.GetDistances) this);

        liked = new ArrayList<>();
        distances = new ArrayList<>();
        ids = new ArrayList<>();
        address = new ArrayList<>();
        likedManager = new LinearLayoutManager(this);
        likedAdapter = new LikedAdapter(liked, distances, ids, address, this);

        rvLiked.setLayoutManager(likedManager);
        rvLiked.setAdapter(likedAdapter);
        user = ParseUser.getCurrentUser();
        tvName.setText(user.getString(User.KEY_NAME));
        tvUsername.setText(user.getUsername());

        ParseFile imageFile = user.getParseFile(User.KEY_PROFILE_PIC);
        ParseFile headerFile = user.getParseFile(User.KEY_HEADER_IMAGE);
        if (imageFile != null) {
            Glide.with(this)
                    .load(imageFile.getUrl())
                    .placeholder(defaultPic)
                    .error(defaultPic)
                    .into(ivProfilePic);
        } else {
            Glide.with(this)
                    .load(defaultPic)
                    .placeholder(defaultPic)
                    .error(defaultPic)
                    .into(ivProfilePic);
        }

        if (headerFile != null) {
            Glide.with(this)
                    .load(headerFile.getUrl())
                    .placeholder(defaultHeader)
                    .error(defaultHeader)
                    .into(ivHeaderImage);
        } else {
            Glide.with(this)
                    .load(defaultHeader)
                    .placeholder(defaultHeader)
                    .error(defaultHeader)
                    .into(ivHeaderImage);
        }


        getLiked();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED, new Intent());
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        flSurvey.removeAllViews();
        addSurveyButton();
        addCurrentInterests();
    }

    private void addCurrentInterests() {
        ParseUser user = ParseUser.getCurrentUser();
        ArrayList<Boolean> tags = (ArrayList<Boolean>) user.get(User.KEY_TAGS);
        ArrayList<Boolean> categories = (ArrayList<Boolean>) user.get(User.KEY_CATEGORIES);

        if (categories.get(0)) {
            flSurvey.addView(createSurveyItem(PublicVariables.SURVEY_ITEMS[0]));
        }
        if (categories.get(4)) {
            flSurvey.addView(createSurveyItem(PublicVariables.SURVEY_ITEMS[1]));
        }
        if (categories.get(5)) {
            flSurvey.addView(createSurveyItem(PublicVariables.SURVEY_ITEMS[2]));
        }
        if (categories.get(6)) {
            flSurvey.addView(createSurveyItem(PublicVariables.SURVEY_ITEMS[3]));
        }
        if (categories.get(7)) {
            flSurvey.addView(createSurveyItem(PublicVariables.SURVEY_ITEMS[4]));
        }
        if (categories.get(8)) {
            flSurvey.addView(createSurveyItem(PublicVariables.SURVEY_ITEMS[5]));
        }
        if (categories.get(9)) {
            flSurvey.addView(createSurveyItem(PublicVariables.SURVEY_ITEMS[6]));
        }
        if (categories.get(10)) {
            flSurvey.addView(createSurveyItem(PublicVariables.SURVEY_ITEMS[7]));
        }
        if (categories.get(11)) {
            flSurvey.addView(createSurveyItem(PublicVariables.SURVEY_ITEMS[8]));
        }
        if (tags.get(2)) {
            flSurvey.addView(createSurveyItem(PublicVariables.SURVEY_ITEMS[9]));
        }
        if (tags.get(8)) {
            flSurvey.addView(createSurveyItem(PublicVariables.SURVEY_ITEMS[10]));
        }
        if (tags.get(9)) {
            flSurvey.addView(createSurveyItem(PublicVariables.SURVEY_ITEMS[11]));
        }
        if (tags.get(18)) {
            flSurvey.addView(createSurveyItem(PublicVariables.SURVEY_ITEMS[12]));
        }
    }

    private void addSurveyButton() {
        LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_interest_add, null);
        ((ImageView) view.findViewById(R.id.ivAdd)).setImageDrawable(surveyAdd);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(ProfileActivity.this, SurveyActivity.class);
                intent.putExtra("retaking", true);
                startActivity(intent);
            }
        });
        flSurvey.addView(view);
    }

    private View createSurveyItem(String name) {
        LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_survey_profile_page, null);
        TextView tvName = view.findViewById(R.id.tvName);
        tvName.setText(name);
        return view;
    }

    private void getLiked() {
        DirectionsApi api = new DirectionsApi(mGetDistances.get());
        api.setOrigin(BottomNavActivity.currentLat, BottomNavActivity.currentLng);
        ArrayList<String> likedParse = (ArrayList<String>) user.get(User.KEY_LIKED_EVENTS);

        for (String i : likedParse) {
            try {
                String[] spot = i.split(Constants.splitindicator);
                ids.add(spot[0]);
                liked.add(spot[1]);
                address.add(spot[2]);
                ParseQuery query = new ParseQuery("PlaceEvent");
                query.whereContains(PlaceEvent.KEY_API, spot[0]);
                String coords = ((PlaceEvent) query.getFirst()).getCoordinates().replace(" ", ",");
                api.addDestination(coords);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        api.getDistance();
    }

    private void openImageIntent() {
        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Pictures" + File.separator);
        root.mkdirs();
        final String fname = "img_"+ System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));

        startActivityForResult(chooserIntent, YOUR_SELECT_PICTURE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == YOUR_SELECT_PICTURE_REQUEST_CODE) {
                final boolean isCamera;
                if (data == null || data.getData() == null) {
                    isCamera = true;
                } else {
                    isCamera = MediaStore.ACTION_IMAGE_CAPTURE.equals(data.getAction());
                }
                Uri selectedImageUri;
                if (isCamera) {
                    selectedImageUri = outputFileUri;
                    photoPath = selectedImageUri.getPath();
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                    photoPath = getRealPathFromURI_API19(this, selectedImageUri);
                }

                File file = new File(photoPath);
                if (isProfile) {
                    Glide.with(this)
                            .load(file)
                            .placeholder(defaultPic)
                            .error(defaultPic)
                            .into(ivProfilePic);
                } else {
                    Glide.with(this)
                            .load(file)
                            .placeholder(defaultHeader)
                            .error(defaultHeader)
                            .into(ivHeaderImage);
                }

                savePicture();
            }
        }
    }

    @SuppressLint("NewApi")
    private String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);

        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ id }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    private void askFilePermission() {
        ActivityCompat.requestPermissions(ProfileActivity.this,
        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageIntent();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(ProfileActivity.this, "Permission denied to read your External storage",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void savePicture() {
        ParseUser user = ParseUser.getCurrentUser();
        File file = new File(photoPath);
        ParseFile parseFile = new ParseFile(file);
        if (isProfile) {
            user.put(User.KEY_PROFILE_PIC, parseFile);
        } else {
            user.put(User.KEY_HEADER_IMAGE, parseFile);
        }

        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(ProfileActivity.this, "Picture changed!", Toast.LENGTH_SHORT).show();
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void gotDistances(ArrayList<String> distancesApi) {
        distances.addAll(distancesApi);
        likedAdapter.notifyDataSetChanged();
    }
}
