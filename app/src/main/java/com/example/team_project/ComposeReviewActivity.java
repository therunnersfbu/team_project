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
import com.example.team_project.model.Post;
import com.example.team_project.utils.BitmapScaler;
import com.nex3z.flowlayout.FlowLayout;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.DelayQueue;

public class ComposeReviewActivity extends AppCompatActivity{

    private static final String EVENT_ID = "eventID";
    private static final String NAME = "eventName";
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
    public File photoFile;
    private Uri outputFileUri;
    private String photoPath;

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
                askFilePermission();
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

    // result code is 0 not -1
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
                btnPhoto.setText("Replace Photo");
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
