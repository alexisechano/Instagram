package com.example.instagram;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instagram.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    // view element variables
    private Button btnLogout; // temp button for logout
    private Button btnTakePic;
    private Button btnSubmit;
    private Button btnFeed;
    private EditText etDescription;
    private ImageView ivPostImage;
    private File photoFile;

    // other instance variables for File
    private String photoFileName = "photo.jpg";

    // constants
    private static final String TAG = "MainActivity";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // match variables to layout id and set click listeners
        setUpView();
    }

    private void setUpView() {
        // basic logout feature
        btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Log.i(TAG, "Logged out user");
                finish();
            }
        });

        // other elements in layout
        btnTakePic = findViewById(R.id.btnTakePic);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnFeed = findViewById(R.id.btnFeed);
        etDescription = findViewById(R.id.etDescription);
        ivPostImage = findViewById(R.id.ivPostImage);

        // set up listener to open camera
        btnTakePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        // set up btn listener to save and upload post
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imgDescription = etDescription.getText().toString();

                if(imgDescription.isEmpty()){
                    Toast.makeText(MainActivity.this, "Description cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // error check
                if(photoFile == null || ivPostImage.getDrawable() == null){
                    Toast.makeText(MainActivity.this, "There is no image!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // get current user and use info to save post to database
                ParseUser currUser = ParseUser.getCurrentUser();
                savePost(imgDescription, currUser, photoFile);
            }
        });

        // set up feed navigator
        btnFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Navigating to Feed");
                Intent i = new Intent(MainActivity.this, FeedActivity.class);
                startActivity(i);
            }
        });
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(MainActivity.this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getPackageManager()) != null) {
            // start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // use camera on disk to load into image view
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                ivPostImage.setImageBitmap(takenImage);
            } else {
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public File getPhotoFileUri(String fileName) {
        // get safe storage directory for photos
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    private void savePost(String description, ParseUser currUser, File photoFile) {
        Post post = new Post();

        // set post fields
        post.setDescription(description);
        post.setImage(new ParseFile(photoFile));
        post.setUser(currUser);

        // save to the database
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(MainActivity.this, "Cannot save post!", Toast.LENGTH_SHORT).show();
                }

                // if no error, let log know
                Log.i(TAG, "Saved post successfully");

                // clear out current data to show user it was successful
                etDescription.setText("");
                ivPostImage.setImageResource(0);
            }
        });
    }
}