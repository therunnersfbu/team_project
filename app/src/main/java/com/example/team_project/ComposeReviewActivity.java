package com.example.team_project;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.team_project.fragments.EventsFragment;
import com.example.team_project.model.PlaceEvent;
import com.example.team_project.model.Post;
import com.nex3z.flowlayout.FlowLayout;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;

public class ComposeReviewActivity extends AppCompatActivity{

    private static final String EVENT_ID = "eventID";
    private static final String NAME = "eventName";
    private final int YOUR_SELECT_PICTURE_REQUEST_CODE = 150;

    private String id;
    private String name;
    private PlaceEvent placeEvent;
    private Uri outputFileUri;
    private String photoPath;
    private File photoFile;
    private String location;
    private ArrayList<View> tags;
    private ArrayList<Boolean> tagsSelected;

    @BindView(R.id.ivPreview) ImageView ivPreview;
    @BindView(R.id.tvHeader) TextView tvHeader;
    @BindView(R.id.etBody) EditText etBody;
    @BindView(R.id.flTags) FlowLayout flTags;
    @BindView(R.id.sLocal) Switch sLocal;

    @OnClick(R.id.ivPreview)
    public void changePic(ImageView view) {
        askFilePermission();
    }

    @OnClick(R.id.btnCancel)
    public void cancel(ImageButton button) {
        finish();
    }

    @OnClick(R.id.btnPost)
    public void post(Button button) {
        if(etBody.getText().length()<1) {
            Toast.makeText(ComposeReviewActivity.this, "Can't post an empty review!", Toast.LENGTH_SHORT).show();
        } else if (!tagsSelected.contains(true)) {
            Toast.makeText(ComposeReviewActivity.this, "Select at least one tag!", Toast.LENGTH_SHORT).show();
        } else {
            checkPlaceEventExists();
        }
    }

    @OnTouch(R.id.etBody)
    public boolean focus(EditText et, MotionEvent event) {
        if (etBody.hasFocus()) {
            et.getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction() & MotionEvent.ACTION_MASK){
                case MotionEvent.ACTION_SCROLL:
                    et.getParent().requestDisallowInterceptTouchEvent(false);
                    return true;
            }
        }
        return false;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_review);

        ButterKnife.bind(this);
        photoFile = null;
        tags = new ArrayList<>();
        tagsSelected = new ArrayList<>();

        id = getIntent().getStringExtra(EVENT_ID);
        name = getIntent().getStringExtra(NAME);
        location = getIntent().getStringExtra("location");
        tvHeader.setText(name);

        for (int i = 0; i < 20; i++) {
            tagsSelected.add(false);
        }
        for (int i = 1; i < 20; i++) {
            View view = createReviewItem(PublicVariables.getTagStr(i));
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickTag(v);
                }
            });
            ((CardView) view).setCardBackgroundColor(getResources().getColor(R.color.reviewNot));
            tags.add(view);
            flTags.addView(view);
        }

    }

    private void clickTag(View view) {
        int index = tags.indexOf(view) + 1;
        tagsSelected.set(index, !tagsSelected.get(index));
        setColor(index);
    }

    private void setColor(int index) {
        CardView card = (CardView) tags.get(index - 1);
        if (tagsSelected.get(index)) {
            card.setCardBackgroundColor(getResources().getColor(R.color.appThemeDark));
        } else {
            card.setCardBackgroundColor(getResources().getColor(R.color.reviewNot));
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == YOUR_SELECT_PICTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
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

                ivPreview.setAdjustViewBounds(true);
                ivPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                photoFile = new File(photoPath);

                File file = new File(photoPath);
                Glide.with(this)
                        .load(file)
                        .into(ivPreview);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private View createReviewItem(String name) {
        LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_tag, null);
        TextView tvName = view.findViewById(R.id.tvName);
        tvName.setText(name);
        return view;
    }

    private void checkPlaceEventExists() {
        ParseQuery parseQuery = new ParseQuery("PlaceEvent");
        parseQuery.setLimit(1000);
        parseQuery.whereMatches(PlaceEvent.KEY_API, id);

        try {
            placeEvent = (PlaceEvent) parseQuery.getFirst();
            savePost();
        } catch (ParseException e) {
            placeEvent = new PlaceEvent();
            ArrayList<Boolean> categories = new ArrayList<>();
            ArrayList<Integer> tags = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                categories.add(false);
            }
            for (int i = 0; i < 20; i++) {
                tags.add(0);
            }
            if (EventsFragment.categoryToMark > -1) {
                categories.set(EventsFragment.categoryToMark, true);
            }

            placeEvent.put(PlaceEvent.KEY_API, id);
            placeEvent.put(PlaceEvent.KEY_CATEGORIES, categories);
            placeEvent.put(PlaceEvent.KEY_TAGS, tags);
            placeEvent.setName(name);
            placeEvent.setCoordinates(location);
            placeEvent.setLiked(0);
            placeEvent.setReviewed(0);
            placeEvent.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        savePost();
                    } else {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    private void savePost() {
        Post myPost = new Post();
        myPost.setUser(ParseUser.getCurrentUser());
        if (photoFile != null){
            myPost.setImage(new ParseFile(photoFile));
        }
        myPost.setReview(etBody.getText().toString());
        myPost.setEventPlace(placeEvent);
        myPost.setIsLocal(sLocal.isChecked());
        myPost.setTags(tagsSelected);

        ArrayList<Integer> placeEventTags = placeEvent.getTags();
        for (int i = 0; i < tagsSelected.size(); i++) {
            if (tagsSelected.get(i)) {
                placeEventTags.set(i, placeEventTags.get(i) + 1);
            }
        }
        placeEvent.setTags(placeEventTags);
        placeEvent.setReviewed(placeEvent.getReviewed() + 1);
        placeEvent.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Log.d("PostActivity", "event update successful");
                } else {
                    Log.d("PostActivity", "event update unsuccessful");
                    e.printStackTrace();
                }
            }
        });

        myPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null) {
                    Log.d("PostActivity", "post successful");
                    finish();
                } else {
                    Log.d("PostActivity", "post unsuccessful");
                    e.printStackTrace();
                }
            }
        });

    }

    private void openImageIntent() {
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "Pictures" + File.separator);
        root.mkdirs();
        final String fname = "img_"+ System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        startActivityForResult(chooserIntent, YOUR_SELECT_PICTURE_REQUEST_CODE);
    }

    @SuppressLint("NewApi")
    private String getRealPathFromURI_API19(Context context, Uri uri){
        String filePath = "";
        String wholeID = DocumentsContract.getDocumentId(uri);
        String id = wholeID.split(":")[1];
        String[] column = { MediaStore.Images.Media.DATA };
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
        ActivityCompat.requestPermissions(ComposeReviewActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openImageIntent();

                } else {
                    Toast.makeText(ComposeReviewActivity.this, "Permission denied to read your External storage",
                            Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
