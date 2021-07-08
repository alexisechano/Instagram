package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;

import com.example.instagram.adapters.PostsAdapter;
import com.example.instagram.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {
    // view element variables
    private RecyclerView rvPosts;
    private SwipeRefreshLayout swipeContainer;

    protected PostsAdapter adapter;
    protected List<Post> postList;

    // constants
    private static final String TAG = "FeedActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
            }
        });

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // find Recycler View
        rvPosts = findViewById(R.id.rvPosts);

        // initialize the array that will hold posts and create a PostsAdapter
        postList = new ArrayList<>();
        adapter = new PostsAdapter(this, postList);

        // set the adapter on the recycler view
        rvPosts.setAdapter(adapter);

        // set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(this));

        // query posts from Parse database
        queryPosts();
    }

    private ParseQuery<Post> setQuery(){
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);

        // include data referred by user key
        query.include(Post.KEY_USER);

        // limit query to latest 20 items
        query.setLimit(20);

        // order posts by creation date (newest first)
        query.addDescendingOrder("createdAt");

        return query;
    }

    private void fetchTimelineAsync() {
        // Remember to CLEAR OUT old items before appending in the new ones
        adapter.clear();

        // set new list and query
        ParseQuery<Post> query = setQuery();

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                // update recycler view with new objects
                adapter.addAll(objects);
                swipeContainer.setRefreshing(false);
            }
        });
    }

    private void queryPosts() {
        ParseQuery<Post> query = setQuery();

        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }

                // for debugging purposes let's print every post description to logcat
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }

                // save received posts to list and notify adapter of new data
                postList.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }
}