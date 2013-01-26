package com.hrupin.flickrapp;

import android.app.Application;

import com.hrupin.flickrapp.development.Logger;

public class App extends Application {

    private static final String TAG = App.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        UserPreferences.init(getApplicationContext());
    }

}
