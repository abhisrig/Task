package com.uber.challenge.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


final class ImageRequest implements Comparable<ImageRequest> {
    private String mUrl;
    private int priority = 0;
    private List<ImageFetcher.ImageLoadCallback> mDeliveryCallbacks;
    private static final Object sLock = new Object();
    private Handler mHandler = new Handler(Looper.getMainLooper());

    protected ImageRequest(String url, ImageFetcher.ImageLoadCallback callback) {
        mUrl = url;
        mDeliveryCallbacks = new ArrayList<>(4);
        mDeliveryCallbacks.add(callback);
    }

    protected void addCallback(ImageFetcher.ImageLoadCallback callback) {
        mDeliveryCallbacks.add(callback);
    }

    protected void execute() {
        InputStream stream = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(mUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(30_000);
            connection.setConnectTimeout(30_000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }
            stream = connection.getInputStream();
            Bitmap bitmap = getBitmap(stream);
            deliverResult(bitmap, null);
        } catch (ProtocolException e) {
            deliverResult(null, e);
        } catch (MalformedURLException e) {
            deliverResult(null, e);
        } catch (IOException e) {
            deliverResult(null, e);
        } finally {
            // Close Stream and disconnect HTTPS connection.
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void deliverResult(final Bitmap bitmap, final Exception e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                for (ImageFetcher.ImageLoadCallback callback : mDeliveryCallbacks) {
                    if (callback != null) {
                        if (e == null) {
                            callback.onImageLoaded(bitmap);
                        } else {
                            callback.onError(e);
                        }
                    }
                }
            }
        });
    }

    /**
     * The real guts of parseNetworkResponse. Broken out for readability.
     */
    private Bitmap getBitmap(InputStream response) {
        synchronized (sLock) {
            return BitmapFactory.decodeStream(response);
        }
    }

    @Override
    public int compareTo(@NonNull ImageRequest o) {
        return priority - o.priority;
    }
}