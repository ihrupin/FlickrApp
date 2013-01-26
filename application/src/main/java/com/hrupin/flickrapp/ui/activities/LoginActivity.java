package com.hrupin.flickrapp.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.gmail.yuyang226.flickr.oauth.OAuth;
import com.hrupin.flickrapp.R;
import com.hrupin.flickrapp.UserPreferences;
import com.hrupin.flickrapp.task.OAuthTask;

public class LoginActivity extends Activity implements OnClickListener {
    private Button buttonSignIn;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        buttonSignIn = (Button) findViewById(R.id.buttonSignIn);
        buttonSignIn.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (UserPreferences.isLogedIn()) {
            openApp();
        }
    }

    private void openApp() {
        Intent intent = new Intent(this, FlickrAppActivity.class);
        startActivity(intent);
        finish();
    }

    public void onOAuthDone(OAuth result) {
        if (result == null) {
            Toast.makeText(this, "Authorization failed", Toast.LENGTH_LONG).show();
        } else {
            openApp();
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == buttonSignIn.getId()) {
            OAuthTask task = new OAuthTask(this);
            task.execute();
        }
    }
}