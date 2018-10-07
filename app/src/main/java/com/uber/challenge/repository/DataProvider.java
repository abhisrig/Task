package com.uber.challenge.repository;

public interface DataProvider {

    public interface ResponseListener<T> {
        public void onResponse(T object);

        public void onError(Exception e);
    }

    public <T> T getDataSync(Query query);

    public <T> void getDataAsync(Query query, ResponseListener<T> listener);
}
