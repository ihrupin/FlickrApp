package com.hrupin.flickrapp.development;

import java.util.Locale;

import android.util.Log;

import com.hrupin.flickrapp.Config;

public class Logger {

    private static final String firstPartOfTag = "";

    public static void i(String tag, String msg) {
        if (Config.IS_UNDER_DEVELOPMENT) {
            Log.i(firstPartOfTag + tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (Config.IS_UNDER_DEVELOPMENT) {
            Log.e(firstPartOfTag + tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable th) {
        if (Config.IS_UNDER_DEVELOPMENT) {
            Log.e(firstPartOfTag + tag, msg, th);
        }
    }

    public static void d(String tag, String msg) {
        if (Config.IS_UNDER_DEVELOPMENT) {
            Log.d(firstPartOfTag + tag, msg);
        }
    }

    public static void d(String tag, String msg, Object... args) {
        if (Config.IS_UNDER_DEVELOPMENT) {
            Log.d(firstPartOfTag + tag, String.format(Locale.US, msg, args));
        }
    }

}
