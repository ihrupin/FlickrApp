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
    public static final String CALLBACK_SCHEME = "flickr_app_oauth";

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
                Toast.makeText(this, "Authorization failed", Toast.LENGTH_LONG).show();
                return;
            }
            String message = String.format(Locale.US, "Authorization Succeed: user=%s, userId=%s, oauthToken=%s, tokenSecret=%s", //$NON-NLS-1$
                    user.getUsername(), user.getId(), token.getOauthToken(), token.getOauthTokenSecret());
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            UserPreferences.saveOAuthToken(user.getUsername(), user.getId(), token.getOauthToken(), token.getOauthTokenSecret());
            load(oAuth);
        } else {
            Toast.makeText(this, "Please, login first", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void load(OAuth oauth) {
        if (oauth != null) {
            // new LoadUserTask(this, userIcon).execute(oauth);
            // new LoadPhotostreamTask(this, listView).execute(oauth);
            Toast.makeText(this, "OK", Toast.LENGTH_LONG).show();
        }
    }
}