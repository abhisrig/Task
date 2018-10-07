package com.uber.challenge.search;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.uber.challenge.R;
import com.uber.challenge.image.widget.ServerImage;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment implements SearchContract.View, SearchView.OnQueryTextListener {

    private SearchContract.Presenter mPresenter;
    private SearchView mSearchView;
    private RecyclerView mRecyclerView;
    private RecyclerViewAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefresh;
    private ProgressBar mProgressBar;
    private List<ImageData.Photo> mList = new ArrayList<>();
    private String mSearchKeyword = "";

    public static Fragment newInstance() {
        return new SearchFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSearchView = view.findViewById(R.id.search_view);
        mSwipeRefresh = view.findViewById(R.id.swipe_refresh);
        mProgressBar = view.findViewById(R.id.progress_bar);
        mSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.fetchAsync(mSearchKeyword);
            }
        });
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mSearchView.setOnQueryTextListener(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, LinearLayoutManager.VERTICAL
                , false);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new RecyclerViewAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void showEmpty() {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.GONE);
        }
        mSwipeRefresh.setRefreshing(false);
        mList.clear();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public <T> void showServerList(T object, String keyword) {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.GONE);
        }
        mSwipeRefresh.setRefreshing(false);
        if (mSearchKeyword.equals(keyword)) {
            if (object instanceof ImageData && ((ImageData) object).photos != null) {
                mList.clear();
                mList.addAll(((ImageData) object).photos.photo);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public <T> void showLoadMoreServerList(T object, String keyword) {
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.GONE);
        }
        mSwipeRefresh.setRefreshing(false);
        if (mSearchKeyword.equals(keyword)) {
            if (object instanceof ImageData && ((ImageData) object).photos != null) {
                mList.addAll(((ImageData) object).photos.photo);
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showError() {
        mSwipeRefresh.setRefreshing(false);
        if (mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showLoading() {
        mSwipeRefresh.setRefreshing(true);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showLoadMore() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void setPresenter(SearchContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (mPresenter != null) {
            mSearchKeyword = query;
            mPresenter.fetchAsync(query);
            return true;
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (mPresenter != null) {
            mSearchKeyword = newText;
            mPresenter.fetchAsync(newText);
            return true;
        }
        return false;
    }


    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ImageViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grid_image, parent, false));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ImageViewHolder viewHolder = (ImageViewHolder) holder;
            viewHolder.mImageView.bindImage(mList.get(position).getImageUrl());
            if (position == mList.size() - 1) {
                mPresenter.fetchMoreAsync(mSearchKeyword);
            }
        }

        @Override
        public int getItemCount() {
            return mList != null ? mList.size() : 0;
        }

    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        protected ServerImage mImageView;

        public ImageViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image_view);
        }
    }
}
