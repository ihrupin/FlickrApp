package com.hrupin.flickrapp.task;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.GridView;
import android.widget.ImageView;

import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.oauth.OAuth;
import com.gmail.yuyang226.flickr.oauth.OAuthToken;
import com.gmail.yuyang226.flickr.people.User;
import com.gmail.yuyang226.flickr.photos.PhotoList;
import com.hrupin.flickrapp.FlickrHelper;
import com.hrupin.flickrapp.development.Logger;
import com.hrupin.flickrapp.images.ImageCache;
import com.hrupin.flickrapp.images.ImageUtils;
import com.hrupin.flickrapp.images.ImageUtils.DownloadedDrawable;
import com.hrupin.flickrapp.task.LoadPhotostreamTask.LoadListener;

public class ImageDeleteTask extends AsyncTask<OAuth, Void, Boolean> {

    private static final String TAG = ImageDeleteTask.class.getSimpleName();
    private DeleteListener listener;
    private String photoId;

    public ImageDeleteTask(String photoId, DeleteListener listener) {
        this.photoId = photoId;
        this.listener = listener;
    }

    @Override
    protected Boolean doInBackground(OAuth... arg0) {
        Logger.i(TAG, "doInBackground");
        OAuthToken token = arg0[0].getToken();
        Flickr f = FlickrHelper.getInstance().getFlickrAuthed(token.getOauthToken(), token.getOauthTokenSecret());
        try {
            f.getPhotosInterface().delete(photoId);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(Boolean result) {
        Logger.i(TAG, "onPostExecute");
        if (listener != null)
            listener.onComplete();
    }
    public static interface DeleteListener {
        void onComplete();
    }
}
