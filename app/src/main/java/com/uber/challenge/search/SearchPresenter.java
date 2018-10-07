package com.uber.challenge.search;

import android.text.TextUtils;

import com.uber.challenge.globalconstants.UrlConstants;
import com.uber.challenge.repository.DataProvider;
import com.uber.challenge.repository.NetworkDataProvider;
import com.uber.challenge.repository.Query;

import java.util.HashMap;
import java.util.Map;

public class SearchPresenter implements SearchContract.Presenter {
    private Query mQuery;
    private SearchContract.View mView;
    private int pageNo;
    private SearchRequest mSearchRequest;

    public SearchPresenter(SearchContract.View view) {
        view.setPresenter(this);
        mView = view;
    }

    @Override
    public void fetchAsync(String keyword) {
        pageNo = 0;
        if (mView.isActive()) {
            mView.showLoading();
        }
        fetchData(keyword, false);
    }

    @Override
    public void fetchMoreAsync(final String keyword) {
        pageNo++;
        if (mView.isActive()) {
            mView.showLoadMore();
        }
        fetchData(keyword, true);
    }

    private void fetchData(final String keyword, final boolean isLoadMore) {
        if (!TextUtils.isEmpty(keyword)) {
            if (mQuery == null) {
                Query.Builder builder = new Query.Builder();
                builder.pageNo(pageNo);
                builder.path(UrlConstants.BASE_URL_FLICKR_API);
                Map<String, String> map = new HashMap<>();
                map.put("method", "flickr.photos.search");
                map.put("format", "json");
                map.put("nojsoncallback", "1");
                map.put("safe_search", "1");
                map.put("page", String.valueOf(pageNo));
                map.put("api_key", "3e7cc266ae2b0e0d78e279ce8e361736");
                map.put("text", keyword);
                builder.addExtraParams(map);
                mQuery = builder.build();
            } else {
                mQuery.getParamsMap().put("page", String.valueOf(pageNo));
                mQuery.getParamsMap().put("text", keyword);
            }
            NetworkDataProvider.INSTANCE.getDataAsync(mQuery, new DataProvider.ResponseListener<ImageData>() {
                private String url;

                @Override
                public void onStart() {
                    url = mQuery.build();
                }

                @Override
                public void onResponse(ImageData object) {
                    if (url.equals(mQuery.build()) && mView.isActive()) {
                        if (!isLoadMore)
                            mView.showServerList(object, keyword);
                        else
                            mView.showLoadMoreServerList(object, keyword);
                    }
                }

                @Override
                public void onError(Exception e) {
                    if (url.equals(mQuery.build()) && mView.isActive()) {
                        mView.showError();
                    }
                }
            });
        } else {
            if (mView.isActive())
                mView.showEmpty();
        }
    }

    @Override
    public void start() {

    }
}
