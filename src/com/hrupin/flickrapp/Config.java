package com.hrupin.flickrapp;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class Config {
    public static final String FICKR_API_KEY = "44c3bde12a9e3ccbc1810fe1241b5f91";
    public static final String FICKR_API_SECRET = "699edf26609dc724";
    public static final boolean IS_UNDER_DEVELOPMENT = true;
    private static final String PHOTOS = "photos";

    public static File getStoragePath(Context context, String fileName) {
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            file = new File(context.getExternalFilesDir(null), fileName);
        } else {
            file = new File(context.getDir(PHOTOS, Context.MODE_PRIVATE), fileName);
        }
        return file;
    }
}
