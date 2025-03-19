package com.example.do_an.database;

import com.google.gson.annotations.SerializedName;

public class ImgurResponse {
    @SerializedName("data")
    private ImgurData data;

    public ImgurData getData() {
        return data;
    }

    public void setData(ImgurData data) {
        this.data = data;
    }

    public static class ImgurData {
        @SerializedName("link")
        private String link;

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }
    }
}
