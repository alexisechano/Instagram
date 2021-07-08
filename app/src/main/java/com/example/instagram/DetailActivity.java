package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.instagram.models.Post;
import com.parse.ParseFile;

import org.parceler.Parcels;

public class DetailActivity extends AppCompatActivity {
    // view elements
    private TextView tvUsername;
    private TextView tvDescription;
    private TextView tvTime;
    private ImageView ivImage;

    // Post variable
    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // set the view items
        setUpView();

        // get tweet from intent
        post = (Post) Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));

        // bind all of the items
        bind(post);
    }

    private void setUpView() {
        tvDescription = findViewById(R.id.tvDescription);
        tvUsername = findViewById(R.id.tvUsername);
        tvTime = findViewById(R.id.tvTime);
        ivImage = findViewById(R.id.ivImage);
    }

    private void bind(Post post) {
        // bind the information from parcel
        tvDescription.setText(post.getDescription());
        tvUsername.setText(post.getUser().getUsername());
        tvTime.setText(Post.calculateTimeAgo(post.getCreatedAt()));

        ParseFile image = post.getImage();
        if (image != null) {
            Glide.with(this).load(image.getUrl()).into(ivImage);
        }
    }

}