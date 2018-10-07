package com.uber.challenge.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.UiThread;
import android.widget.ImageView;

import com.uber.challenge.R;

public class ImageLoader {

    private static volatile ImageFetcher imageFetcher;


    /*
        method should be called to setup the image loading framework.
        threadCount : Number of concurrent network image request to be carried out
     */
    @UiThread
    public static void initialize(Context context, int threadCount) {
        imageFetcher = new ImageFetcher(context, threadCount);
    }

    /*
     * url : network url to fetch
     * imageView : fetched url bitmap data to be attached to
     * */
    public static ImageTarget<ImageView> with(final ImageView imageView, final String url) {
        if (imageFetcher == null) {
            throw new IllegalStateException("Make sure to initialize ImageLoader before using it");
        }
        final ImageTarget<ImageView> imageTarget = new ImageTarget<>(imageView, url);
        imageFetcher.getImageRequest(url, new ImageFetcher.ImageLoadCallback() {
            @Override
            public void onImageLoaded(Bitmap bitmap) {
                ImageView view = imageTarget.getAssociatedTarget();
                if (view != null && view.getTag(R.id.image_tag).equals(url)) {
                    view.setImageBitmap(bitmap);
                }
            }

            @Override
            public void onError(Exception e) {
                if (imageTarget.getErrorDrawableId() != -1) {
                    ImageView view = imageTarget.getAssociatedTarget();
                    if (view != null) {
                        view.setImageResource(imageTarget.getErrorDrawableId());
                    }
                }
            }
        });
        return imageTarget;
    }
}
