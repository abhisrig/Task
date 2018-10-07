package com.uber.challenge.repository;

/*
 * Contract for different sources of data
 * */
public interface DataProvider {

    public interface ResponseListener<T> {
        public void onStart();

        public void onResponse(T object);

        public void onError(Exception e);
    }

    public <T> T getDataSync(Query query);

    public <T> void getDataAsync(Query query, ResponseListener<T> listener);
}
