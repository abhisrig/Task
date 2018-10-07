package com.uber.challenge.globalconstants;

public class UrlConstants {
    private UrlConstants() {
        throw new IllegalStateException("Can not be instantiated");
    }

    public static final String BASE_URL_FLICKR_API = "https://api.flickr.com/services/rest/";
    public static final String GET_URL_SEARCH = BASE_URL_FLICKR_API +
            "?method=flickr.photos.search&format=json&nojsoncallback=1&safe_search=1&&page=2&api_key=3e7cc266ae2b0e0d78e279ce8e361736&text=kittens";
}
