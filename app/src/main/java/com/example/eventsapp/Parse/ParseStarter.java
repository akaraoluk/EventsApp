package com.example.eventsapp.Parse;

import android.app.Application;

import com.parse.Parse;

public class ParseStarter extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("SzODb9rcEop2j7DG6ZLVzNAF6h32Qsry9LjOBNXs")
                .clientKey("WPMOps0Ig1fcvEw3YiIvjiF97bcS5EInDlRADASC")
                .server("https://parseapi.back4app.com/")
                .build());

    }
}
