package com.hrupin.flickrapp;

import javax.xml.parsers.ParserConfigurationException;

import com.gmail.yuyang226.flickr.Flickr;
import com.gmail.yuyang226.flickr.REST;
import com.gmail.yuyang226.flickr.RequestContext;
import com.gmail.yuyang226.flickr.interestingness.InterestingnessInterface;
import com.gmail.yuyang226.flickr.oauth.OAuth;
import com.gmail.yuyang226.flickr.oauth.OAuthToken;
import com.gmail.yuyang226.flickr.photos.PhotosInterface;

public final class FlickrHelper {

    private static FlickrHelper instance = null;

    private static final String API_KEY = "174b6cfbdaccf6886985c08527708d98"; //$NON-NLS-1$
    public static final String API_SEC = "11df5e5bf032b557"; //$NON-NLS-1$

    private FlickrHelper() {

    }

    public static FlickrHelper getInstance() {
        if (instance == null) {
            instance = new FlickrHelper();
        }

        return instance;
    }

    public Flickr getFlickr() {
        try {
            Flickr f = new Flickr(API_KEY, API_SEC, new REST());
            return f;
        } catch (ParserConfigurationException e) {
            return null;
        }
    }

    public Flickr getFlickrAuthed(String token, String secret) {
        Flickr f = getFlickr();
        RequestContext requestContext = RequestContext.getRequestContext();
        OAuth auth = new OAuth();
        auth.setToken(new OAuthToken(token, secret));
        requestContext.setOAuth(auth);
        return f;
    }

    public InterestingnessInterface getInterestingInterface() {
        Flickr f = getFlickr();
        if (f != null) {
            return f.getInterestingnessInterface();
        } else {
            return null;
        }
    }

    public PhotosInterface getPhotosInterface() {
        Flickr f = getFlickr();
        if (f != null) {
            return f.getPhotosInterface();
        } else {
            return null;
        }
    }

}