package com.uber.challenge.image;

import java.util.concurrent.BlockingQueue;

public class ImageRequestThread extends Thread {
    private BlockingQueue<ImageRequest> processingQueue;

    public ImageRequestThread(BlockingQueue<ImageRequest> queue) {
        processingQueue = queue;
    }

    @Override
    public void run() {
        while (true) {
            if (!processingQueue.isEmpty()) {
                ImageRequest imageRequest = processingQueue.poll();
                if (imageRequest != null) {
                    imageRequest.execute();
                }
            }
        }
    }
}