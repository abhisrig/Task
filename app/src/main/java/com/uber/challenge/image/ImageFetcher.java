package com.uber.challenge.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Process;

import com.uber.challenge.image.cache.Cache;
import com.uber.challenge.image.cache.DiskBasedCache;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Helper class used by ImageLoader, which is responsible for initializing the framework and request processing.
 * All the image related works are delegated to this class by ImageLoader
 */
public class ImageFetcher {
    public interface ImageLoadCallback {
        void onImageLoaded(Bitmap bitmap);

        void onError(Exception e);
    }

    private static final String DEFAULT_CACHE_DIR_IMG = "com/uber/challenge/img";
    private static final int DEFAULT_DISK_CACHE_SIZE = 5 * 1024 * 1024;
    private Map<String, ImageRequest> requestMap;
    private BlockingQueue<ImageRequest> mRequestQueue;
    private BlockingQueue<ImageRequest> mCacheQueue;
    private List<ImageRequestThread> mRequestThread;
    private Cache mCache;

    // access should be package default so that other callers may not have access to it
    /*public*/ ImageFetcher(Context context, int numOfThreads) {
        File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR_IMG);
        mCache = new DiskBasedCache(cacheDir, DEFAULT_DISK_CACHE_SIZE);
        requestMap = new HashMap<>();
        mRequestQueue = new PriorityBlockingQueue<>();
        mCacheQueue = new PriorityBlockingQueue<>();
        mRequestThread = new ArrayList<>(numOfThreads);
        CacheRequestThread cacheRequestThread = new CacheRequestThread(mCache, mCacheQueue, mRequestQueue);
        cacheRequestThread.start();
        for (int i = 0; i < numOfThreads; i++) {
            ImageRequestThread imageRequestThread = new ImageRequestThread(mRequestQueue, mCache);
            imageRequestThread.setPriority(Process.THREAD_PRIORITY_BACKGROUND);
            imageRequestThread.setName("IRT#" + i);
            imageRequestThread.start();
            mRequestThread.add(imageRequestThread);
        }
    }

    public void getImageRequest(final String imageUrl, ImageLoadCallback callback) {
        ImageRequest onGoingRequest = requestMap.get(imageUrl);
        if (onGoingRequest != null) { // we already have an image request going for the provided url,
            // add the listener so that even the new caller gets notified once the data has been fetched
            onGoingRequest.addCallback(callback);
        } else {
            ImageRequest imageRequest = new ImageRequest(imageUrl, callback);
            imageRequest.addCallback(new ImageLoadCallback() {
                @Override
                public void onImageLoaded(Bitmap bitmap) {
                    requestMap.remove(imageUrl);
                }

                @Override
                public void onError(Exception e) {
                    requestMap.remove(imageUrl);
                }
            });
            requestMap.put(imageUrl, imageRequest);
            mCacheQueue.add(imageRequest);
        }
    }

    public void cancel(String url) {

    }
}
