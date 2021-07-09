package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.instagram.adapters.PostsAdapter;
import com.example.instagram.fragments.FeedFragment;
import com.example.instagram.fragments.PostFragment;
import com.example.instagram.models.Post;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    // constants
    private static final String TAG = "MainActivity";

    public MainActivity() {
        super(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set feed fragment
        FeedFragment feedFragment = FeedFragment.newInstance();
        setFeedFragment(feedFragment);

        // set bottom navigation
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        setFeedFragment(feedFragment);
                        break;
                    case R.id.action_post:
                        // set post fragment
                        PostFragment postFragment = PostFragment.newInstance();
                        setPostFragment(postFragment);
                        break;
                    case R.id.action_profile:
                        ParseUser.logOut();
                        Log.i(TAG, "Logged out user");
                        Intent i = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(i);
                        break;
                }
                return true;
            }
        });
    }

    private void setFeedFragment(FeedFragment currFragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container_view, currFragment);
        ft.commit();
    }

    private void setPostFragment(PostFragment currFragment) {
        FragmentTransaction ft2 = getSupportFragmentManager().beginTransaction();
        ft2.replace(R.id.fragment_container_view, currFragment);
        ft2.commit();
    }
}