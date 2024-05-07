package com.example.chuapp.Domain;

import android.net.Uri;

public class RestaurantsDomain {
    private String title;
    private Uri imageUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Uri getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(Uri imageUrl) {
        this.imageUrl = imageUrl;
    }

    public RestaurantsDomain(String title, Uri imageUrl) {
        this.title = title;
        this.imageUrl = imageUrl;
    }
}
