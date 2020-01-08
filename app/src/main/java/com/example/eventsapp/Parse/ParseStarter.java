package com.example.eventsapp.Parse;

import android.app.Application;

import com.parse.Parse;

public class ParseStarter extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("lDZptq4XOEb9wrzUP0XPyTdUEc9Qr6wO4JmXAxQC")
                .clientKey("DZXSVWalstDaIVTx5K4Z0i0uYeZG9LHNeEbmoHZH")
                .server("https://parseapi.back4app.com/")
                .build());

    }
}
