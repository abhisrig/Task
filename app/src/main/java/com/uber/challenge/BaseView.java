package com.uber.challenge;

public interface BaseView<T extends BasePresenter> {
    public void setPresenter(T presenter);
}
