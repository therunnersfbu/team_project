package com.example.team_project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
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
import com.example.team_project.model.Post;
import com.nex3z.flowlayout.FlowLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.concurrent.DelayQueue;

public class ComposeReviewActivity extends AppCompatActivity{

    private TextView tvHeader;
    private EditText etBody;
    private Button btnPhoto;
    private Button btnCancel;
    private Button btnPost;
    private FlowLayout flTags;
    private Switch sLocal;
    private String id;
    private String name;
    private static final String EVENT_ID = "eventID";
    private static final String NAME = "eventName";
    public final String APP_TAG = "CamActivity";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    public File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_review);
        tvHeader = findViewById(R.id.tvHeader);
        etBody = findViewById(R.id.etBody);
        btnPhoto = findViewById(R.id.btnPhoto);
        btnCancel = findViewById(R.id.btnCancel);
        btnPost = findViewById(R.id.btnPost);
        flTags = findViewById(R.id.flTags);
        sLocal = findViewById(R.id.sLocal);
        id = getIntent().getStringExtra(EVENT_ID);
        name = getIntent().getStringExtra(NAME);
        tvHeader.setText(name);
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLaunchCamera();
            }
        });
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etBody.getText().length()>1) {
                    savePost(etBody.getText().toString(), ParseUser.getCurrentUser(), photoFile, id);
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
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                ImageView ivPreview = (ImageView) findViewById(R.id.ivPreview);
                ivPreview.setImageBitmap(takenImage);
                btnPhoto.setText("Replace Photo");
                ParseFile file = new ParseFile(photoFile);
                btnPost.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        savePost(etBody.getText().toString(), ParseUser.getCurrentUser(), photoFile, id);
                    }
                });
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void savePost(String body, ParseUser user, File photoFile, final String id)    {
        Post myPost = new Post();
        myPost.setUser(user);
        myPost.setImage(new ParseFile(photoFile));
        myPost.setReview(body);
        myPost.setId(id);
        myPost.setIsLocal(sLocal.isChecked());
        // TODO myPost.setIsLocal(false);
        myPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null) {
                    Log.d("CameraActivity", "post successful");
                    finish();
                } else {
                    Log.d("CameraActivity", "post unsuccessful");
                    e.printStackTrace();
                }
            }
        });

    }

    public void onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFile = getPhotoFileUri(photoFileName);
        Uri fileProvider = FileProvider.getUriForFile(ComposeReviewActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }

    public static class BitmapScaler {
        // Scale and maintain aspect ratio given a desired width
        // BitmapScaler.scaleToFitWidth(bitmap, 100);
        public static Bitmap scaleToFitWidth(Bitmap b, int width)
        {
            float factor = width / (float) b.getWidth();
            return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), true);
        }
    }
}
