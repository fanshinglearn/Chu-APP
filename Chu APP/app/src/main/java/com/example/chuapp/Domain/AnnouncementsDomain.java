package com.example.chuapp.Domain;

public class AnnouncementsDomain {
    private String date;
    private String title;
    private String link;

    public AnnouncementsDomain(String date, String title, String link) {
        this.date = date;
        this.title = title;
        this.link = link;
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

    public String getLink() { return link; }

    public void setLink(String link) { this.link = link; }
}
