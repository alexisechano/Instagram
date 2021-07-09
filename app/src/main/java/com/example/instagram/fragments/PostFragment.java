package com.example.instagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

import static android.app.Activity.RESULT_OK;

public class PostFragment extends Fragment {
    // view element variables
    private Button btnTakePic;
    private Button btnSubmit;
    private EditText etDescription;
    private ImageView ivPostImage;
    private File photoFile;

    // other instance variables for File
    private String photoFileName = "photo.jpg";

    // constants
    private static final String TAG = "PostFragment";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;


    public PostFragment() {
        // Required empty public constructor
    }

    public static PostFragment newInstance() {
        PostFragment fragment = new PostFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View currView = inflater.inflate(R.layout.fragment_post, container, false);

        // match variables to layout id and set click listeners
        setUpView(currView);

        return currView;

    }

    private void setUpView(View currView) {
        // other elements in layout
        btnTakePic = currView.findViewById(R.id.btnTakePic);
        btnSubmit = currView.findViewById(R.id.btnSubmit);
        etDescription = currView.findViewById(R.id.etDescription);
        ivPostImage = currView.findViewById(R.id.ivPostImage);

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
                    Toast.makeText(getActivity(), "Description cannot be empty!", Toast.LENGTH_SHORT).show();
                    return;
                }
                // error check
                if(photoFile == null || ivPostImage.getDrawable() == null){
                    Toast.makeText(getActivity(), "There is no image!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // get current user and use info to save post to database
                ParseUser currUser = ParseUser.getCurrentUser();
                savePost(imgDescription, currUser, photoFile);
            }
        });

    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            // start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // use camera on disk to load into image view
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                ivPostImage.setImageBitmap(takenImage);
            } else {
                Toast.makeText(getActivity(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public File getPhotoFileUri(String fileName) {
        // get safe storage directory for photos
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

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
                    Toast.makeText(getActivity(), "Cannot save post!", Toast.LENGTH_SHORT).show();
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