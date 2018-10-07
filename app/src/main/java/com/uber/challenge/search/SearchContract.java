package com.uber.challenge.search;

import com.uber.challenge.BasePresenter;
import com.uber.challenge.BaseView;

public interface SearchContract {
    interface View extends BaseView<Presenter> {

        void showEmpty();

        <T> void showServerList(T Object, String keyword);

        void showError();

        void showLoading();

        void showLoadMore();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void fetchAsync(String keyword);

        void fetchMoreAsync(String keyword);

    }
}
