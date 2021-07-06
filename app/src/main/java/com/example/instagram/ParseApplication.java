package com.example.instagram;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    // Initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("PYFROCn15mGqHRreU9NkLUKukYnjRpS6PMfg4mlQ")
                .clientKey("0HGx2zbey3T3EtnsfgqbGG6F64nj53UOZuOqYfso")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
