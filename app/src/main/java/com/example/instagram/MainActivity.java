package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    // view element variables
    private Button btnLogout; // temp button for logout
    private Button btnTakePic;
    private Button btnSubmit;
    private EditText etDescription;
    private ImageView ivPostImage;

    // constants
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // match variables to layout id
        setViewElems();

        // get posts from database
        queryPosts();
    }

    private void queryPosts() {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        // get all posts
        query.include(Post.KEY_USER);
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check if null
                if(e != null){
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                // iterate through all of the posts
                for(Post post: posts){
                    Log.i(TAG, "Current post descrip: " + post.getDescription() + ", Username: " + post.getUser().getUsername());
                }
            }
        });
    }

    private void setViewElems() {
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
        etDescription = findViewById(R.id.etDescription);
        ivPostImage = findViewById(R.id.ivPostImage);
    }
}