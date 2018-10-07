package com.uber.challenge.image.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.util.AttributeSet;

import com.uber.challenge.R;
import com.uber.challenge.image.ImageLoader;

/*
 * Widget to display images from server
 * Clients needs to call bind image with given url to bind images
 * */
public class ServerImage extends android.support.v7.widget.AppCompatImageView {
    private int mDefaultResId = -1;
    private int mErrorResId = -1;

    public ServerImage(Context context) {
        super(context);
    }

    public ServerImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ServerImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ServerImage, 0, 0);
        mDefaultResId = attributes.getResourceId(R.styleable.ServerImage_defaultDrawableId, R.drawable.ic_loading);
        mErrorResId = attributes.getResourceId(R.styleable.ServerImage_errorDrawableId, R.drawable.ic_error);
        attributes.recycle();
    }

    @UiThread
    public void bindImage(final String url) {
        if (mDefaultResId != -1) {
            setImageResource(mDefaultResId);
        }
        ImageLoader.with(this, url).setErrorDrawable(mErrorResId != -1 ? mErrorResId : R.drawable.ic_error);
    }
}