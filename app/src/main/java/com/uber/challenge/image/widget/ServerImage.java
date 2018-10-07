package com.uber.challenge.image.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

import com.uber.challenge.R;
import com.uber.challenge.image.ImageFetcher;
import com.uber.challenge.image.ImageLoader;

public class ServerImage extends android.support.v7.widget.AppCompatImageView {
    public ServerImage(Context context) {
        super(context);
    }

    public ServerImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ServerImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void bindImage(final String url) {
        ImageLoader.with(this, url);
    }

    public void setErrorImage() {
    }
}