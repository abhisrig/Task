package com.uber.challenge;

import android.app.Application;

import com.uber.challenge.image.ImageLoader;

public class AppController extends Application {
    private static AppController sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        ImageLoader.initialize(this, 3);
    }

    public static AppController getInstance() {
        return sInstance;
    }
}
