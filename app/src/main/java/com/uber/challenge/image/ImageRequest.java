package com.uber.challenge.image;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import com.uber.challenge.image.cache.Cache;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Responsible for executing network request and decoding bitmaps
 */
final class ImageRequest implements Comparable<ImageRequest> {
    private String mUrl;
    private int priority = 0;
    private List<ImageFetcher.ImageLoadCallback> mDeliveryCallbacks;
    private static final Object sLock = new Object();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int completeCacheExpiryTime = 12 * 60;
    private int cacheExpiryTime = 1 * 60;

    protected ImageRequest(String url, ImageFetcher.ImageLoadCallback callback) {
        mUrl = url;
        mDeliveryCallbacks = new ArrayList<>(4);
        mDeliveryCallbacks.add(callback);
    }

    protected void addCallback(ImageFetcher.ImageLoadCallback callback) {
        mDeliveryCallbacks.add(callback);
    }

    public String getUrl() {
        return mUrl;
    }

    protected Cache.Entry execute() {
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

            ResponseData responseData = getResponseData(connection);
            deliverResult(getBitmap(responseData.data), null);
            return parseIgnoreCacheHeader(responseData);
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
        return null;
    }

    protected void deliverFromCache(byte[] data) {
        Bitmap bitmap = getBitmap(data);
        if (bitmap != null) {
            deliverResult(bitmap, null);
        } else {
            deliverResult(null, new Exception("Not found"));
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

    private byte[] getBytes(InputStream in) throws IOException {
        byte[] buffer = new byte[1024];
        int count;
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        while ((count = in.read(buffer)) != -1) {
            bytes.write(buffer, 0, count);
        }
        return bytes.toByteArray();
    }

    private Bitmap getBitmap(byte[] bytes) {
        synchronized (sLock) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
    }

    @Override
    public int compareTo(@NonNull ImageRequest o) {
        return priority - o.priority;
    }

    private ResponseData getResponseData(HttpURLConnection connection) throws IOException {
        Map<String, String> map = new HashMap<>();
        for (Map.Entry<String, List<String>> header : connection.getHeaderFields().entrySet()) {
            if (header.getKey() != null) {
                map.put(header.getKey(), header.getValue().get(0));
            }
        }
        byte[] data = getBytes(connection.getInputStream());
        return new ResponseData(data, map);
    }

    public Cache.Entry parseIgnoreCacheHeader(ResponseData response) {
        long now = System.currentTimeMillis();
        Map<String, String> headers = response.responseHeaderMap;
        long serverDate = 0;
        String serverEtag = null;
        String headerValue;
        serverEtag = headers.get("ETag");
        final long cacheHitButRefreshed = cacheExpiryTime * 60 * 1000; // in 4 hours cache will be hit, but also refreshed on background
        final long cacheExpired = completeCacheExpiryTime * 60 * 1000; // in 24 hours this cache entry expires completely
        final long softExpire = now + cacheHitButRefreshed;
        final long ttl = now + cacheExpired;

        Cache.Entry entry = new Cache.Entry();
        entry.data = response.data;
        entry.etag = serverEtag;
        entry.softTtl = softExpire;
        entry.ttl = ttl;
        entry.serverDate = serverDate;
        entry.responseHeaders = headers;
        return entry;
    }
}