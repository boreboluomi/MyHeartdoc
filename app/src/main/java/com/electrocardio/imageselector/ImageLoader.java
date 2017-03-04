package com.electrocardio.imageselector;

import android.content.Context;
import android.widget.ImageView;

import java.io.Serializable;

/**
 * ImageLoader
 * Created by yangzheng on 2016/05/10.
 */
public interface ImageLoader extends Serializable {
    void displayImage(Context context, String path, ImageView imageView);
}