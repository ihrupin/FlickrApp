package com.hrupin.flickrapp.ui.activities;

import java.util.Locale;

import com.gmail.yuyang226.flickr.oauth.OAuth;
import com.gmail.yuyang226.flickr.oauth.OAuthToken;
import com.gmail.yuyang226.flickr.people.User;
import com.hrupin.flickrapp.R;
import com.hrupin.flickrapp.UserPreferences;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class FlickrAppActivity extends Activity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    protected void onResume() {
        super.onResume();

        successLoginFlow();
    }

    private void successLoginFlow() {
        OAuth oAuth = UserPreferences.getOAuthToken();
        if (oAuth != null) {
            User user = oAuth.getUser();
            OAuthToken token = oAuth.getToken();
            if (user == null || user.getId() == null || token == null || token.getOauthToken() == null || token.getOauthTokenSecret() == null) {
                pleaseLoginFirst();
                return;
            }
            load(oAuth);
        } else {
            pleaseLoginFirst();
        }
    }

    private void pleaseLoginFirst() {
        Toast.makeText(this, "Please, login first", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void load(OAuth oauth) {
        if (oauth != null) {
            // new LoadUserTask(this, userIcon).execute(oauth);
            // new LoadPhotostreamTask(this, listView).execute(oauth);
            Toast.makeText(this, "OK", Toast.LENGTH_LONG).show();
        }
    }
}