package com.uber.challenge.search;

import android.support.annotation.NonNull;

public class SearchRequest {
    String keyword;
    boolean isCanceled;
    int pageNo;
    String url;

    SearchRequest(String keyword, int pageNo, String url) {
        this.keyword = keyword;
        this.pageNo = pageNo;
        this.url = url;
    }

    public void setCanceled(boolean canceled) {
        isCanceled = canceled;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (obj.getClass() != this.getClass())
            return false;
        SearchRequest toCompare = (SearchRequest) obj;
        if (toCompare.pageNo == this.pageNo && toCompare.keyword.equals(this.keyword))
            return true;
        return false;
    }
}
