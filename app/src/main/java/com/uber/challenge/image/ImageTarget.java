package com.uber.challenge.image;

import android.view.View;

import com.uber.challenge.R;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

public class ImageTarget<T extends View> {

    private Reference<T> reference;
    private int defaultDrawableId = -1;
    private int errorDrawableId = -1;

    public ImageTarget(T view, String url) {
        view.setTag(R.id.image_tag, url);
        reference = new WeakReference<>(view);
    }

    public T getAssociatedTarget() {
        return reference != null ? reference.get() : null;
    }

    public ImageTarget<T> setDefaultDrawableId(int defaultDrawableId) {
        this.defaultDrawableId = defaultDrawableId;
        return this;
    }

    public ImageTarget<T> setErrorDrawable(int errorDrawableId) {
        this.errorDrawableId = errorDrawableId;
        return this;
    }

    public int getDefaultDrawableId() {
        return defaultDrawableId;
    }

    public int getErrorDrawableId() {
        return errorDrawableId;
    }
}
