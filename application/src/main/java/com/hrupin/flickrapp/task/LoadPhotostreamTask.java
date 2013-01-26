package com.hrupin.flickrapp.task;

import java.util.HashSet;
import java.util.Set;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.GridView;
import android.widget.ListView;

import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.oauth.OAuth;
import com.gmail.yuyang226.flickr.oauth.OAuthToken;
import com.gmail.yuyang226.flickr.people.User;
import com.gmail.yuyang226.flickr.photos.PhotoList;
import com.hrupin.flickrapp.FlickrHelper;
import com.hrupin.flickrapp.adapters.GridThumbsAdapter;

public class LoadPhotostreamTask extends AsyncTask<OAuth, Void, PhotoList> {

    private Activity activity;
    private GridView gridView;

    public LoadPhotostreamTask(Activity activity, GridView gridView) {
        this.activity = activity;
        this.gridView = gridView;
    }

    @Override
    protected PhotoList doInBackground(OAuth... arg0) {
        OAuthToken token = arg0[0].getToken();
        Flickr f = FlickrHelper.getInstance().getFlickrAuthed(token.getOauthToken(), token.getOauthTokenSecret());
        Set<String> extras = new HashSet<String>();
        extras.add("url_sq"); //$NON-NLS-1$
        extras.add("url_l"); //$NON-NLS-1$
        extras.add("views"); //$NON-NLS-1$
        User user = arg0[0].getUser();
        try {
            return f.getPeopleInterface().getPhotos(user.getId(), extras, 20, 1);
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
        if (result != null) {
            GridThumbsAdapter adapter = new GridThumbsAdapter(this.activity, result);
            this.gridView.setAdapter(adapter);
        }
    }

}