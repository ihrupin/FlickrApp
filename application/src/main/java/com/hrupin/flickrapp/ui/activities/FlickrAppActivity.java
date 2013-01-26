package com.hrupin.flickrapp.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gmail.yuyang226.flickr.oauth.OAuth;
import com.gmail.yuyang226.flickr.oauth.OAuthToken;
import com.gmail.yuyang226.flickr.people.User;
import com.gmail.yuyang226.flickr.photos.Photo;
import com.gmail.yuyang226.flickr.photos.PhotoList;
import com.hrupin.flickrapp.R;
import com.hrupin.flickrapp.UserPreferences;
import com.hrupin.flickrapp.adapters.GridThumbsAdapter;
import com.hrupin.flickrapp.development.Logger;
import com.hrupin.flickrapp.task.LoadPhotostreamTask;
import com.hrupin.flickrapp.task.LoadPhotostreamTask.LoadListener;

public class FlickrAppActivity extends Activity implements OnItemClickListener {

    private static final String TAG = FlickrAppActivity.class.getSimpleName();
    private GridView gridView;
    private LinearLayout linearLayoutFullScreenImageWrapper;
    private ImageView imageViewFullScreen;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        gridView = (GridView)findViewById(R.id.gridViewThumbs);
        linearLayoutFullScreenImageWrapper = (LinearLayout)findViewById(R.id.linearLayoutFullScreenImageWrapper);
        imageViewFullScreen = (ImageView)findViewById(R.id.imageViewFullScreen);
    }
    
    private void setFillScreen(Photo photo){
        if(photo != null){
            linearLayoutFullScreenImageWrapper.setVisibility(View.VISIBLE);
        }else {
            linearLayoutFullScreenImageWrapper.setVisibility(View.GONE);
            imageViewFullScreen.setImageDrawable(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        successLoginFlow();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        gridView.setAdapter(null);
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
            setFillScreen(null);
            new LoadPhotostreamTask(this, gridView, new LoadListenerImpl()).execute(oauth);
        }
    }
    
    class LoadListenerImpl implements LoadListener{

        @Override
        public void onComplete(PhotoList result) {
            GridThumbsAdapter adapter = new GridThumbsAdapter(FlickrAppActivity.this, result);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(FlickrAppActivity.this);
        }
        
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Photo photo = (Photo)arg1.getTag();
        Logger.i(TAG, "PHOTO:::" + photo.getLargeUrl());
        Toast.makeText(this, photo.getLargeUrl(), Toast.LENGTH_LONG).show();
        
    }
}