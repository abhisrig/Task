package com.uber.challenge.image;

import com.uber.challenge.image.cache.Cache;

import java.util.concurrent.BlockingQueue;

public class CacheRequestThread extends Thread {
    private BlockingQueue<ImageRequest> cacheQueue;
    private BlockingQueue<ImageRequest> networkQueue;
    private Cache mCache;

    public CacheRequestThread(Cache cache, BlockingQueue<ImageRequest> cacheQueue, BlockingQueue<ImageRequest> nwQueue) {
        this.cacheQueue = cacheQueue;
        this.networkQueue = nwQueue;
        this.mCache = cache;
    }

    @Override
    public void run() {
        mCache.initialize();
        while (true) {
            try {
                if (!cacheQueue.isEmpty()) {
                    ImageRequest request = cacheQueue.poll();
                    if (request != null) {
                        Cache.Entry entry = mCache.get(request.getUrl());
                        if (entry == null || entry.isExpired()) {
                            networkQueue.put(request);
                        } else {
                            request.deliverFromCache(entry.data);
                        }
                    }
                }
            } catch (InterruptedException e) {
            }
        }
    }
}

