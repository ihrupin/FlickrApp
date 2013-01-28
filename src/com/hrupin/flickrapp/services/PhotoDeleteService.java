package com.hrupin.flickrapp.services;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;

import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.oauth.OAuth;
import com.gmail.yuyang226.flickr.oauth.OAuthToken;
import com.google.gson.Gson;
import com.hrupin.flickrapp.FlickrHelper;
import com.hrupin.flickrapp.development.Logger;

public class PhotoDeleteService extends IntentService {

    private static final String TAG = PhotoDeleteService.class.getSimpleName();
    private int result = Activity.RESULT_CANCELED;

    public PhotoDeleteService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        OAuth oAuth = null;
        String photoId = null;
        try {
            oAuth = new Gson().fromJson(intent.getStringExtra(IntentKeys.OAUTH), OAuth.class);
            photoId = intent.getStringExtra(IntentKeys.PHOTO_ID);
        } catch (Exception e) {

        }
        if (oAuth != null & photoId != null) {
            OAuthToken token = oAuth.getToken();
            Flickr f = FlickrHelper.getInstance().getFlickrAuthed(token.getOauthToken(), token.getOauthTokenSecret());
            try {
                f.getPhotosInterface().delete(photoId);
                result = Activity.RESULT_OK;
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        Bundle extras = intent.getExtras();
        if (extras != null) {
            Messenger messenger = (Messenger) extras.get(IntentKeys.MESSENGER);
            Message msg = Message.obtain();
            msg.arg1 = result;

            try {
                messenger.send(msg);
            } catch (android.os.RemoteException e1) {
                Logger.e(TAG, "Exception sending message", e1);
            }

        }
    }
}
