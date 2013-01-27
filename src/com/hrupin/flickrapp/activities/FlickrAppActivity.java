package com.hrupin.flickrapp.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.gmail.yuyang226.flickr.oauth.OAuth;
import com.gmail.yuyang226.flickr.oauth.OAuthToken;
import com.gmail.yuyang226.flickr.people.User;
import com.gmail.yuyang226.flickr.photos.GeoData;
import com.gmail.yuyang226.flickr.photos.Photo;
import com.gmail.yuyang226.flickr.photos.PhotoList;
import com.hrupin.flickrapp.R;
import com.hrupin.flickrapp.UserPreferences;
import com.hrupin.flickrapp.adapters.GridThumbsAdapter;
import com.hrupin.flickrapp.development.Logger;
import com.hrupin.flickrapp.images.ImageUtils.DownloadedDrawable;
import com.hrupin.flickrapp.task.ImageDeleteTask;
import com.hrupin.flickrapp.task.ImageDeleteTask.DeleteListener;
import com.hrupin.flickrapp.task.ImageDownloadTask;
import com.hrupin.flickrapp.task.LoadPhotostreamTask;
import com.hrupin.flickrapp.task.LoadPhotostreamTask.LoadListener;

public class FlickrAppActivity extends Activity implements OnItemClickListener, OnClickListener {

    private static final String TAG = FlickrAppActivity.class.getSimpleName();
    private static final int DIALOG_WAIT = 0;
    private GridView gridView;
    private FrameLayout frameLayoutFullScreenImageWrapper;
    private ImageView imageViewFullScreen;
    private Button buttonShowOnMap;
    private Button buttonDelete;
    public GridThumbsAdapter adapter;
    private Photo currentPhoto;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        gridView = (GridView) findViewById(R.id.gridViewThumbs);
        frameLayoutFullScreenImageWrapper = (FrameLayout) findViewById(R.id.frameLayoutFullScreenImageWrapper);
        imageViewFullScreen = (ImageView) findViewById(R.id.imageViewFullScreen);
        imageViewFullScreen.setOnClickListener(this);
        buttonShowOnMap = (Button) findViewById(R.id.buttonShowOnMap);
        buttonShowOnMap.setOnClickListener(this);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(this);
        enableFullscreenMode(false);
    }

    private void openPhotoInFullScreen(Photo photo) {
        if (photo != null) {
            this.currentPhoto = photo;
            enableFullscreenMode(true);
            ImageDownloadTask task = new ImageDownloadTask(imageViewFullScreen);
            Drawable drawable = new DownloadedDrawable(task);
            imageViewFullScreen.setImageDrawable(drawable);
            task.execute(photo.getLargeUrl());

            if (photo.getGeoData() != null) {
                GeoData geoData = photo.getGeoData();
                Logger.i(TAG, "geoDate != null" + ", LAT:" + geoData.getLatitude() + ", LNG:" + geoData.getLongitude());
                buttonShowOnMap.setVisibility(View.VISIBLE);

            } else {
                buttonShowOnMap.setVisibility(View.GONE);
                Logger.i(TAG, "geoDate == null");
            }
            buttonDelete.setTag(photo);
        } else {
            this.currentPhoto = null;
            enableFullscreenMode(false);
            imageViewFullScreen.setImageDrawable(null);
        }

    }

    private void enableFullscreenMode(boolean isEnable) {
        if (isEnable) {
            frameLayoutFullScreenImageWrapper.setVisibility(View.VISIBLE);
            gridView.setEnabled(false);
            imageViewFullScreen.setClickable(true);
            buttonShowOnMap.setVisibility(View.VISIBLE);
            buttonDelete.setVisibility(View.VISIBLE);
        } else {
            frameLayoutFullScreenImageWrapper.setVisibility(View.GONE);
            gridView.setEnabled(true);
            imageViewFullScreen.setClickable(false);
            buttonShowOnMap.setVisibility(View.GONE);
            buttonDelete.setVisibility(View.GONE);
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
        Toast.makeText(this, getString(R.string.please_login_first), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void load(OAuth oauth) {
        if (oauth != null) {
            showDialog(DIALOG_WAIT);
            enableFullscreenMode(false);
            new LoadPhotostreamTask(new LoadListenerImpl()).execute(oauth);
        }
    }

    class LoadListenerImpl implements LoadListener {

        @Override
        public void onComplete(PhotoList result) {
            dismissDialog(DIALOG_WAIT);
            adapter = new GridThumbsAdapter(FlickrAppActivity.this, result);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(FlickrAppActivity.this);
        }

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        Photo photo = (Photo) arg1.getTag();
        Logger.i(TAG, "PHOTO:::" + photo.getLargeUrl());
        openPhotoInFullScreen(photo);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (frameLayoutFullScreenImageWrapper.getVisibility() == View.VISIBLE) {
                enableFullscreenMode(false);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == imageViewFullScreen.getId()) {
            if (frameLayoutFullScreenImageWrapper.getVisibility() == View.VISIBLE) {
                enableFullscreenMode(false);
            }
        }
        if (v.getId() == buttonShowOnMap.getId()) {
            if (currentPhoto != null) {
                GeoData geoData = currentPhoto.getGeoData();
                if (geoData != null) {
                    double latitude = geoData.getLatitude();
                    double longitude = geoData.getLongitude();
                    String title = (currentPhoto.getTitle().equals(""))?getString(R.string.photo):currentPhoto.getTitle();
                    String searchStr = "geo:0,0?q=" + latitude + "," + longitude + " (" + title + ")";
                    Logger.i(TAG, searchStr);
                    Uri uri = Uri.parse(searchStr);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            }
        }
        if (v.getId() == buttonDelete.getId()) {
            if (currentPhoto != null) {
                showDialog(DIALOG_WAIT);
                OAuth oAuth = UserPreferences.getOAuthToken();
                if (oAuth != null) {
                    new ImageDeleteTask(currentPhoto.getId(), new DeleteListener() {
                        @Override
                        public void onComplete() {
                            gridView.setAdapter(null);
                            successLoginFlow();
                            dismissDialog(DIALOG_WAIT);
                        }
                    }).execute(oAuth);
                }
            }
        }

    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
        if(id == DIALOG_WAIT){
            ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage(getString(R.string.please_wait));
            return dialog;
        }
        return null;
    }
}