package com.hrupin.flickrapp;

import android.app.Application;

public class App extends Application {

    private static final String TAG = App.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        UserPreferences.init(getApplicationContext());
    }

}
