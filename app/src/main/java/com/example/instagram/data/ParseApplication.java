package com.example.instagram.data;

import android.app.Application;

import com.example.instagram.R;
import com.example.instagram.models.Post;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    // initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        // register Post class
        ParseObject.registerSubclass(Post.class);

        // use keys to initialize Parse here
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());
    }
}
