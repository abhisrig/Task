package com.uber.challenge.repository;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Map;

/*
    Helper class to represent different type of queries
 * */
public class Query {

    private static final String DEFAULT_PARAMS_ENCODING = "UTF-8";
    private String mPath;
    private int pageNo;
    private int limit;
    private boolean fetchFresh;
    private boolean shouldCache;
    private Class<?> className;
    private Map<String, String> extraParams;

    private Query(Builder builder) {
        mPath = builder.mPath;
        pageNo = builder.pageNo;
        limit = builder.limit;
        fetchFresh = builder.fetchFresh;
        shouldCache = builder.shouldCache;
        extraParams = builder.extraParams;
        className = builder.className;
    }

    public Map<String, String> getParamsMap() {
        return extraParams;
    }

    public String build() {
        StringBuilder sb = new StringBuilder();
        sb.append(mPath).append("?");
        if (extraParams != null) {
            try {
                for (Map.Entry<String, String> entry : extraParams.entrySet()) {
                    sb.append(URLEncoder.encode(entry.getKey(), DEFAULT_PARAMS_ENCODING));
                    sb.append('=');
                    sb.append(URLEncoder.encode(entry.getValue(), DEFAULT_PARAMS_ENCODING));
                    sb.append('&');
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static class Builder {
        private String mPath;
        private int pageNo = -1;
        private int limit = -1;
        private boolean fetchFresh;
        private boolean shouldCache;
        private Map<String, String> extraParams;
        private Class<?> className;

        public Builder path(String path) {
            this.mPath = path;
            return this;
        }

        public Builder pageNo(int offset) {
            this.pageNo = offset;
            return this;
        }

        public Builder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public Builder fetchFresh(boolean value) {
            this.fetchFresh = value;
            return this;
        }

        public Builder shouldCache(boolean shouldCache) {
            this.shouldCache = shouldCache;
            return this;
        }

        public Builder addExtraParams(Map<String, String> extraParams) {
            this.extraParams = extraParams;
            return this;
        }

        public Builder putExtraParams(Map.Entry<String, String> entry) {
            if (extraParams == null)
                extraParams = Collections.emptyMap();
            extraParams.put(entry.getKey(), entry.getValue());
            return this;
        }

        public <T> Builder className(Class<T> tClass) {
            className = tClass;
            return this;
        }

        public Query build() {
            return new Query(this);
        }
    }
}
