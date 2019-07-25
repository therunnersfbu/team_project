package com.example.team_project;

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
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.team_project.fragments.EventsFragment;
import com.example.team_project.model.PlaceEvent;
import com.example.team_project.model.Post;
import com.example.team_project.utils.BitmapScaler;
import com.nex3z.flowlayout.FlowLayout;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.DelayQueue;

public class ComposeReviewActivity extends AppCompatActivity{

    private static final String EVENT_ID = "eventID";
    private static final String NAME = "eventName";
    private static final int[][] tagsToShow = {
        {2, 4, 6, 7, 18},
        {1, 2, 4, 6, 7, 18},
        {2, 4, 6, 7, 18},
        {2, 4, 6, 7, 18},
        {2, 7, 18, 19},
        {2, 3, 10, 12, 5},
        {2, 11},
        {9, 8, 2, 12, 18},
        {12, 18},
        {13, 14, 18},
        {15, 16, 17},
        {12, 18}};
    private final int YOUR_SELECT_PICTURE_REQUEST_CODE = 150;
    private TextView tvHeader;
    private EditText etBody;
    private Button btnPhoto;
    private Button btnCancel;
    private Button btnPost;
    private FlowLayout flTags;
    private Switch sLocal;
    private String id;
    private String name;
    private PlaceEvent placeEvent;
    private Uri outputFileUri;
    private String photoPath;
    private File photoFile;
    private String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_review);
        photoFile = null;
        tvHeader = findViewById(R.id.tvHeader);
        etBody = findViewById(R.id.etBody);
        btnPhoto = findViewById(R.id.btnPhoto);
        btnCancel = findViewById(R.id.btnCancel);
        btnPost = findViewById(R.id.btnPost);
        flTags = findViewById(R.id.flTags);
        sLocal = findViewById(R.id.sLocal);
        id = getIntent().getStringExtra(EVENT_ID);
        name = getIntent().getStringExtra(NAME);
        location = getIntent().getStringExtra("location");
        tvHeader.setText(name);
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askFilePermission();
            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etBody.getText().length()>0) {
                    checkPlaceEventExists();
                }
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

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
                Bitmap takenImage = BitmapFactory.decodeFile(photoPath);
                Bitmap resizedBitmap = com.example.team_project.utils.BitmapScaler.scaleToFitWidth(takenImage, 400);
                ImageView ivPreview = (ImageView) findViewById(R.id.ivPreview);
                ivPreview.setImageBitmap(resizedBitmap);
                photoFile = new File(photoPath);
                btnPhoto.setText("Replace Photo");
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkPlaceEventExists() {
        final ArrayList<PlaceEvent> placeEventList = new ArrayList<>();
        ParseQuery parseQuery = new ParseQuery("PlaceEvent");
        parseQuery.setLimit(1000);

        parseQuery.findInBackground(new FindCallback<PlaceEvent>() {
            @Override
            public void done(List<PlaceEvent> objects, ParseException e) {
                if (e == null) {
                    placeEventList.addAll(objects);
                    for (int i = 0; i < placeEventList.size(); i++) {
                        if (id.equals(placeEventList.get(i).getAppId())) {
                            placeEvent = placeEventList.get(i);
                            savePost();
                            return;
                        }
                    }

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
                } else {
                    e.printStackTrace();
                }
            }
        });
    }

    private void savePost() {
        Post myPost = new Post();
        myPost.setUser(ParseUser.getCurrentUser());
        myPost.setImage(new ParseFile(photoFile));
        myPost.setReview(etBody.getText().toString());
        myPost.setEventPlace(placeEvent);
        myPost.setIsLocal(sLocal.isChecked());
        myPost.setCoordinates(location);
        myPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null) {
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

    public static String getTagStr(int i) {
        switch (i) {
            case 0:
        return "TrendyCity verified";
            case 1:
        return "bottomless";
            case 2:
        return "upscale";
            case 3:
        return "young";
            case 4:
        return "dress cute";
            case 5:
        return "rooftop";
            case 6:
        return "dress comfy";
            case 7:
        return "insta-worthy";
            case 8:
        return "outdoors";
            case 9:
        return "indoors";
            case 10:
        return "clubby";
            case 11:
        return "mall";
            case 12:
        return "food available";
            case 13:
        return "barber";
            case 14:
        return "spa";
            case 15:
        return "classes";
            case 16:
        return "trails";
            case 17:
        return "gyms";
            case 18:
        return "family friendly";
            case 19:
        return "museums";
        default:
            return "";
        }
    }
}
