package com.hrupin.flickrapp;

import com.gmail.yuyang226.flickr.oauth.OAuth;
import com.gmail.yuyang226.flickr.oauth.OAuthToken;
import com.gmail.yuyang226.flickr.people.User;
import com.hrupin.flickrapp.development.Logger;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class UserPreferences {
    private static final String TAG = UserPreferences.class.getSimpleName();

    public static final String PREFS_NAME = "flickr_app_private_prefs";
    public static final String KEY_OAUTH_TOKEN = "key_oauth_token";
    public static final String KEY_TOKEN_SECRET = "key_token_secret";
    public static final String KEY_USER_NAME = "key_user_name";
    public static final String KEY_USER_ID = "key_user_id";

    private static SharedPreferences settings = null;

    public static void init(Context context) {
        settings = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Logger.i(TAG, "###################### PREFS ####################");
        Logger.i(TAG, getDumpSharedPrefs());
        Logger.i(TAG, "###################### /PREFS ####################");
    }

    private static String getDumpSharedPrefs() {
        String str = settings.getAll().toString();
        return str;
    }

    public static boolean isLogedIn() {
        boolean result = false;
        OAuth oAuth = getOAuthToken();
        if (oAuth != null) {
            if (oAuth.getUser() != null) {
                result = true;
            }
        }
        return result;
    }

    public static void saveOAuthToken(String userName, String userId, String token, String tokenSecret) {
        Logger.d(TAG, "Saving userName=%s, userId=%s, oauth token={}, and token secret={}", userName, userId, token, tokenSecret); //$NON-NLS-1$
        Editor editor = settings.edit();
        editor.putString(KEY_OAUTH_TOKEN, token);
        editor.putString(KEY_TOKEN_SECRET, tokenSecret);
        editor.putString(KEY_USER_NAME, userName);
        editor.putString(KEY_USER_ID, userId);
        editor.commit();
    }

    public static OAuth getOAuthToken() {
        String oauthTokenString = settings.getString(KEY_OAUTH_TOKEN, null);
        String tokenSecret = settings.getString(KEY_TOKEN_SECRET, null);
        if (oauthTokenString == null && tokenSecret == null) {
            Logger.e(TAG, "No oauth token retrieved");
            return null;
        }
        OAuth oauth = new OAuth();
        String userName = settings.getString(KEY_USER_NAME, null);
        String userId = settings.getString(KEY_USER_ID, null);
        if (userId != null) {
            User user = new User();
            user.setUsername(userName);
            user.setId(userId);
            oauth.setUser(user);
        }
        OAuthToken oauthToken = new OAuthToken();
        oauth.setToken(oauthToken);
        oauthToken.setOauthToken(oauthTokenString);
        oauthToken.setOauthTokenSecret(tokenSecret);
        Logger.d(TAG, "Retrieved token from preference store: oauth token={}, and token secret={}", oauthTokenString, tokenSecret); //$NON-NLS-1$
        return oauth;
    }

}
