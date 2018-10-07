package com.uber.challenge.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ImageData {

    @SerializedName("photos")
    @Expose
    public Photos photos;
    @SerializedName("stat")
    @Expose
    public String stat;

    @Override
    public String toString() {
        return "photos # " + photos + " stat # " + stat;
    }

    public static class Photo {

        @SerializedName("id")
        @Expose
        public String id;
        @SerializedName("owner")
        @Expose
        public String owner;
        @SerializedName("secret")
        @Expose
        public String secret;
        @SerializedName("server")
        @Expose
        public String server;
        @SerializedName("farm")
        @Expose
        public int farm;
        @SerializedName("title")
        @Expose
        public String title;
        @SerializedName("ispublic")
        @Expose
        public int ispublic;
        @SerializedName("isfriend")
        @Expose
        public int isfriend;
        @SerializedName("isfamily")
        @Expose
        public int isfamily;

        @Override
        public String toString() {
            return "id # " + id + " owner # " + owner + " secret # " + secret + " server # " + server +
                    " farm # " + farm + " title # " + title + " ispublic # " + ispublic + " isfriend # " +
                    isfriend + " isfamily # " + isfamily;
        }

        public String getImageUrl() {
            return "http://farm" + farm + ".static.flickr.com/" + server + "/" + id + "_" + secret + ".jpg";
        }

    }

    public static class Photos {

        @SerializedName("page")
        @Expose
        public int page;
        @SerializedName("pages")
        @Expose
        public int pages;
        @SerializedName("perpage")
        @Expose
        public int perpage;
        @SerializedName("total")
        @Expose
        public String total;
        @SerializedName("photo")
        @Expose
        public List<Photo> photo = null;

        @Override
        public String toString() {
            return "page # " + page + " pages # " + pages + " perpage # " + perpage + " total # " + total +
                    " photo # " + photo;
        }
    }
}