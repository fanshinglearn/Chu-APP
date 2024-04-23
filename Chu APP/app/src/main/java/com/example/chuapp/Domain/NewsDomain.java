package com.example.chuapp.Domain;

public class NewsDomain {
    private String date;
    private String title;
    private String link;
    private String detail;
    private String imageUrl;

    public NewsDomain(String date, String title, String link, String detail, String imageUrl) {
        this.date = date;
        this.title = title;
        this.link = link;
        this.detail = detail;
        this.imageUrl = imageUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
