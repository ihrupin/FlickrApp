package com.hrupin.flickrapp.services;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;

import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.oauth.OAuth;
import com.gmail.yuyang226.flickr.oauth.OAuthToken;
import com.gmail.yuyang226.flickr.people.User;
import com.gmail.yuyang226.flickr.photos.PhotoList;
import com.google.gson.Gson;
import com.hrupin.flickrapp.FlickrHelper;
import com.hrupin.flickrapp.development.Logger;

public class PhotosLoadService extends IntentService {

    private static final String TAG = PhotosLoadService.class.getSimpleName();
    private int result = Activity.RESULT_CANCELED;

    public PhotosLoadService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PhotoList list = null;
        OAuth oAuth = null;
        try {
            oAuth = new Gson().fromJson(intent.getStringExtra(IntentKeys.OAUTH), OAuth.class);
        } catch (Exception e) {

        }
        if (oAuth != null) {
            OAuthToken token = oAuth.getToken();
            Flickr f = FlickrHelper.getInstance().getFlickrAuthed(token.getOauthToken(), token.getOauthTokenSecret());
            Set<String> extras = new HashSet<String>();
            extras.add("url_sq");
            extras.add("url_l");
            extras.add("views");
            extras.add("geo");
            User user = oAuth.getUser();
            try {
                list = f.getPeopleInterface().getPhotos(user.getId(), extras, 40, 1);
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
            msg.obj = list;

            try {
                messenger.send(msg);
            } catch (android.os.RemoteException e1) {
                Logger.e(TAG, "Exception sending message", e1);
            }

        }
    }
}
