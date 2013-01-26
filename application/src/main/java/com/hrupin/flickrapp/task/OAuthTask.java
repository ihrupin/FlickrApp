/**
 * 
 */
package com.hrupin.flickrapp.task;

import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.auth.Permission;
import com.gmail.yuyang226.flickr.oauth.OAuthToken;
import com.hrupin.flickrapp.FlickrHelper;
import com.hrupin.flickrapp.UserPreferences;
import com.hrupin.flickrapp.development.Logger;
import com.hrupin.flickrapp.ui.activities.FlickrAppActivity;
import com.hrupin.flickrapp.ui.activities.LoginActivity;

/**
 * Represents the task to start the oauth process.
 * 
 * @author yayu
 * 
 */
public class OAuthTask extends AsyncTask<Void, Integer, String> {

    private static final Uri OAUTH_CALLBACK_URI = Uri.parse(FlickrAppActivity.CALLBACK_SCHEME + "://oauth"); //$NON-NLS-1$

    private static final String TAG = OAuthTask.class.getSimpleName();

    /**
     * The context.
     */
    private Context mContext;

    /**
     * The progress dialog before going to the browser.
     */
    private ProgressDialog mProgressDialog;

    /**
     * Constructor.
     * 
     * @param context
     */
    public OAuthTask(Context context) {
        super();
        this.mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mProgressDialog = ProgressDialog.show(mContext, "", "Generating the authorization request..."); //$NON-NLS-1$ //$NON-NLS-2$
        mProgressDialog.setCanceledOnTouchOutside(true);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dlg) {
                OAuthTask.this.cancel(true);
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected String doInBackground(Void... params) {
        try {
            Flickr f = FlickrHelper.getInstance().getFlickr();
            OAuthToken oauthToken = f.getOAuthInterface().getRequestToken(OAUTH_CALLBACK_URI.toString());
            saveTokenSecrent(oauthToken.getOauthTokenSecret());
            URL oauthUrl = f.getOAuthInterface().buildAuthenticationUrl(Permission.DELETE, oauthToken);
            return oauthUrl.toString();
        } catch (Exception e) {
            Logger.e(TAG, "Error to oauth", e);
            return "error:" + e.getMessage();
        }
    }

    /**
     * Saves the oauth token secrent.
     * 
     * @param tokenSecret
     */
    private void saveTokenSecrent(String tokenSecret) {
        Logger.d(TAG, "request token: " + tokenSecret);
        UserPreferences.saveOAuthToken(null, null, null, tokenSecret);
        Logger.d(TAG, "oauth token secret saved: " + tokenSecret);
    }

    @Override
    protected void onPostExecute(String result) {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        if (result != null && !result.startsWith("error")) { //$NON-NLS-1$
            mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(result)));
        } else {
            Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
        }
    }

}
