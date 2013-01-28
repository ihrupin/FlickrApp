package com.hrupin.flickrapp;

import java.io.File;

import android.content.Context;
import android.os.Environment;

public class Config {
    public static final String FICKR_API_KEY = "44c3bde12a9e3ccbc1810fe1241b5f91";
    public static final String FICKR_API_SECRET = "699edf26609dc724";
    public static final boolean IS_UNDER_DEVELOPMENT = true;
    private static final String PHOTOS = "photos";

    public static File getFilePathOnExternalStorage(Context context, String fileName) {
        File root = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            root = new File(context.getExternalFilesDir(null), PHOTOS);
        } else {
            root = new File(context.getDir(PHOTOS, Context.MODE_PRIVATE), PHOTOS);
        }
        if (root != null) {
            root.mkdirs();
        }
        String mFilePath = root.getPath() + "/" + fileName;
        File f = new File(mFilePath);
        return f;
    }
}
