package com.uber.challenge.image;

public interface Request {
    public void cancel(String url);

    public void execute();
}
