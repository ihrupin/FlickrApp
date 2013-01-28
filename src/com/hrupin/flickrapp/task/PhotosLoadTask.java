package com.hrupin.flickrapp.task;

import java.util.HashSet;
import java.util.Set;

import android.os.AsyncTask;

import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.oauth.OAuth;
import com.gmail.yuyang226.flickr.oauth.OAuthToken;
import com.gmail.yuyang226.flickr.people.User;
import com.gmail.yuyang226.flickr.photos.PhotoList;
import com.hrupin.flickrapp.FlickrHelper;
/**this Task need for loading photostream from Flickr. Now this task not used*/
@Deprecated
public class PhotosLoadTask extends AsyncTask<OAuth, Void, PhotoList> {

    private LoadListener listener;

    public PhotosLoadTask(LoadListener listener) {
        this.listener = listener;
    }

    @Override
    protected PhotoList doInBackground(OAuth... arg0) {
        OAuthToken token = arg0[0].getToken();
        Flickr f = FlickrHelper.getInstance().getFlickrAuthed(token.getOauthToken(), token.getOauthTokenSecret());
        Set<String> extras = new HashSet<String>();
        extras.add("url_sq");
        extras.add("url_l");
        extras.add("views"); 
        extras.add("geo"); 
        User user = arg0[0].getUser();
        try {
            return f.getPeopleInterface().getPhotos(user.getId(), extras, 40, 1);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
     */
    @Override
    protected void onPostExecute(PhotoList result) {
        if (listener != null)
            if (result != null) {
                listener.onComplete(result);
            }
    }

    public static interface LoadListener {
        void onComplete(PhotoList result);
    }

}