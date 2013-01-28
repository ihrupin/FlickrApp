package com.hrupin.flickrapp.images;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.http.AndroidHttpClient;
import android.util.Log;

import com.hrupin.flickrapp.Config;
import com.hrupin.flickrapp.development.Logger;
import com.hrupin.flickrapp.task.ImageDownloadTask;

public final class ImageUtils {

    private static final String TAG = ImageUtils.class.getSimpleName();

    public static Bitmap downloadImage(Context context, String url) {
        Bitmap bitmap = downloadImageFromExternalStorage(context, url);
        if (bitmap == null) {
            bitmap = downloadImageFromWeb(context, url);
        }
        return bitmap;
    }

    private static void saveImageToExternalStorage(final Context context, final Bitmap bitmap, final String url) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String fileName = prepareFileName(url);
                try {
                    File file = Config.getFilePathOnExternalStorage(context, fileName);
                    FileOutputStream stream = new FileOutputStream(file);
                    bitmap.compress(CompressFormat.JPEG, 100, stream);
                    stream.flush();
                    stream.close();
                    Logger.i(TAG, "IMAGE saved to filename=" + fileName);
                } catch (IOException e) {
                    Logger.d(TAG, "I/O error while saving bitmap to " + fileName, e);
                } catch (Exception e) {
                    Logger.d(TAG, "Error while saving bitmap to " + fileName, e);
                }
            }
        }).start();
    }

    public static Bitmap downloadImageFromExternalStorage(Context context, String url) {
        String fileName = prepareFileName(url);
        File file = Config.getFilePathOnExternalStorage(context, fileName);
        InputStream inputStream = null;
        try {
            if (file.exists()) {
                inputStream = new FileInputStream(file);
                Logger.i(TAG, "###IMAGE CACHE: " + fileName);
                return BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
            }
        } catch (IOException e) {
            Logger.d(TAG, "I/O error while retrieving bitmap from " + fileName, e);
        } catch (Exception e) {
            Logger.d(TAG, "Error while retrieving bitmap from " + fileName, e);
        }
        return null;
    }

    private static String prepareFileName(String url) {
        String fileName = url.replace(":", "_");
        fileName = fileName.replace("/", "_");
        fileName = fileName.replace(".", "_");
        return fileName;
    }

    public static Bitmap downloadImageFromWeb(Context context, String url) {
        // final int IO_BUFFER_SIZE = 4 * 1024;

        // AndroidHttpClient is not allowed to be used from the main thread
        final HttpClient client = AndroidHttpClient.newInstance("Android");
        final HttpGet getRequest = new HttpGet(url);

        try {
            HttpResponse response = client.execute(getRequest);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + url);
                return null;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    // return BitmapFactory.decodeStream(inputStream);
                    // Bug on slow connections, fixed in future release.
                    Logger.i(TAG, "##IMAGE FROM WEB: " + url);
                    Bitmap bitmat = BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
                    saveImageToExternalStorage(context, bitmat, url);
                    return bitmat;
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (IOException e) {
            getRequest.abort();
            Logger.d(TAG, "I/O error while retrieving bitmap from " + url, e); //$NON-NLS-1$
        } catch (IllegalStateException e) {
            getRequest.abort();
            Logger.d(TAG, "Incorrect URL:" + url, e); //$NON-NLS-1$
        } catch (Exception e) {
            getRequest.abort();
            Logger.d(TAG, "Error while retrieving bitmap from " + url, e); //$NON-NLS-1$
        } finally {
            if ((client instanceof AndroidHttpClient)) {
                ((AndroidHttpClient) client).close();
            }
        }
        return null;
    }

    public static class DownloadedDrawable extends ColorDrawable {

        private WeakReference<ImageDownloadTask> taskRef;

        public DownloadedDrawable(ImageDownloadTask task) {
            taskRef = new WeakReference<ImageDownloadTask>(task);
        }

        public ImageDownloadTask getBitmapDownloaderTask() {
            if (taskRef != null) {
                return taskRef.get();
            } else {
                return null;
            }
        }
    }
}
