/**
 * 
 */
package com.hrupin.flickrapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.gmail.yuyang226.flickr.photos.Photo;
import com.gmail.yuyang226.flickr.photos.PhotoList;
import com.hrupin.flickrapp.R;
import com.hrupin.flickrapp.images.ImageUtils.DownloadedDrawable;
import com.hrupin.flickrapp.task.ImageDownloadTask;

public class GridThumbsAdapter extends BaseAdapter {

    private Activity activity;
    private PhotoList photos;
    private int thumbHeight;
    private static LayoutInflater inflater = null;

    public GridThumbsAdapter(Activity a, PhotoList d) {
        activity = a;
        photos = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int screenWidth = a.getResources().getDisplayMetrics().widthPixels;
        thumbHeight = (screenWidth - (6 * activity.getResources().getDimensionPixelSize(R.dimen.gridCellSpacingAndPadding))) / 3;
    }

    public int getCount() {
        return photos.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null) {
            vi = inflater.inflate(R.layout.item_grid_galery_thumb, null);
        }

        ImageView image = (ImageView) vi.findViewById(R.id.imageViewThumb);
        Photo photo = photos.get(position);
        if (image != null) {
            ImageDownloadTask task = new ImageDownloadTask(image);
            Drawable drawable = new DownloadedDrawable(task);
            image.setImageDrawable(drawable);
            task.execute(photo.getSmallSquareUrl());
        }
        vi.setLayoutParams(new GridView.LayoutParams(thumbHeight, thumbHeight));
        vi.setTag(photo);
        return vi;
    }
}
