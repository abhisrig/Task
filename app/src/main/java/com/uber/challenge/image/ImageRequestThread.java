package com.uber.challenge.image;

import android.graphics.Bitmap;

import com.uber.challenge.image.cache.Cache;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.BlockingQueue;

/*
 * Thread for performing network operation on image request queue
 * */
public class ImageRequestThread extends Thread {
    private BlockingQueue<ImageRequest> processingQueue;
    private Cache mCache;

    public ImageRequestThread(BlockingQueue<ImageRequest> queue, Cache cache) {
        processingQueue = queue;
        mCache = cache;
    }

    @Override
    public void run() {
        while (true) {
            if (!processingQueue.isEmpty()) {
                ImageRequest imageRequest = processingQueue.poll();
                if (imageRequest != null) {
                    Cache.Entry entry = imageRequest.execute();
                    if (entry != null) {
                        mCache.put(imageRequest.getUrl(), entry);
                    }
                }
            }
        }
    }
}
