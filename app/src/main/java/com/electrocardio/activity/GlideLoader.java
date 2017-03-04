package com.electrocardio.activity;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.electrocardio.R;
import com.electrocardio.imageselector.ImageLoader;

/**
 * GlideLoader
 * Created by Yancy on 2015/12/6.
 */
public class GlideLoader implements ImageLoader {

    @Override
    public void displayImage(Context context, String path, ImageView imageView) {
        Glide.with(context)
                .load(path)
                .placeholder(R.mipmap.imageselector_photo)
                .centerCrop()
                .into(imageView);
    }

}
