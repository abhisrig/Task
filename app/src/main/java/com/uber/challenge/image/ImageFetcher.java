package com.uber.challenge.image;

import android.graphics.Bitmap;
import android.os.Process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

public class ImageFetcher {
    public interface ImageLoadCallback {
        void onImageLoaded(Bitmap bitmap);

        void onError(Exception e);
    }

    private Map<String, ImageRequest> requestMap;
    private BlockingQueue<ImageRequest> mRequestQueue;
    private List<ImageRequestThread> mRequestThread;

    public ImageFetcher(int numOfThreads) {
        requestMap = new HashMap<>();
        mRequestQueue = new PriorityBlockingQueue<>();
        mRequestThread = new ArrayList<>(numOfThreads);
        for (int i = 0; i < numOfThreads; i++) {
            ImageRequestThread imageRequestThread = new ImageRequestThread(mRequestQueue);
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
            mRequestQueue.add(imageRequest);
        }
    }

    public void cancel(String url) {

    }
}
