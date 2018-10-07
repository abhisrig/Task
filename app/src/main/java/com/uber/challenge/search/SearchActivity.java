package com.uber.challenge.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.uber.challenge.R;

public class SearchActivity extends AppCompatActivity {

    private SearchPresenter mSearchPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        SearchFragment searchFragment = (SearchFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);
        if (searchFragment == null) {
            searchFragment = (SearchFragment) SearchFragment.newInstance();
            bindFragmentToActivity(searchFragment);
        }
        mSearchPresenter = new SearchPresenter(searchFragment);
    }

    private void bindFragmentToActivity(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName()).commitAllowingStateLoss();
    }
}
